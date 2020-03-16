import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;

public class ChartsFrame extends JFrame  {
    JPanel charts = new JPanel();
    JPanel toolbar = new JPanel();
    Instant from = Instant.parse("2015-01-01T04:00:00Z");
    Instant to = Instant.parse("2015-01-01T10:30:00Z");
    JButton zoomIn = new JButton("+");
    JButton zoomOut = new JButton("-");
    String[] symbols = {"AAPL", "AMZN", "MSFT", "TSLA", "SPY", "GDR", "GE"};

    public ChartsFrame(String name) {
        super(name);
        toolbar.add(zoomOut);
        toolbar.add(zoomIn);
        zoomOut.setPreferredSize(new Dimension(24, zoomOut.getPreferredSize().height));
        zoomIn.setPreferredSize(new Dimension(24, zoomIn.getPreferredSize().height));
        zoomIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Component c : charts.getComponents()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        ((ChartPanel) c).zoomChartReset();
                    }
                    else {
                        ((ChartPanel) c).zoomChartIn();
                    }
                }
            }
        });
        zoomOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Component c : charts.getComponents()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        ((ChartPanel) c).zoomChartReset();
                    }
                    else {
                        ((ChartPanel) c).zoomChartOut();
                    }
                }
            }
        });
        for (String symbol : symbols) {
            charts.add(new ChartPanel(symbol, from, to, "Minute"));
        }
        charts.setLayout(new GridLayout(0,1));
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
        mainPane.add(toolbar);
        JScrollPane pane = new JScrollPane(charts, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Scrollbar padding = 12
        pane.setPreferredSize(new Dimension(pane.getPreferredSize().width + 12, pane.getPreferredSize().height));
        mainPane.add(pane);
        this.add(mainPane);
    }
}
