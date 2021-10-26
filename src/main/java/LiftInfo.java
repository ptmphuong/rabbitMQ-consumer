import java.util.Properties;

public class LiftInfo {
    private Integer liftID;
    private Integer time;

    public LiftInfo(Integer liftID, Integer time) {
        this.liftID = liftID;
        this.time = time;
    }

    @Override
    public String toString() {
        return "LiftInfo{" +
                "liftID=" + liftID +
                ", time=" + time +
                '}';
    }

    public static void main(String[] args) {
        Properties properties = ReadProperty.load();
        String ip = properties.getProperty("ip");
        System.out.println(ip);
    }
}
