import models.OHLCV;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChartPanel extends JPanel {
    static final String aggLevels[] = {"1m", "1d"};

    JPanel controlsPanel = new JPanel();
    JTextField symbolInput;
    JTextField fromInput;
    JTextField toInput;
    JComboBox aggSelect = new JComboBox(aggLevels);
    JButton zoomIn = new JButton("+");
    JButton zoomOut = new JButton("-");
    CandlestickChart chart;

    List<OHLCV> generateCandlestickData() {
        List<OHLCV> res = new ArrayList<>();

        Instant from = Instant.parse(fromInput.getText());
        Instant to = Instant.parse(toInput.getText());
        for (long it = from.toEpochMilli(); it < to.toEpochMilli(); it+=60000) {
            OHLCV candle = new OHLCV(it);
            candle.open = ThreadLocalRandom.current().nextInt(180, 200 + 1);
            candle.close = ThreadLocalRandom.current().nextInt(180, 200 + 1);
            candle.high = ThreadLocalRandom.current().nextInt((int) Math.max(candle.open, candle.close), 200 + 1);
            candle.low = ThreadLocalRandom.current().nextInt(180, (int) Math.min(candle.open, candle.close) + 1);
            candle.volume = ThreadLocalRandom.current().nextInt(100, 10000 + 1);
            res.add(candle);
        }

        return res;
    }

    public ChartPanel(String symbol, Instant from, Instant to, String aggLevel) {
        super();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.add(symbolInput = new JTextField(symbol));
        symbolInput.setColumns(5);
        symbolInput.setMaximumSize(new Dimension(symbolInput.getPreferredSize().width, symbolInput.getMaximumSize().height));
        controlsPanel.add(fromInput = new JTextField(from.toString()));
        controlsPanel.add(toInput = new JTextField(to.toString()));
        aggSelect.setSelectedItem(aggLevel);
        controlsPanel.add(aggSelect);
        controlsPanel.add(zoomIn);
        controlsPanel.add(zoomOut);
        controlsPanel.setMaximumSize(new Dimension(10000, 50));
        chart = new CandlestickChart(generateCandlestickData());
        zoomOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    chart.zoomReset();
                } else {
                    chart.zoomOut();
                }
            }
        });
        zoomIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    chart.zoomReset();
                } else {
                    chart.zoomIn();
                }
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(true);
        this.setDoubleBuffered(true);
        this.add(controlsPanel);
        this.add(chart);
    }

    public void zoomChartIn() {
        chart.zoomIn();
    }

    public void zoomChartOut() {
        chart.zoomOut();
    }

    public void zoomChartReset() {
        chart.zoomReset();
    }
}
