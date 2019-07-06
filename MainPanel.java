import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * メインパネルの実装
 */
public class MainPanel extends JPanel implements KeyListener, Runnable{

    // パネルのサイズ
    public static final int WIDTH = 640;
    public static final int HEIGHT = 420;
    
    private Player player;
    private Field field;
    private Thread mainLoop;
    private Font myFont;

    public MainPanel() {
        // パネルサイズの設定
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // 背景色の設定
        setBackground(Color.black);

        // フォントの設定
        myFont = new Font("Lucida Sans", Font.BOLD, 20);

        // キーイベントを取れるようにする
        setFocusable(true);

        // フィールドの初期化
        field = new Field();

        // プレイヤーの初期化
        player = new Player(field);

        // キーリスナーの登録
        addKeyListener(this);

        // スレッド開始
        mainLoop = new Thread(this);
        mainLoop.start();
    }

    /**
     * キーリスナー
     * @param e キーイベント
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
            case KeyEvent.VK_LEFT:
                // 左移動
                player.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                // 右移動
                player.moveRight();
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_UP:
                // ジャンプ
                player.jump();
                break;
        }
    }

    /**
     * 左右キーが離されたらプレイヤーの動作を止める
     * @param e キーイベント
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                player.stop();
        }
    }

    /**
     * Nothing todo
     * @param e キーイベント
     */
    public void keyTyped(KeyEvent e) {
        // Nothing todo
    }

    /**
     * ゲームのメインループ
     */
    public void run() {
        for(;;) {
            try {
                // だいたい50fpsくらいで画面更新
                repaint();
                Thread.sleep(20);
            } catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }
    
    /**
     * 描画処理
     * @param g グラフィックスオブジェクト
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // X方向のオフセット計算
        int offsetX = player.getX() - WIDTH/2;
        if(offsetX < 0) {
            offsetX = 0;
        } else if(offsetX > field.getWidthInPx() - WIDTH) {
            offsetX = field.getWidthInPx() - WIDTH;
        }

        // Y方向のオフセット計算
        int offsetY = player.getY() - HEIGHT/2;
        if(offsetY < 0) {
            offsetY = 0;
        } else if(offsetY > field.getHeightInPx() - HEIGHT) {
            offsetY = field.getHeightInPx() - HEIGHT;
        }

        // field の描画
        field.draw(g, offsetX, offsetY);

        // Playerの描画
        player.draw(g, offsetX, offsetY);

        // 残りライフと残り時間の描画
        g.setFont(myFont);
        FontMetrics fm = g.getFontMetrics(myFont);
        g.setColor(Color.white);
        g.drawString("Time:" + Integer.toString(field.getTimeRemained()), 0, HEIGHT);
        g.drawString("Life: x" + Integer.toString(player.getLife()), 0, fm.getHeight());
    }
}
