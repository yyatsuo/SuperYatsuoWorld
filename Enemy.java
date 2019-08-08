import java.awt.*;
import javax.swing.*;

/**
 * 敵キャラクタに関するクラスの実装
 */
public class Enemy {
    // 位置と速度
    private int x, y, initx, inity, vx;
    private int myHeight, myWidth;

    // field情報
    private Field field;

    // 描画関連情報
    private static final int ANIMATION_CNT = 5; // 20x5 ms  毎に画像入れ替え
    private int animationCounter;
    private Image[] imgMe = new Image[4];
    private boolean isDead;

    /**
     * Enemyコンストラクタ
     * @param x 初期位置のX座標
     * @param y 初期位置のY座標
     * @param f Enemyを生成するフィールド
     */
    public Enemy(int x, int y, Field f) {
        // アニメーション用のイメージ配列
        imgMe[0] = getImg("image/enemy_front.png");
        imgMe[1] = getImg("image/enemy_right.png");
        imgMe[2] = getImg("image/enemy_back.png");
        imgMe[3] = getImg("image/enemy_left.png");
        myWidth = imgMe[0].getWidth(null);
        myHeight = imgMe[0].getHeight(null);
        this.initx = x;
        this.inity = y+2;
        this.x = x;
        this.y = y;
        isDead = false;
        field = f;
        vx = 1;
    }

    /**
     * 与えられた座標を始点する四角形の領域に敵キャラがいるかどうかを判定する
     * @param x 判定するx座標
     * @param y 判定するy座標
     * @param w 判定するx方向の領域
     * @param h 判定するy方向の領域
     * @param killable あたりの場合に敵キャラを倒すかどうか
     * @return 敵キャラがいれば true
     */
    public boolean isExist(int x, int y, int w, int h, boolean killable) {
        if (!isDead) {
            if (((this.x >= x && this.x + this.myWidth - x < w + this.myWidth)
                    || (this.x <= x && x + w - this.x < w + this.myWidth))
                    && ((this.y >= y && this.y + this.myHeight - y < h + this.myHeight)
                            || (this.y <= y && y + h - this.y < h + this.myHeight))) {
                if (killable) {
                    isDead = true;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 初期状態に戻す
     */
    public void reset() {
        x = initx;
        y = inity;
        isDead = false;
        vx = 1;
    }

    /**
     * あたり判定を行って座標更新する
     */
    private void updatePosition() {
        // 移動先座標の計算
        int nextx = x + vx;

        // 左から右へのあたり判定 (右側がブロックか、床がない)
        if (field.isBlock(nextx + myWidth, y, false) || field.isBlock(nextx + myWidth, y + myHeight, false)
                || !field.isBlock(nextx + myWidth, y + myHeight + 10, false)) {
            if (vx > 0) {
                vx = -1;
            }
        }

        // 右から左へのあたり判定（左側がブロックか、床がない）
        if (field.isBlock(nextx, y, false) || field.isBlock(nextx, y + myHeight, false) || !field.isBlock(nextx, y + myHeight + 10, false)) {
            if (vx < 0) {
                vx = 1;
            }
        }

        // x座標更新
        x = x + vx;
    }

    /**
     * 更新処理を行う
     * @param g グラフィックスオブジェクト
     * @param offsetX X軸方向の描画オフセット
     * @param offsetY Y軸方向の描画オフセット
     */
    public void update(Graphics g, int offsetX, int offsetY) {
        if (!isDead) {
            updatePosition();

            int index = 0;
            // 移動アニメーション
            if (animationCounter < ANIMATION_CNT) {
                index = 0;
            } else if (animationCounter < ANIMATION_CNT * 2) {
                index = 1;
            } else if (animationCounter < ANIMATION_CNT * 3) {
                index = 2;
            } else if (animationCounter < ANIMATION_CNT * 4) {
                index = 3;
            } else {
                animationCounter = 0;
                index = 0;
            }
            ++animationCounter;
            g.drawImage(imgMe[index], x - offsetX, y - offsetY, null);
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