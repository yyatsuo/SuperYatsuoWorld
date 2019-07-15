import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

class Location {
    int x;
    int y;
}

public class Field implements ActionListener{
    private final int TimeLimit = 300; // フィールドの制限時間
    public final int UNIT = 32; // 部品1個の縦横サイズ。32px決め打ち。
    private final int WIDTH = 40; // フィールドの横の単位 (100block * 32px/block)
    private final int HEIGHT = 20; // フィールドの縦の単位 (20block  * 32px/block)

    private boolean isTimeOver;
    private int timeRemained;
    private Location startPlayerPos, goalPos;
    private Image imgBlock;
    private Image imgGoal;

    private Timer timer;

    private List<Enemy> enemies;
    
    // TODO: ファイルからフィールドを読み込みできるようにすると良いかも
    public final int NULL_BLOCK   = 0; // 0: 移動可能な場所
    public final int BRICK_BLOCK  = 1; // 1: レンガブロック
    public final int HATENA_BLOCK = 2; // 2: はてなブロック
    public final int COIN         = 3; // 3: コイン
    public final int P_UP_ITEM    = 4; // 4: パワーアップアイテム
    public final int JUMP_ITEM    = 5; // 5: 二段ジャンプアイテム
    public final int S_UP_ITEM    = 6; // 6: 移動速度アップアイテム
    public final int ENEMY        = 7; // 7: 敵キャラの初期位置
    public final int START_POS    = 8; // 8: スタート位置
    public final int GOAL_POS     = 9; // 9: ゴール位置
    private int[][] field = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,1,1,1,1,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    /**
     * Fieldクラスのコンストラクタ
     */
    public Field() {
        imgBlock = getImg("image/brick_block.png");
        imgGoal = getImg("image/goal.png");
        isTimeOver = false;
        timeRemained = TimeLimit;
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
    } 

    /**
     * フィールドを初期状態に戻す
     */
    public void restart() {
        timeRemained = TimeLimit;
        isTimeOver = false;
        for(Enemy e:enemies ) {
            e.reset();
        }
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
     * @return 座標がブロックの場合 true
     */
    public boolean isBlock(int x, int y) {
        boolean ret = false;
        int block = field[y/UNIT][x/UNIT];
        if(block == BRICK_BLOCK || block == HATENA_BLOCK) {
            ret = true;
        }
        return ret;
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
                return true;
            }
        }
        return false;
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
                    case GOAL_POS:
                        g.drawImage(imgGoal, x*UNIT - offsetX - imgGoal.getWidth(null)/2, y*UNIT - offsetY - imgGoal.getHeight(null)/2, null);
                        break;
                }
            }
        }
        // フィールド内の敵キャラを描画
        for(int i=0; i<enemies.size(); ++i) {
            enemies.get(i).update(g, offsetX, offsetY);
        }
    }

    /**
     * アクションリスナー。残り時間の減算処理を行う。
     * @param e アクションイベント
     */
    public void actionPerformed(ActionEvent e) {
        timeRemained--;
        if(timeRemained < 0) {
            isTimeOver = true;
        }
    }

    /**
     * 時間切れフラグ取得
     * @return 時間切れかどうか
     */
    public boolean isTimeOver() {
        return isTimeOver;
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
     * 画像ファイルからImageクラスへの変換
     * @param filename 画像ファイルのパス
     */
    private Image getImg(String filename) {
        ImageIcon icon = new ImageIcon(filename);
        Image img = icon.getImage();
        return img;
    }
}