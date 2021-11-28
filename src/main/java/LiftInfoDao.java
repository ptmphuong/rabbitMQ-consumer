import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class LiftInfoDao {
    private final static Logger logger = Logger.getLogger(LiftInfoDao.class.getName());
    private static HikariDataSource dataSource;
    private String tableName;

    public LiftInfoDao(String tableName) {
        System.out.println("creating liftInfoDao");
        this.tableName = tableName;
        dataSource = DataSource.getDataSource(tableName);
        System.out.println("set dataSource");
    }

    public void createTable(String tableName) {
        String createTableQuery;
        if (tableName.equals(SqlScript.SKIERS_TABLE_NAME)) createTableQuery = SqlScript.createSkiersTable;
        else if (tableName.equals(SqlScript.RESORTS_TABLE_NAME)) createTableQuery = SqlScript.createResortsTable;
        else throw new IllegalArgumentException("Invalid table name");
        executeQuery(createTableQuery);
    }

    public void createLiftInfo(LiftInfo liftInfo) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO " + tableName +
                " (skierID, dayID, liftId) " +
                "VALUES (?,?,?)";

        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, liftInfo.getSkierID());
            preparedStatement.setInt(2, liftInfo.getDayID());
            preparedStatement.setInt(3, liftInfo.getLiftID());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
            System.out.println("LiftInfo sent to db");
        } catch (SQLException e) {
            logger.info("cannot send to db: " + liftInfo + ". Error: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                logger.info("cannot send to db: " + liftInfo + ". Error: " + e.getMessage());
            }
        }
    }

    public void emptyTable() {
        String emptyTableQuery = "TRUNCATE TABLE " + tableName;
        executeQuery(emptyTableQuery);
    }

    public void executeQuery(String query) {
        long start = System.currentTimeMillis();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            long end = System.currentTimeMillis();
            long time = (end - start) / 1000;
            logger.info(String.format("Write success. Execute time: %d", time));
            conn.close();
        } catch (SQLException e) {
            long end = System.currentTimeMillis();
            long time = (end - start) / 1000;
            logger.info("cannot send to db. Time: " + time + ". Error: " + e.getMessage());
        }
    }
}
