import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player {
    // プレイヤーのサイズ
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    // 移動速度
    private static final int SPEED = 10;
    private static final int JUMP = 20;
    private static final int GRAVITY = 1;

    // プレイヤーの位置
    private int x;
    private int y;

    // プレイヤーの速度
    private int vx;
    private int vy;

    // ジャンプ中
    private boolean isJump;
    /**
     * Player コンストラクタ
     * @param x プレイヤーの初期位置X
     * @param y プレイヤーの初期位置y
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        isJump = true; // 地面判定できるまではジャンプ中として扱う
    }

    /**
     * 右方向への移動
     */
    public void moveRight() {
        vx = SPEED;
    }

    /**
     * 左方向への移動
     */
    public void moveLeft() {
        vx = -SPEED;
    }

    /**
     * 左右方向への移動停止
     */
    public void stop() {
        vx = 0;
    }

    /**
     * ジャンプ
     */
    public void jump() {
        if(isJump==false) {
            vy = -JUMP;
            isJump = true;
        }
    }

    /**
     * 地面判定
     */
    private boolean isOnGround() {
        // ToDo: ブロック上かどうかのあたり判定
        if(y > 750) {
            return true;
        }
        return false;
    }

    /**
     * Player の描画更新
     * @param g グラフィクスオブジェクト
     */
    public void draw(Graphics g) {
        // TODO このままだとめり込む。どうしよう。。。
        // 速度を更新する
        if(isOnGround() && vy >= 0) {
            vy = 0;
            isJump = false;
        }
        else {
            vy = vy + GRAVITY;
        }

        // プレイヤー位置の調整

        // 位置更新
        x += vx;
        y += vy;

        // 描画
        // TODO: プレイヤーの画像設定
        g.setColor(Color.RED);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
}