import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player {
    // 定数:プレイヤーの移動方向定義
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    // 移動速度
    private static final int SPEED = 8;
    private static final int JUMP = 15;
    private static final int GRAVITY = 1;

    // 描画関連情報
    private static final int ANIMATION_CNT = 5; // 100ms 毎に画像入れ替え
    private int animationCounter;
    private Image[] imgMeLeft = new Image[3];
    private Image[] imgMeRight = new Image[3];

    // サイズ
    private int myWidth, myHeight;

    // 進行方向
    private int myDirection;

    // 位置と速度
    private int x, y, vx, vy;
    private boolean isOnGround;
    
    // ユーザ操作
    private boolean requestLeft, requestRight;

    // プレイヤーが存在するfield
    private Field field;

    /**
     * Player コンストラクタ
     * @param x プレイヤーの初期位置X
     * @param y プレイヤーの初期位置y
     */
    public Player(Field f) {
        imgMeLeft[0] = getImg("image/player_left1.png");
        imgMeLeft[1] = getImg("image/player_left2.png");
        imgMeLeft[2] = getImg("image/player_left3.png");
        
        imgMeRight[0] = getImg("image/player_right1.png");
        imgMeRight[1] = getImg("image/player_right2.png");
        imgMeRight[2] = getImg("image/player_right3.png");

        myWidth = imgMeRight[0].getWidth(null);
        myHeight = imgMeRight[0].getHeight(null);

        field = f;
        x = field.getPlayerStartPos().x;
        y = field.getPlayerStartPos().y;
        vx = 0;
        vy = 0;
        isOnGround = false;
        myDirection = RIGHT;
        animationCounter = 0;

        requestLeft = false;
        requestRight = false;
    }

    /**
     * 右方向への移動
     */
    public void moveRight() {
        myDirection = RIGHT;
        requestRight = true;
        requestLeft = false;
    }

    /**
     * 左方向への移動
     */
    public void moveLeft() {
        myDirection = LEFT;
        requestRight = false;
        requestLeft = true;
    }

    /**
     * 左右方向への移動停止
     */
    public void stop() {
        requestLeft = false;
        requestRight = false;
    }

    /**
     * ジャンプ
     */
    public void jump() {
        if(isOnGround) {
            vy = -JUMP;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * あたり判定
     * TODO: 座標調整して移動可能ぎりぎりまで移動できるようにする
     */
    private void checkCollision() {
        // 落下速度更新
        vy = vy + GRAVITY;

        // 移動速度計算
        if(requestRight) {
            vx = SPEED;
        } else if(requestLeft) {
            vx = -SPEED;
        } else {
            vx = 0;
        }
        int nexty = y + vy; 
        int nextx = x + vx;
        
        // 左から右へのあたり判定
        if( field.isBlock(nextx+myWidth, y) || field.isBlock(nextx+myWidth, y+myHeight) ) {
            if(vx > 0) {
                vx = 0;
            }
        }

        // 右から左へのあたり判定
        if( field.isBlock(nextx, y) || field.isBlock(nextx, y+myHeight) ) {
            if(vx < 0) {
                vx = 0;
            }
        }

        // x座標更新
        x = x + vx;

        // 下から上へのあたり判定
        if( field.isBlock(x, nexty) ||  field.isBlock(x+myWidth, nexty)) {
            vy = 0;
        }

        // 上から下へのあたり判定
        if( field.isBlock(x, nexty+myHeight) || field.isBlock(x+myWidth, nexty+myHeight) ) {
            vy = 0;
            isOnGround = true;
        } else {
            isOnGround = false;
        }

        // ｙ座標更新
        y = y + vy;
    }

    /**
     * Player の描画更新
     * @param g グラフィクスオブジェクト
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
        // あたり判定 & プレイヤー位置更新
        checkCollision();

        // 描画画像選択
        Image imgMe;
        int index;
        if(vx != 0) {
            if( animationCounter < ANIMATION_CNT) {
                index = 0;
            } else if ( animationCounter < ANIMATION_CNT*2) {
                index = 1;
            } else if ( animationCounter < ANIMATION_CNT*3) {
                index = 0;
            } else if ( animationCounter < ANIMATION_CNT*4) {
                index = 2;
            } else {
                animationCounter = 0;
                index = 0;
            }
            ++animationCounter;
        } else {
            animationCounter = 0;
            index = 0;
        }


        if(myDirection == RIGHT) imgMe = imgMeRight[index];
        else imgMe = imgMeLeft[index];

        g.drawImage(imgMe, x-offsetX, y-offsetY, null);
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