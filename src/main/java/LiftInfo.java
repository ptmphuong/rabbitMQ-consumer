import com.google.gson.Gson;

import java.util.Properties;

public class LiftInfo {
    private int resortID;
    private int seasonID;
    private int dayID;
    private int skierID;
    private int liftID;
    private int time;

    public LiftInfo(int resortID, int seasonID, int dayID, int skierID, int liftID, int time) {
        this.resortID = resortID;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
        this.liftID = liftID;
        this.time = time;
    }

    public int getSkierID() {
        return skierID;
    }

    public int getResortID() {
        return resortID;
    }

    public int getSeasonID() {
        return seasonID;
    }

    public int getDayID() {
        return dayID;
    }

    public int getLiftID() {
        return liftID;
    }

    public int getTime() {
        return time;
    }



    public static LiftInfo fromJsonStr(String jsonStr) {
        Gson gson = new Gson();
        try {
            LiftInfo liftInfo = gson.fromJson(jsonStr, LiftInfo.class);
            return liftInfo;
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot turn message string to LiftInfo - " + e);
        }
    }

    @Override
    public String toString() {
        return "LiftInfo{" +
                "resortID=" + resortID +
                ", seasonID=" + seasonID +
                ", dayID=" + dayID +
                ", skierID=" + skierID +
                ", liftID=" + liftID +
                ", time=" + time +
                '}';
    }

    public String getSkiersDBValue() {
        return String.format("(%d, %d, %d)", this.skierID, this.dayID, this.liftID);
    }

    public String getResortsDBValue() {
        return String.format("(%d, %d, %d)", this.resortID, this.dayID, this.time);
    }

    public static void main(String[] args) {
        LiftInfo liftInfo = new LiftInfo(1, 3, 4, 5, 12, 24);
        System.out.println(liftInfo.getSkiersDBValue());
    }
}
