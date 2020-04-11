import models.Range;
import models.Trace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Chart extends JPanel implements ComponentListener, MouseListener, MouseMotionListener {
    protected static final int legendHeight = 20;
    protected static final int legendWidth = 50;
    private Dimension dimension;
    protected Range<Long> viewDomainPanStart;
    protected Range<Long> viewDomain;
    protected Range<Double> viewRange;
    protected Point mouse = new Point(0, 0);
    private Point click;
    private Point2D.Double viewClick;
    private Point drag;
    private boolean isZooming = false;
    private Trace trace;
    protected boolean showCrosshair = false;
    protected long frequency;

    protected Range<Long> domain;
    protected Range<Double> range = new Range<>(Double.MAX_VALUE, Double.MIN_VALUE);
    protected DateTimeFormatter formatter;
    protected DateTimeFormatter detailFormatter;

    public Chart() {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
    }

    protected int getChartWidth() {
        Dimension size = getSize();
        Insets insets = getInsets();

        return size.width - insets.left - insets.right - legendWidth;
    }

    protected int getChartHeight() {
        Dimension size = getSize();
        Insets insets = getInsets();

        return size.height - insets.top - insets.bottom - legendHeight;
    }

    public void zoomTo(long from, long to) {
        long left = Math.max(Math.min(from, to), domain.min);
        long right = Math.min(Math.max(from, to), domain.max);

        viewDomain.setMin(left);
        viewDomain.setMax(right);
        resetChart();
    }

    public void zoomIn() {
        long offset = (viewDomain.max - viewDomain.min) / 4;
        viewDomain.setMin(viewDomain.min + offset);
        viewDomain.setMax(viewDomain.max - offset);
        repaint();
    }

    public void zoomOut() {
        long offset = (viewDomain.max - viewDomain.min) / 4;
        viewDomain.setMin(viewDomain.min - offset);
        viewDomain.setMax(viewDomain.max + offset);
        repaint();
    }

    public void zoomReset() {
        viewDomain.setMin(domain.min);
        viewDomain.setMax(domain.max);
        repaint();
    }

    public void resetChart() {
        isZooming = false;
        trace = null;
        repaint();
    }

    private void paintXLegendTick(Graphics2D g2d, long tickMicros, DateTimeFormatter formatter, boolean invert) {
        int maxLegendWidth = g2d.getFontMetrics().stringWidth(formatter.format(Instant.ofEpochMilli(domain.min)));
        int x = (int) ((tickMicros - viewDomain.min) / viewDomain.getRange() * getChartWidth());

        if (invert) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(x - maxLegendWidth / 2, getChartHeight(), maxLegendWidth,  legendHeight);
        }
        g2d.setColor(invert ? Color.WHITE : Color.BLACK);
        g2d.drawLine(
                x,
                getChartHeight(),
                x,
                getChartHeight() + 4
        );
        g2d.drawString(
                formatter.format(Instant.ofEpochMilli(tickMicros)),
                x - maxLegendWidth / 2,
                getChartHeight() + getFont().getSize() + 6
        );
    }

    protected void paintXLegend(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(
                0,
                getChartHeight(),
                getChartWidth(),
                legendHeight
        );
        g2d.setColor(Color.BLACK);
        int maxLegendWidth = g2d.getFontMetrics().stringWidth(formatter.format(Instant.ofEpochMilli(domain.min)));
        int numTicks = getChartWidth() / maxLegendWidth / 2;
        long tickIncrement = Math.round(Math.max(viewDomain.getRange() / numTicks, frequency) / frequency) * frequency;
        for (long tickMicros = domain.min / frequency * frequency; tickMicros < viewDomain.max; tickMicros += tickIncrement) {
            if (tickMicros < viewDomain.min) {
                continue;
            }

            paintXLegendTick(g2d, tickMicros, formatter, false);
        }
        g2d.drawLine(
                0,
                getChartHeight(),
                getChartWidth(),
                getChartHeight()
        );
    }

    private void paintYLegendTick(Graphics2D g2d, double price, boolean invert) {
        int y = (int) (getChartHeight() - (price - viewRange.min) / viewRange.getRange() * getChartHeight());

        if (invert) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(getChartWidth(), y - getFont().getSize() / 2 + 1, legendWidth,  getFont().getSize());
        }
        g2d.setColor(invert ? Color.WHITE : Color.BLACK);
        g2d.drawLine(
                getChartWidth(),
                y,
                getChartWidth() + 4,
                y
        );
        String legendVal = String.format("%.2f", price);
        g2d.drawString(
                legendVal,
                getChartWidth() + 6,
                y + getFont().getSize() / 2 - 1
        );
    }

    protected void paintYLegend(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(
                getChartWidth(),
                0,
                legendWidth,
                getChartHeight()
        );
        g2d.setColor(Color.BLACK);
        int numTicks = getChartHeight() / getFont().getSize() / 3;
        double tickIncrement = (range.max - range.min) / numTicks;
        for (int i = 0; i < numTicks + 1; i++) {
            paintYLegendTick(g2d, range.min + i * tickIncrement, false);
        }
        g2d.drawLine(
                getChartWidth(),
                0,
                getChartWidth(),
                getChartHeight()
        );
    }

    private void paintTraceLabel(Graphics2D g2d, String label, Point point, boolean isOffset) {
        int labelWidth = g2d.getFontMetrics().stringWidth(label);
        int xOffset = isOffset ? -labelWidth - 12 : 12;
        g2d.drawString(label, point.x + xOffset, Math.max(point.y, g2d.getFont().getSize()));
    }

    protected void paintOverlay(Graphics2D g2d) {
        // Zoom drag
        if (isZooming) {
            g2d.setColor(new Color(15, 15, 15, 100));
            g2d.fillRect(
                    (int) Math.min(click.getX(), drag.getX()),
                    0,
                    (int) Math.abs(click.getX() - Math.min(drag.getX(), getChartWidth())),
                    getChartHeight()
            );
        }
        // Crosshair
        if (showCrosshair) {
            if (mouse.x < getChartWidth()) {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(mouse.x - 1, 0, mouse.x - 1, getChartHeight());
                paintXLegendTick(g2d, getMouseDomain(), detailFormatter, true);
            }
            if (mouse.y < getChartHeight()) {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(0, mouse.y, getChartWidth(), mouse.y);
                paintYLegendTick(g2d, getMouseRange(), true);
            }
        }
        // Trace
        if (trace != null) {
            Graphics2D trace2d = (Graphics2D) g2d.create();
            trace2d.setFont(new Font("default", Font.BOLD, 16));
            trace2d.setStroke(new BasicStroke(4));
            trace2d.setColor(Color.BLACK);
            trace2d.drawLine(trace.start.x, trace.start.y, trace.end.x, trace.end.y);

            String startLabel = String.format("%3.2f", trace.startValue.y);
            paintTraceLabel(trace2d, startLabel, trace.start, trace.endValue.x > trace.startValue.x);
            double percentDiff =  (trace.endValue.y - trace.startValue.y) * 100.0 / trace.startValue.y;
            String endLabel = String.format("%3.2f (%2.2f%%)", trace.endValue.y, percentDiff);
            paintTraceLabel(trace2d, endLabel, trace.end, trace.endValue.x < trace.startValue.x);
        }
    }

    protected long getMouseDomain() {
        return (long) (viewDomain.min + (mouse.getX() / getChartWidth()) * viewDomain.getRange());
    }

    private double getMouseRange() {
        return viewRange.max - (mouse.getY() / getChartHeight()) * viewRange.getRange();
    }

    protected void paintDebug(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getChartWidth(), 14);
        g2d.setColor(Color.BLACK);
        g2d.drawString(
                String.format(
                        "%04d,%04d=%s,%04.2f view: %s,%s,%04.2f,%04.2f",
                        mouse.x, mouse.y,
                        detailFormatter.format(Instant.ofEpochMilli(getMouseDomain())), getMouseRange(),
                        detailFormatter.format(Instant.ofEpochMilli(viewDomain.min)), detailFormatter.format(Instant.ofEpochMilli(viewDomain.max)),
                        viewRange.min, viewRange.max),
                0,
                12
        );
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        Dimension newDimensions = new Dimension(getWidth(), getHeight());
        boolean hasResized = !newDimensions.equals(dimension);
        if (dimension == null || hasResized) {
            dimension = newDimensions;
            viewDomain.setMin(domain.min);
            viewDomain.setMax(domain.max);
            viewRange.setMin(range.min);
            viewRange.setMax(range.max);
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 100);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(500, 400);
    }

    @Override
    public void mousePressed(MouseEvent ev) {
        click = ev.getPoint();
        viewClick = new Point2D.Double(getMouseDomain(), getMouseRange());
        viewDomainPanStart = new Range<>(viewDomain.min, viewDomain.max);
        if (SwingUtilities.isMiddleMouseButton(ev)) {
            trace = new Trace(ev.getPoint(), new Point2D.Double(getMouseDomain(), getMouseRange()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent ev) {
        // Zoom to region
        if (SwingUtilities.isRightMouseButton(ev)) {
            if (click != null && Math.abs(ev.getX() - click.getX()) > 20) {
                zoomTo((long) viewClick.x, getMouseDomain());
            } else {
                resetChart();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent ev) {
        if (ev.getClickCount() == 2 && SwingUtilities.isRightMouseButton(ev)) {
            zoomReset();
        }
    }

    @Override
    public void mouseDragged(MouseEvent ev) {
        mouse = ev.getPoint();
        // Pan
        if (SwingUtilities.isLeftMouseButton(ev)) {
            long panStartMouseDomain = (long) (viewDomainPanStart.min + (mouse.getX() / getChartWidth()) * viewDomainPanStart.getRange());
            long offset = (long) (viewClick.x - panStartMouseDomain);
            viewDomain.setMin(viewDomainPanStart.min + offset);
            viewDomain.setMax(viewDomainPanStart.max + offset);
            repaint();
        }
        // Zoom to region
        else if (SwingUtilities.isRightMouseButton(ev)) {
            drag = new Point(ev.getX(), ev.getY());
            isZooming = true;
            repaint();
        }
        // Trace
        else if (SwingUtilities.isMiddleMouseButton(ev)) {
            trace.end = new Point(mouse.x, mouse.y);
            trace.endValue = new Point2D.Double(getMouseDomain(), getMouseRange());
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent ev) {
        mouse = ev.getPoint();
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent ev) {
        showCrosshair = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent ev) {
        showCrosshair = false;
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {}

    @Override
    public void componentShown(ComponentEvent componentEvent) {}

    @Override
    public void componentHidden(ComponentEvent componentEvent) {}
}
