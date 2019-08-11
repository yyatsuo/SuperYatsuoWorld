import java.awt.*;
import javax.swing.*;

// Applet は非推奨らしい。。。
import java.applet.Applet;
import java.applet.AudioClip;

/**
 * フィールド内に配置するアイテムに関する実装
 */
public class Item {
    /**
     * アイテム種別の定義
     */
    public enum Type {
        PowerUp,
        SpeedUp,
        DoubleJump,
    };

    private Type type;
    private int x, y, myWidth, myHeight;
    private Image imgMe;

    /**
     * Itemコンストラクタ
     * @param x アイテムのx座標
     * @param y アイテムのy座標
     * @param t アイテムの種類
     */
    public Item(int x, int y, Type t) {
        this.x = x;
        this.y = y;
        this.myWidth = 32;
        this.myHeight = 32;
        this.type = t;
        switch(this.type) {
            case PowerUp:
                imgMe = getImg("image/kinoko.png");
                break;
            case SpeedUp:
                imgMe = getImg("image/speed.png");
                break;
            case DoubleJump:
                imgMe = getImg("image/wing.png");
                break;
        }
    }

    /**
     * アイテムを使用する
     * @param p アイテムを使用するプレイヤー
     */
    public void use(Player p) {
        switch(this.type) {
            case PowerUp:
                p.powerUp();
                break;
            case SpeedUp:
                p.speedup();
                break;
            case DoubleJump:
                p.setDoubleJump();
                break;
        }
    }

    /**
     * 与えられた領域にアイテムが存在するか判定する
     * @param x 開始X座標
     * @param y 開始y座標
     * @param w 横幅
     * @param h 高さ
     * @return アイテムがその領域に存在するかどうか
     */
    public boolean isExist(int x, int y, int w, int h) {
        if (((this.x >= x && this.x + this.myWidth - x < w + this.myWidth)
                || (this.x <= x && x + w - this.x < w + this.myWidth))
                && ((this.y >= y && this.y + this.myHeight - y < h + this.myHeight)
                        || (this.y <= y && y + h - this.y < h + this.myHeight))) {
            return true;
        }
        return false;
    }

    /**
     * 描画更新処理
     * @param g グラフィックスオブジェクト
     * @param offsetX X軸方向の描画オフセット
     * @param offsetY Y軸方向の描画オフセット
     */
    public void update(Graphics g, int offsetX, int offsetY) {
        g.drawImage(imgMe, x-offsetX, y-offsetY, null);
    }

    /**
     * 画像ファイルからImageクラスへの変換
     * 
     * @param filename 画像ファイルのパス
     */
    private Image getImg(String filename) {
        var url = getClass().getResource(filename);
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        return img;
    }
}