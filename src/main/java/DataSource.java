import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DataSource {
    private final static Logger logger = Logger.getLogger(DataSource.class.getName());
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String DATABASE;
    private static String HOST_NAME;
    private static String PORT;
    private static String USERNAME;
    private static String PASSWORD;
    private static String url;

    private static void setDbProperties(String dbName) {
        Properties properties;
        try {
            if (dbName.equals(SqlScript.SKIERS_TABLE_NAME)) properties = ReadProperty.loadSkiersDBConfig();
            else properties = ReadProperty.loadResortsDBConfig();
            DATABASE = properties.getProperty("mysql_database");
            HOST_NAME = properties.getProperty("mysql_endpoint");
            PORT = properties.getProperty("mysql_port");
            USERNAME = properties.getProperty("mysql_username");
            PASSWORD = properties.getProperty("mysql_password");
            url = String
                    .format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("cannot load db config");
        }
    }

    private static void setDsConfig() {
        config.setJdbcUrl(url);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName(JDBC_DRIVER);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        ds = new HikariDataSource(config);
        ds.setMaximumPoolSize(100);
    }

//    static {
//        Properties properties;
//        try {
//            properties = ReadProperty.loadSkiersDBConfig();
//            String DATABASE = properties.getProperty("mysql_database");
//            String HOST_NAME = properties.getProperty("mysql_endpoint");
//            String PORT = properties.getProperty("mysql_port");
//            String USERNAME = properties.getProperty("mysql_username");
//            String PASSWORD = properties.getProperty("mysql_password");
//            String url = String
//                    .format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
//            config.setJdbcUrl(url);
//            config.setUsername(USERNAME);
//            config.setPassword(PASSWORD);
//            config.setDriverClassName(JDBC_DRIVER);
//            config.addDataSourceProperty("cachePrepStmts", "true");
//            config.addDataSourceProperty("prepStmtCacheSize", "250");
//            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//            config.addDataSourceProperty("useServerPrepStmts", true);
//            config.addDataSourceProperty("rewriteBatchedStatements", true);
//            config.addDataSourceProperty("cacheResultSetMetadata", true);
//            config.addDataSourceProperty("cacheServerConfiguration", true);
//            ds = new HikariDataSource(config);
//            ds.setMaximumPoolSize(100);
////            ds.setConnectionTimeout(30000);
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info("cannot load hikari config");
//        }
//    }

    public static HikariDataSource getDataSource(String dbName) {
        setDbProperties(dbName);
        setDsConfig();
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
