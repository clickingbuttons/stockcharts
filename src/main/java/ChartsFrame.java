import javax.swing.*;
import java.awt.*;
import java.time.Instant;

public class ChartsFrame extends JFrame  {
    GridLayout chartsLayout = new GridLayout(0,1);
    Instant from = Instant.parse("2015-01-01T04:00:00Z");
    Instant to = Instant.parse("2015-01-01T10:30:00Z");

    public ChartsFrame(String name) {
        super(name);
        this.setLayout(chartsLayout);
        this.add(new ChartPanel("AAPL", from, to, "Minute"));
        this.add(new ChartPanel("AMZN", from, to, "Minute"));
        this.add(new ChartPanel("MSFT", from, to, "Minute"));
        this.add(new ChartPanel("TSLA", from, to, "Minute"));
    }
}
