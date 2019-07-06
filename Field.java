import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Location {
    int x;
    int y;
}

public class Field implements ActionListener{
    private final int TimeLimit = 300; // フィールドの制限時間
    private final int UNIT = 32; // 部品1個の縦横サイズ。32px決め打ち。
    private final int WIDTH = 40; // フィールドの横の単位 (100block * 32px/block)
    private final int HEIGHT = 20; // フィールドの縦の単位 (20block  * 32px/block)

    private boolean isGoal;
    private boolean isTimeOver;
    private int timeRemained;
    private Location startPlayerPos;
    private Image imgBlock;
    private Image imgGoal;

    private Timer timer;

    
    // TODO: ファイルからフィールドを読み込みできるようにすると良いかも
    public final int NULL_BLOCK = 0;   // 0: 移動可能な場所
    public final int BRICK_BLOCK = 1;  // 1: レンガブロック
    public final int HATENA_BLOCK = 2; // 2: はてなブロック
    public final int START_POS = 8;    // 8: スタート位置
    public final int GOAL_POS = 9;     // 9: ゴール位置
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
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,1,1,1,1,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public Field() {
        imgBlock = getImg("image/brick_block.png");
        imgGoal = getImg("image/goal.png");
        isGoal = false;
        isTimeOver = false;
        timeRemained = TimeLimit;
        timer = new Timer(1000, this);
        timer.start();

        startPlayerPos = new Location();
        for(int y=0; y<HEIGHT; ++y) {
            for(int x=0; x<WIDTH; ++x) {
                if (field[y][x] == START_POS) {
                    startPlayerPos.x = UNIT*x;
                    startPlayerPos.y = UNIT*y;
                }
            }
        }
    } 

    public void restart() {
        timeRemained = TimeLimit;
        isTimeOver = false;
    }
    
    /**
     * プレイヤーの初期位置を取得
     * @return プレイヤーの初期位置
     */
    public Location getPlayerStartPos() {
        return startPlayerPos;
    }

    public int getWidthInPx() {
        return UNIT*WIDTH;
    }

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
        if(block == GOAL_POS) {
            isGoal = true;
        }
        return ret;
    }

    /**
     * Fieldの描画更新
     * @param g グラフィックスオブジェクト
     * @param offsetX　X方向の描画オフセット
     * @param offsetY　Y方向の描画オフセット
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
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
    }

    public void actionPerformed(ActionEvent e) {
        timeRemained--;
        if(timeRemained < 0) {
            isTimeOver = true;
        }
    }

    public boolean isTimeOver() {
        return isTimeOver;
    }

    public boolean isGoal() {
        return isGoal;
    }

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