import java.awt.*;
import javax.swing.*;

public class SuperYatsuoWorld extends JFrame {
    public static void main(String[] args) {
        new SuperYatsuoWorld();
    }

    /**
     * ActionGame のコンストラクタ
     */
    public SuperYatsuoWorld() {
        setTitle("Super Yatsuo World");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // サイズ変更禁止
        setResizable(false);

        MainPanel panel = new MainPanel();
        Container c = getContentPane();
        c.add(panel);
        
        //メインパネルのサイズに合わせる
        pack();
        
        setVisible(true);
    }
}
