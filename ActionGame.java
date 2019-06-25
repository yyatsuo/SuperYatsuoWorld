import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ActionGame extends JFrame {
    public static void main(String[] args) {
        new ActionGame();
    }

    /**
     * ActionGame のコンストラクタ
     */
    public ActionGame() {
        setTitle("Super Yatsuo World");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        MainPanel panel = new MainPanel();
        Container c = getContentPane();
        c.add(panel);
        pack();
        setVisible(true);
    }
}
