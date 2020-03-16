import models.OHLCV;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

class CandlestickChart extends JPanel implements MouseListener, MouseMotionListener {
    List<OHLCV> candleSticks;
    double zoom = 1;
    double candlestickWidth;
    double firstClickX;
    double dragX;

    public CandlestickChart(List<OHLCV> candleSticks) {
        super();
        this.candleSticks = candleSticks;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    private double getChartWidth() {
        Dimension size = this.getSize();
        Insets insets = this.getInsets();

        return size.width * zoom - insets.left - insets.right;
    }

    private double getChartHeight() {
        Dimension size = this.getSize();
        Insets insets = this.getInsets();

        return size.height - insets.top - insets.bottom;
    }

    public void zoomTo(double fromX , double toX) {
        zoom = this.getChartWidth() / Math.abs(fromX - toX);
        firstClickX = 0;
        dragX = 0;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        double width = this.getChartWidth();
        double height = this.getChartHeight();

        this.candlestickWidth = width / candleSticks.size();
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (OHLCV candle : candleSticks) {
            if (candle.high > max) {
                max = candle.high;
            }
            if (candle.low < min) {
                min = candle.low;
            }
        }
        double heightScale = height / (max - min);

        for (int i = 0; i < candleSticks.size(); i++) {
            OHLCV candlestick = candleSticks.get(i);
            double yStart, candleHeight;
            if (candlestick.close > candlestick.open) {
                g2d.setColor(Color.green);
                yStart = candlestick.close;
                candleHeight = (candlestick.close - candlestick.open) * heightScale;
            }
            else if (candlestick.close < candlestick.open) {
                g2d.setColor(Color.red);
                yStart = candlestick.open;
                candleHeight = (candlestick.open - candlestick.close) * heightScale;
            }
            else {
                g2d.setColor(Color.black);
                yStart = candlestick.open;
                candleHeight = 2;
            }
            g2d.drawLine(
                    (int) (i * candlestickWidth + candlestickWidth / 2 - 1),
                    (int) ((max - candlestick.low)  * heightScale),
                    (int) (i * candlestickWidth + candlestickWidth / 2 - 1),
                    (int) ((max - candlestick.high) * heightScale)
            );
            g2d.fillRect(
                    (int) (i * candlestickWidth),
                    (int) ((max - yStart) * heightScale),
                    (int) candlestickWidth - 1,
                    (int) candleHeight
            );
        }

        g2d.setColor(new Color(15, 15, 15, 100));
        g2d.fillRect(
                (int) Math.min(this.firstClickX, this.dragX),
                0,
                (int) Math.abs(this.firstClickX - this.dragX),
                (int) this.getChartHeight()
        );
    }

    public void zoomIn() {
        zoom += 0.5;
        this.repaint();
    }

    public void zoomOut() {
        zoom -= 0.5;
        this.repaint();
    }

    public void zoomReset() {
        zoom = 1;
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 100);
    }

    @Override
    public void mousePressed(MouseEvent ev) {
        firstClickX = ev.getX();
    }

    @Override
    public void mouseReleased(MouseEvent ev) {
        this.zoomTo(firstClickX, ev.getX());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragX = e.getX();
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}