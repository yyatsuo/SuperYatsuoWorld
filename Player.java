import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player {
    // プレイヤーの移動方向
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    // 移動速度
    private static final int SPEED = 6;
    private static final int JUMP = 15;
    private static final int GRAVITY = 1;

    // 描画関連情報
    private static final int ANIMATION_CNT = 5; // 100ms (20ms x 5) 毎に画像入れ替え
    private int animationCounter;
    private Image[] imgMeLeft = new Image[3];
    private Image[] imgMeRight = new Image[3];

    // サイズ
    private int myWidth, myHeight, myLife;

    // 進行方向
    private int myDirection;

    // 位置と速度
    private int x, y, vx, vy;
    private boolean isOnGround;
    
    // ユーザ操作
    private boolean requestLeft, requestRight;

    // field情報
    private Field field;

    /**
     * Player コンストラクタ
     * @param f プレイするフィールド
     */
    public Player(Field f) {
        // アニメーション用のイメージ配列
        imgMeLeft[0] = getImg("image/player_left1.png");
        imgMeLeft[1] = getImg("image/player_left2.png");
        imgMeLeft[2] = getImg("image/player_left3.png");
        imgMeRight[0] = getImg("image/player_right1.png");
        imgMeRight[1] = getImg("image/player_right2.png");
        imgMeRight[2] = getImg("image/player_right3.png");

        myWidth = imgMeRight[0].getWidth(null);
        myHeight = imgMeRight[0].getHeight(null);

        myLife = 4;

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

    /**
     * プレイヤーのX座標を取得する
     * @return 現在のX座標
     */
    public int getX() {
        return x;
    }

    /**
     * プレイヤーのY座標を取得する
     * @return 現在のY座標
     */
    public int getY() {
        return y;
    }

    /**
     * プレイヤーの残りライフを取得する
     * @return 残りライフ
     */
    public int getLife() {
        return myLife;
    }

    /**
     * あたり判定
     * TODO: 座標調整
     */
    private void checkCollision() {
        // 落下速度更新
        vy = vy + GRAVITY;

        // 移動速度更新
        if(requestRight) {
            vx = SPEED;
        } else if(requestLeft) {
            vx = -SPEED;
        } else {
            vx = 0;
        }

        // 移動先座標の計算
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
     * @param offsetX X方向のオフセット
     * @param offsetY Y方向のオフセット
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
        // あたり判定 & プレイヤー位置更新
        checkCollision();

        // 描画画像選択
        Image imgMe;
        int index;
        if(vx != 0) {
            // 歩いてるアニメーション
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
            // 止まってる
            animationCounter = 0;
            index = 0;
        }

        // 画像と移動方向を合わせる
        if(myDirection == RIGHT) imgMe = imgMeRight[index];
        else imgMe = imgMeLeft[index];
        g.drawImage(imgMe, x-offsetX, y-offsetY, null);

        // タイムオーバーチェック
        if (field.isTimeOver()) {
            if (myLife > 0) {
                // テレッテテレッテテ
                myLife--;
                System.out.println(myLife);
                field.restart();
                x = field.getPlayerStartPos().x;
                y = field.getPlayerStartPos().y;
            } else {
                // げーむおーばー
                // TODO: ゲームオーバーっぽい演出
                System.out.println("Game Over");
                System.exit(0);
            }
        }

        if(field.isGoal()) {
            // くりあ
            // TODO: クリアしたっぽい演出
            System.out.println("Game Clear");
            System.exit(0);
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