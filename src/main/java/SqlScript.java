public class SqlScript{
    public final static String SKIERS_TABLE_NAME = "skiers";
    public final static String RESORTS_TABLE_NAME = "resorts";

    public static final String createSkiersTable = "CREATE TABLE skiers (" +
            "skierID int, " +
            "dayID int, " +
            "liftID int" +
            ");";
    public static final String createResortsTable = "CREATE TABLE resorts (" +
            "resortID int, " +
            "dayID int, " +
            "timeID int" +
            ");";

    public static final String INSERT_INTO_SKIERS_HEAD = "INSERT INTO skiers (skierID, dayID, liftId) VALUES";
    public static final String INSERT_INTO_RESORTS_HEAD = "INSERT INTO resorts (resortID, dayID, timeID) VALUES";
}
