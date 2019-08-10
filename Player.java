import java.awt.*;
import javax.swing.*;
import java.io.File;

// Applet は非推奨らしい。。。
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;

/**
 * Playerキャラクタに関するクラスの実装
 */
public class Player {
    // ゲームステート
    public static final int PLAYING  = 0;
    public static final int GAMEOVER = 1;
    public static final int CLEAR    = 2;

    // プレイヤーの移動方向
    private final int LEFT = 0;
    private final int RIGHT = 1;

    // 移動速度
    private final int GRAVITY = 1;
    private int SPEED = 4;
    private int JUMP = 15;

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

    // アイテムによるステータス向上
    private int speedup;
    private int power;
    private boolean doubleJump;
    private boolean allowDoubleJump;

    // 死亡フラグ
    private boolean isDead;

    // 無敵モード
    private boolean isInvincible;

    private AudioClip soundDamage, soundPdown, soundJump;

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

        try {
            //soundDamage = Applet.newAudioClip(new File("se/damage.wav").toURI().toURL());
            soundDamage = Applet.newAudioClip(getClass().getResource("/se/damage.wav"));   
            //soundPdown = Applet.newAudioClip(new File("se/p_down.wav").toURI().toURL());
            soundPdown = Applet.newAudioClip(getClass().getResource("/se/p_down.wav"));
            //soundJump = Applet.newAudioClip(new File("se/jump.wav").toURI().toURL());
            soundJump = Applet.newAudioClip(getClass().getResource("/se/jump.wav"));
        } catch(Exception e) {
            System.out.println("Audioファイルの読み込みに失敗しました。");
            e.printStackTrace();
            System.exit(1);
        }

        myWidth = imgMeRight[0].getWidth(null);
        myHeight = imgMeRight[0].getHeight(null);
        myLife = 4;
        field = f;
        init();
    }

    /**
     * プレイヤー状態の初期化（myLife以外)
     */
    private void init() {
        x = field.getPlayerStartPos().x;
        y = field.getPlayerStartPos().y;
        vx = 0;
        vy = 0;
        power = 0;
        doubleJump = false;
        allowDoubleJump = false;
        isOnGround = false;
        isDead = false;
        isInvincible = false;
        myDirection = RIGHT;
        animationCounter = 0;
        requestLeft = false;
        requestRight = false;
        speedup = 0;
        SPEED = 4;
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
            soundJump.play();
            vy = -JUMP;
            if(this.doubleJump) allowDoubleJump = true;
        } else if(allowDoubleJump) {
            soundJump.play();
            vy = -JUMP;
            allowDoubleJump = false;
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

    public int getPower() {
        return power;
    }

    public boolean getDoubleJump() {
        return doubleJump;
    }

    public int getSpeed() {
        return speedup;
    }

    public void speedup() {
        ++speedup;
        SPEED += 3;
    }

    public void setDoubleJump() {
        this.doubleJump = true;
    }

    public void powerUp() {
        this.power++;
    }

    /**
     * あたり判定を行い、プレイヤーの移動パラメータを更新する
     */
    private void updatePosition() {
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
        if( field.isBlock(nextx+myWidth, y, true) || field.isBlock(nextx+myWidth, y+myHeight, true) ) {
            if(vx > 0) {
                vx = 0;
            }
        }

        // 右から左へのあたり判定
        if( field.isBlock(nextx, y, true) || field.isBlock(nextx, y+myHeight, true) ) {
            if(vx < 0) {
                vx = 0;
            }
        }

        // 横方向から敵への衝突
        if( field.isEnemy(nextx, y, myWidth, myHeight, isInvincible) ) {
            if(!isInvincible) {
                this.isDead = true;
            }
        }

        // x座標更新
        x = x + vx;

        // 下から上へのあたり判定
        if( field.isBlock(x, nexty, true) ||  field.isBlock(x+myWidth, nexty, true)) {
            vy = 0;
            // ブロックをたたく
            field.knock(x, myWidth, nexty);
        }

        // 上から下へのあたり判定
        if( field.isBlock(x, nexty+myHeight, true) || field.isBlock(x+myWidth, nexty+myHeight, true) ) {
            vy = 0;
            isOnGround = true;
            allowDoubleJump = false;
        } else {
            isOnGround = false;
        }

        // 上から敵を踏んだ時のあたり判定
        if( field.isEnemy(x, nexty, myWidth, myHeight, true)) {
            // 強制的にジャンプ
            vy = -JUMP;
        }

        // ｙ座標更新
        y = y + vy;

        // めり込み防止
        while(field.isBlock(x, y, true) || field.isBlock(x+myWidth, y, true)) {
            y++;
        }

        field.isItem(x, y, myWidth, myHeight, this);
    }

    /**
     * Player の描画更新
     * @param g グラフィクスオブジェクト
     * @param offsetX X方向のオフセット
     * @param offsetY Y方向のオフセット
     */
    public int update(Graphics g, int offsetX, int offsetY) {
        // あたり判定 & プレイヤー位置更新
        updatePosition();

        // アイテム取得判定

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

        // ダメージ判定
        if(isDead && power>0 && field.getTimeRemained()>0) {
            soundPdown.play();
            power--;
            isDead = false;
        }
        if (field.isDead() || isDead) {
            if (myLife > 0 ) {
                // テレッテテレッテテ
                soundDamage.play();
                myLife--;
                field.restart();
                init();
            } else {
                // げーむおーばー
                return GAMEOVER;
            }
        }

        // ゴール位置チェック
        if (field.getGoalPos().x < x+(myWidth/2) && x+(myWidth/2) < field.getGoalPos().x+field.UNIT
        &&  field.getGoalPos().y < y+(myHeight/2) && y+(myHeight/2) < field.getGoalPos().y+field.UNIT) {
            // くりあ
            return CLEAR;
        }
        return PLAYING;
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