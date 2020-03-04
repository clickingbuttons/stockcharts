import models.OHLCV;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class CandlestickChart extends JPanel {
    List<OHLCV> candleSticks;

    public CandlestickChart(List<OHLCV> candleSticks) {
        super();
        this.candleSticks = candleSticks;
    }

    public CandlestickChart() {
        super();
        this.candleSticks = candleSticks;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Dimension size = getSize();
        Insets insets = getInsets();

        double width = size.width - insets.left - insets.right;
        double height = size.height - insets.top - insets.bottom;
        double candlestickWidth = width / candleSticks.size();
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
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 300);
    }
}