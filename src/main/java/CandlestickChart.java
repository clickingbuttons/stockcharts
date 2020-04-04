import models.OHLCV;
import models.Range;

import java.awt.*;
import java.util.List;

class CandlestickChart extends Chart {
    private List<OHLCV> candleSticks;
    private double numCandles;

    public CandlestickChart(List<OHLCV> candleSticks) {
        super();
        this.candleSticks = candleSticks;
        range = new Range<>(Double.MAX_VALUE, Double.MIN_VALUE);
        for (OHLCV candle : candleSticks) {
            if (candle.high > range.max) {
                range.setMax(candle.high);
            }
            if (candle.low < range.min) {
                range.setMin(candle.low);
            }
        }
        domain = new Range<>(candleSticks.get(0).timeMicros, candleSticks.get(candleSticks.size() - 1).timeMicros + 60000);
        viewDomain = new Range<>(domain.min, domain.max);
        viewRange = new Range<>(range.min, range.max);
        numCandles = domain.getRange() / 60000;
    }

    private void paintCandles(Graphics2D g2d) {
        double candlestickWidth = (double) getChartWidth() / numCandles * (domain.getRange() / viewDomain.getRange());
        double candlestickHeight = getChartHeight() / viewRange.getRange();

        for (OHLCV candlestick : candleSticks) {
            double x = (candlestick.timeMicros - viewDomain.min) / 60000F;
//            if (x < fromX - candlestickWidth || x > toX + candlestickWidth) {
//                continue;
//            }
            double yStart, candleHeight;
            if (candlestick.close > candlestick.open) {
                g2d.setColor(new Color(24, 140, 32));
                yStart = candlestick.close;
                candleHeight = (candlestick.close - candlestick.open);
            }
            else if (candlestick.close < candlestick.open) {
                g2d.setColor(Color.red);
                yStart = candlestick.open;
                candleHeight = (candlestick.open - candlestick.close) ;
            }
            else {
                g2d.setColor(Color.black);
                yStart = candlestick.open;
                candleHeight = 2;
            }
            final double lineX = x * candlestickWidth + candlestickWidth / 2;
            g2d.drawLine(
                    (int) lineX - 1,
                    (int) ((range.max - candlestick.low)  * candlestickHeight),
                    (int) lineX - 1,
                    (int) ((range.max - candlestick.high) * candlestickHeight)
            );
            g2d.fillRect(
                    (int) (x * candlestickWidth),
                    (int) ((range.max - yStart) * candlestickHeight),
                    (int) candlestickWidth - 1,
                    (int) (candleHeight * candlestickHeight)
            );
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        paintCandles(g2d);
        paintXLegend(g2d);
        paintYLegend(g2d);
        paintOverlay(g2d);
        paintDebug(g2d);
    }
}