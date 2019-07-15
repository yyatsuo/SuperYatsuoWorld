import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Ex_13_1801015138 extends JFrame {
    public static void main(String[] args) {
        new Ex_13_1801015138();
    }

    /**
     * ActionGame のコンストラクタ
     */
    public Ex_13_1801015138() {
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
