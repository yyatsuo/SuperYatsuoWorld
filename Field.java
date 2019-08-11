import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

// Applet は非推奨らしい。。。
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;

/**
 * 位置クラス
 */
class Location {
    int x;
    int y;
}

/**
 * ゲームフィールドに関する実装
 */
public class Field implements ActionListener{
    public final int UNIT = 32; // 部品1個の縦横サイズ。32px決め打ち。
    private int TimeLimit; // フィールドの制限時間
    private int WIDTH;     // フィールドの横の単位 (100block * 32px/block)
    private int HEIGHT;    // フィールドの縦の単位 (20block  * 32px/block)
    private int score;     // 得点

    private boolean isDead;
    private int timeRemained;
    private Location startPlayerPos, goalPos;
    private Image imgBlock, imgHatena, imgKnckd, imgGoal, imgCoin;
    
    private AudioClip soundCoin,soundKill,soundKnock, soundPup;

    private Timer timer;


    private List<Enemy> enemies = new LinkedList<>();
    private List<Item> items = new LinkedList<>();
    
    // ブロックの定義
    public final char NULL_BLOCK   = ' '; // 移動可能な場所
    public final char BRICK_BLOCK  = '*'; // レンガブロック
    public final char P_UP_HATENA  = 'P'; // パワーアップアイテム入りはてなブロック
    public final char JMP_HATENA   = 'J'; // ジャンプアイテム入りはてなブロック
    public final char S_UP_HATENA  = 'V'; // スピードアップアイテム入りはてなブロック
    public final char COIN_HATENA  = 'C'; // コイン入りはてなブロック
    public final char KNCKD_HATENA = 'H'; // 叩かれた後のはてなブロック
    public final char COIN         = 'O'; // コイン
    public final char ENEMY        = 'E'; // 敵キャラの初期位置
    public final char START_POS    = 'S'; // スタート位置
    public final char GOAL_POS     = 'G'; // ゴール位置
    public final char DEAD_BLOCK   = 'D'; // 即死判定ブロック（落とし穴として使う)
    private char[][] field;


    /**
     * Fieldクラスのコンストラクタ
     */
    public Field() {
        System.out.println("Start!");
        imgBlock = getImg("image/brick_block.png");
        imgHatena = getImg("image/hatena_block.png");
        imgKnckd = getImg("image/knocked_hatena_block.png");
        imgGoal = getImg("image/goal.png");
        imgCoin = getImg("image/coin.png");
        loadMapfile("data/field.map");
        isDead = false;
        timeRemained = TimeLimit;
        score = 0;
        timer = new Timer(1000, this);
        timer.start();
        enemies = new ArrayList<Enemy>();
        startPlayerPos = new Location();
        goalPos = new Location();
        for(int y=0; y<HEIGHT; ++y) {
            for(int x=0; x<WIDTH; ++x) {
                switch(field[y][x]) {
                    case START_POS:
                        startPlayerPos.x = UNIT*x;
                        startPlayerPos.y = UNIT*y;
                        break;
                    case GOAL_POS:
                        goalPos.x = UNIT*x;
                        goalPos.y = UNIT*y;
                        break;
                    case ENEMY:
                        enemies.add(new Enemy(UNIT*x, UNIT*y, this));
                        break; 
                }
            }
        }
        try {
            //oundCoin = Applet.newAudioClip(new File("se/coin.wav").toURI().toURL());
            soundCoin = Applet.newAudioClip(getClass().getResource("/se/coin.wav"));
            //soundKill = Applet.newAudioClip(new File("se/kill.wav").toURI().toURL());
            soundKill = Applet.newAudioClip(getClass().getResource("/se/kill.wav"));
            //soundKnock = Applet.newAudioClip(new File("se/knock.wav").toURI().toURL());
            soundKnock = Applet.newAudioClip(getClass().getResource("/se/knock.wav"));
            //soundPup = Applet.newAudioClip(new File("se/p_up.wav").toURI().toURL());
            soundPup = Applet.newAudioClip(getClass().getResource("/se/p_up.wav"));
        } catch(Exception e) {
            System.out.println("Audioファイルの読み込みに失敗しました。");
            e.printStackTrace();
        }
    } 

    /**
     * フィールドを初期状態に戻す
     */
    public void restart() {
        timeRemained = TimeLimit;
        if(timer.isRunning()==false) timer.start();
        isDead = false;
        score = 0;
        for(Enemy e:enemies ) {
            e.reset();
        }
        items.clear();
        loadMapfile("data/field.map");
    }
    
    /**
     * プレイヤーの初期位置を取得
     * @return プレイヤーの初期位置
     */
    public Location getPlayerStartPos() {
        return startPlayerPos;
    }

    /**
     * ゴール位置を取得
     * @return　フィールド内のゴール位置
     */
    public Location getGoalPos() {
        return goalPos;
    }

    /**
     * x座標をブロック単位からpxに変換
     * @return x座標(px)
     */
    public int getWidthInPx() {
        return UNIT*WIDTH;
    }

    /**
     * y座標をブロック単位からpxに変換
     * @return y座標(px)
     */
    public int getHeightInPx() {
        return UNIT*HEIGHT;
    }

    /**
     * 与えられた座標位置がブロックか確認
     * @param x x座標
     * @param y y座標
     * @param isPlayer プレイヤーのあたり判定の場合 true
     * @return 座標がブロックの場合 true
     */
    public boolean isBlock(int x, int y, boolean isPlayer) {
        boolean ret = false;

        try {
            char block = field[y / UNIT][x / UNIT];
            if (block == BRICK_BLOCK || block == P_UP_HATENA || block == JMP_HATENA || block == S_UP_HATENA
                    || block == COIN_HATENA || block == KNCKD_HATENA || block == DEAD_BLOCK) {
                ret = true;
            }

            // プレイヤーの場合は即死判定とコインの取得判定をする
            if (isPlayer) {
                if (block == DEAD_BLOCK) {
                    isDead = true;
                } else if (block == COIN) {
                    soundCoin.play();
                    field[y / UNIT][x / UNIT] = ' ';
                    score += 10;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // フィールド外に出た場合はブロックありとして扱う
            e.printStackTrace();
            ret = true;
        }
        return ret;
    }

    /**
     * 与えられた座標のブロックをたたく
     * @param x x座標
     * @param w x方向の幅
     * @param y y座標
     */
    public void knock(int x, int w, int y) {
        for(int i=x; i<=x+w; i+=w) {
            int idx_x = i/UNIT;
            int idx_y = y/UNIT;
            Item item;
            char block = field[idx_y][idx_x];
            switch(block) {
                case P_UP_HATENA:
                    soundKnock.play();
                    field[idx_y][idx_x] = KNCKD_HATENA;
                    item = new Item(idx_x*UNIT, (idx_y-1)*UNIT, Item.Type.PowerUp);
                    items.add(item);
                    break;
                case JMP_HATENA:
                    soundKnock.play();
                    field[idx_y][idx_x] = KNCKD_HATENA;
                    item = new Item(idx_x*UNIT, (idx_y-1)*UNIT, Item.Type.DoubleJump);
                    items.add(item);
                    break;
                case S_UP_HATENA:
                    soundKnock.play();
                    field[idx_y][idx_x] = KNCKD_HATENA;
                    item = new Item(idx_x*UNIT, (idx_y-1)*UNIT, Item.Type.SpeedUp);
                    items.add(item);
                    break;
                case COIN_HATENA:
                    soundKnock.play();
                    field[idx_y][idx_x] = KNCKD_HATENA;
                    field[idx_y-1][idx_x] = 'O';
                    break;
            }    
        }

    }

    /**
     * 与えられた座標位置に敵キャラがいるか確認
     * @param x x座標
     * @param y y座標
     * @param w x方向の領域
     * @param h y方向の領域
     * @param killable 敵キャラを倒せるかどうか
     * @return 座標の場所に敵キャラがいる場合 true
     */
    public boolean isEnemy(int x, int y, int w, int h, boolean killable) {
        for(Enemy e:enemies ) {
            if(e.isExist(x, y, w, h, killable)) {
                if(killable) {
                    score += 50;
                    soundKill.play();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 与えられて座標位置にアイテムがあるかどうか確認。
     * アイテムがあった場合は該当するアイテムを使用する。
     * @param x x座標
     * @param y y座標
     * @param w プレイヤーの幅
     * @param h プレイヤーの高さ
     * @param p プレイヤーオブジェクト
     */
    public void isItem(int x, int y, int w, int h, Player p) {
        for(int i=0; i<items.size(); ++i) {
            Item item = items.get(i);
            if(item.isExist(x, y, w, h)) {
                item.use(p);
                items.remove(i);
                soundPup.play();
                --i;
            }
        }
    }

    /**
     * Fieldの描画更新
     * @param g グラフィックスオブジェクト
     * @param offsetX　X方向の描画オフセット
     * @param offsetY　Y方向の描画オフセット
     */
    public void update(Graphics g, int offsetX, int offsetY) {
        for(int y=0; y<HEIGHT; ++y) {
            for(int x=0; x<WIDTH; ++x) {
                switch(field[y][x]) {
                    case BRICK_BLOCK:
                        g.drawImage(imgBlock, x*UNIT - offsetX, y*UNIT - offsetY, null);
                        break;
                    case P_UP_HATENA:
                    case JMP_HATENA:
                    case S_UP_HATENA:
                    case COIN_HATENA:
                        g.drawImage(imgHatena, x*UNIT - offsetX, y*UNIT - offsetY, null);
                        break;
                    case KNCKD_HATENA:
                        g.drawImage(imgKnckd, x*UNIT - offsetX, y*UNIT - offsetY, null);
                        break;
                    case COIN:
                        g.drawImage(imgCoin, x*UNIT - offsetX, y*UNIT - offsetY, null);
                        break;
                    case GOAL_POS:
                        g.drawImage(imgGoal, x*UNIT - offsetX - imgGoal.getWidth(null)/2, y*UNIT - offsetY - imgGoal.getHeight(null)/2, null);
                        break;
                }
            }
        }
        // フィールド内のアイテムを描画
        for (Item item:items) {
            item.update(g, offsetX, offsetY);
        }
        // フィールド内の敵キャラを描画
        for (Enemy enemy:enemies) {
            enemy.update(g, offsetX, offsetY);
        }
    }

    /**
     * アクションリスナー。残り時間の減算処理を行う。
     * @param e アクションイベント
     */
    public void actionPerformed(ActionEvent e) {
        timeRemained--;
        if(timeRemained < 0) {
            timer.stop();
            isDead = true;
        }
    }

    /**
     * 時間切れフラグ取得
     * @return 時間切れかどうか
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * 残り時間取得関数
     * @return フィールドの制限時間まであと何秒あるか
     */
    public int getTimeRemained() {
        if(timeRemained < 0) {
            return 0;
        }            
        return timeRemained;
    }

    /**
     * スコア取得
     * @return プレイヤーの取得したスコア
     */
    public int getScore() {
        return score;
    }

    /**
     * ファイルからフィールドをロードする
     * @param f フィールドが記述されているファイルパス
     */
    private void loadMapfile(String f) {
        try {
            File file = new File(f);
            if(file.exists()) {
                FileReader r = new FileReader(file);
                BufferedReader br = new BufferedReader(r);
                this.WIDTH = Integer.parseInt(br.readLine());
                this.HEIGHT = Integer.parseInt(br.readLine());
                this.TimeLimit = Integer.parseInt(br.readLine());
                this.field = new char[HEIGHT][WIDTH];

                for(int y=0; y<HEIGHT; ++y) {
                    String data = br.readLine();
                    this.field[y] = data.toCharArray();
                }
                r.close();   
            }
            else {
                System.out.println("mapファイル " + f + "が見つかりません");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("mapファイルの読み込みに失敗しました。");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 画像ファイルからImageクラスへの変換
     * @param filename 画像ファイルのパス
     */
    private Image getImg(String filename) {
        var url = getClass().getResource("/"+filename);
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        return img;
    }
}