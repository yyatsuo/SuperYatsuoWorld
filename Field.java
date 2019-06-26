import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Location {
    int x;
    int y;
}

public class Field {

    private final int UNIT = 32; // 部品1個の縦横サイズ。32px決め打ち。
    private final int WIDTH = 40; // フィールドの横の単位 (100block * 32px/block)
    private final int HEIGHT = 13; // フィールドの縦の単位 (20block  * 32px/block)

    private Location startPlayerPos;
    private Image imgBlock;
    
    // TODO: ファイルからフィールドを読み込みできるようにする。
    // 0: プレイヤーが移動できる場所
    private final int BRICK_BLOCK = 1;     // 1: レンガブロック
    private final int HATENA_BLOCK = 2;    // 2: はてなブロック
    private final int START_POS   = 8;     // 8: スタート位置
    private final int GOAL_POS    = 9;     // 9: ゴール位置
    private int[][] field = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public Field() {
        imgBlock = getImg("image/brick_block.png");
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
    
    /**
     * プレイヤーの初期位置を取得
     * @return プレイヤーの初期位置
     */
    public Location getPlayerStartPos() {
        return startPlayerPos;
    }

     /**
     * Filed の描画更新
     * @param g グラフィクスオブジェクト
     */
    public void draw(Graphics g) {
        for(int y=0; y<HEIGHT; ++y) {
            for(int x=0; x<WIDTH; ++x) {
                switch(field[y][x]) {
                    case BRICK_BLOCK:
                        g.drawImage(imgBlock, x*UNIT, y*UNIT, null);
                        break;
                }
            }
        }
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