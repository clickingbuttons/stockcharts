import javax.swing.SwingUtilities;
import javax.swing.JFrame;

public class Main {
    private static void createAndShowGUI() {
        ChartsFrame f = new ChartsFrame("Charts Frame");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setResizable(true);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}