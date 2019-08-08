import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 * メインパネルの実装
 */
public class MainPanel extends JPanel implements KeyListener, Runnable{

    // パネルのサイズ
    public static final int WIDTH = 640;
    public static final int HEIGHT = 420;
    private static final String scoreFile = "data/score.dat";    
    private Player player;
    private Field field;
    private Thread mainLoop;
    private Font myFont;
    private Image imgTitle, imgHeart, imgWing, imgSpeed, imgKinoko;
    private boolean running;
    private Scores myScores;
    private int score, highlight;

    public MainPanel() {
        // パネルサイズの設定
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // 背景色の設定
        setBackground(Color.DARK_GRAY);

        // フォントの設定
        myFont = new Font("Lucida Sans", Font.BOLD, 20);

        // キーイベントを取れるようにする
        setFocusable(true);
        
        // キーリスナーの登録
        addKeyListener(this);

        // ゲーム開始待ち
        running = false;

        // Titleイメージ読み込み
        imgTitle = getImg("image/title.png");

        // ベストスコアの読み込み
        myScores = loadScore();

        // スコアハイライト位置の初期化
        highlight = -1;
    }

    public Scores loadScore() {
        Scores s;
        try {
            ObjectInputStream istream = new ObjectInputStream( new FileInputStream(scoreFile) );
            s = (Scores)istream.readObject();
            istream.close();
        } catch(Exception e) {
            e.printStackTrace();
            s = null;
        }
        return s;
    }

    public void saveScore() {
        try {
            ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(scoreFile));
            ostream.writeObject(myScores);
            ostream.flush();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameStart() {
        // 画像の設定
        imgHeart = getImg("image/heart.png");
        imgWing = getImg("image/wing.png");
        imgSpeed = getImg("image/speed.png");
        imgKinoko = getImg("image/kinoko.png");

        // フィールドの初期化
        field = new Field();

        // プレイヤーの初期化
        player = new Player(field);

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
        if(running==false) {
            running = true;
            gameStart();
        }
    }

    /**
     * ゲームのメインループ
     */
    public void run() {
        while(running) {
            try {
                // だいたい50fpsくらいで画面更新
                repaint();
                Thread.sleep(20);
            } catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
        repaint();
    }
    
    /**
     * 描画処理
     * @param g グラフィックスオブジェクト
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(myFont);
        FontMetrics fm = g.getFontMetrics(myFont);

        if (running) {
            // X方向のオフセット計算
            int offsetX = player.getX() - WIDTH / 2;
            if (offsetX < 0) {
                offsetX = 0;
            } else if (offsetX > field.getWidthInPx() - WIDTH) {
                offsetX = field.getWidthInPx() - WIDTH;
            }

            // Y方向のオフセット計算
            int offsetY = player.getY() - HEIGHT / 2;
            if (offsetY < 0) {
                offsetY = 0;
            } else if (offsetY > field.getHeightInPx() - HEIGHT) {
                offsetY = field.getHeightInPx() - HEIGHT;
            }

            // field の描画
            field.update(g, offsetX, offsetY);

            // Playerの描画
            int state = player.update(g, offsetX, offsetY);
            if(state == Player.GAMEOVER) {
                score = 0;
                highlight = -1;
                running = false;
            } else if(state == Player.CLEAR) {
                // 最終スコア集計&登録
                score = field.getScore();
                score += field.getTimeRemained()*10;
                score += player.getLife()*100;
                highlight = myScores.insertScore(score);
                saveScore();
                running = false;
            }

            // 残りライフと残り時間の描画
            g.setColor(Color.white);
            g.drawString("Time:" + Integer.toString(field.getTimeRemained()), 0, HEIGHT);
            g.drawImage(imgHeart, 0, 0, this);
            g.drawString("x" + Integer.toString(player.getLife()), imgHeart.getWidth(this), fm.getHeight());
            g.drawString("Score: " + Integer.toString(field.getScore()), WIDTH - 150, fm.getHeight());

            // Playerステータスの描画
            int statX = WIDTH - field.UNIT;
            int statY = HEIGHT - field.UNIT;
            g.setColor(Color.black);
            for (int i = 0; i < player.getPower(); ++i) {
                g.fillRect(statX, statY, field.UNIT, field.UNIT);
                g.drawImage(imgKinoko, statX, statY, this);
                statX -= field.UNIT;
            }
            for (int i = 0; i < player.getSpeed(); ++i) {
                g.fillRect(statX, statY, field.UNIT, field.UNIT);
                g.drawImage(imgSpeed, statX, statY, this);
                statX -= field.UNIT;
            }
            if (player.getDoubleJump()) {
                g.fillRect(statX, statY, field.UNIT, field.UNIT);
                g.drawImage(imgWing, statX, statY, this);
                statX -= field.UNIT;
            }
        } else {
            // タイトル & ハイスコア描画
            g.drawImage(imgTitle, 0, 0, this);
            g.setColor(Color.white);
            int h = 150;
            g.drawString("HIGH SCORES", 200, h);
            for(int i=0; i<10; ++i) {
                int w = 100;
                if(i%2==0) h += fm.getHeight();
                else w = 350;

                if(i==highlight) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.white);
                }
                String str = myScores.getScore(i);
                g.drawString(str, w, h);
            }
        }
    }

    /**
     * 画像ファイルからImageクラスへの変換
     * 
     * @param filename 画像ファイルのパス
     */
    private Image getImg(String filename) {
        ImageIcon icon = new ImageIcon(filename);
        Image img = icon.getImage();
        return img;
    }
}

