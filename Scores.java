import java.io.Serializable;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * ハイスコアを記録するクラスの実装
 */
public class Scores implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] myScore = new int[10];
    private String[] date = new String[10];

    /**
     * デフォルトコンストラクタ
     */
    public Scores() {
        for(int i=0; i<10; ++i) {
            myScore[i] = 0;
            date[i] = "----/--/--";
        }
    }

    /**
     * 新しいスコアを記録する
     * @param s スコア
     * @return 記録位置。ランク外のときは -1
     */
    public int insertScore(int s) { 
        int insertPosition = -1;
        for(int i=0; i<myScore.length; ++i) {
            if(myScore[i] < s) {
                insertPosition = i;
                break;
            }
        }
        if (insertPosition != -1) {
            int newScore = s;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String newDate = sdf.format(cal.getTime());

            for (int i = myScore.length - 2; i > insertPosition; --i) {
                myScore[i + 1] = myScore[i];
                date[i+1] = date[i];
            }
            myScore[insertPosition] = newScore;
            date[insertPosition] = newDate;
        }
        return insertPosition;
    }

    /**
     * 指定された順位の記録を取得する。ゼロオリジン。
     * @param i 記録位置。ゼロオリジン。
     * @return 順位、記録日、スコア、を文字列として返す。
     */
    public String getScore(int i) {
        String s;
        if(i < 9) s = Integer.toString(i+1) + ".  " + date[i] + "  " + Integer.toString(myScore[i]);
        else s = Integer.toString(i+1) + ". " + date[i] + "  " + Integer.toString(myScore[i]);
        return s;
    }
}