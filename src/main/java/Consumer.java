import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Consumer {

    private final static Logger logger = Logger.getLogger(Consumer.class.getName());
    private static final int MAX_DB_CONNECTION = 64;
    private static final int DEFAULT_NUM_THREADS = 64;
    private final static String LOCAL_HOST = "localhost";
    private String dbTableName;

    private boolean createTable;

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(MAX_DB_CONNECTION);
    private LiftInfoDao liftInfoDao;

    private int workersPerQueryBuilder;
    private int nThreads;
    private String RABBIT_HOST;
    private String RABBIT_USERNAME;
    private String RABBIT_PASSWORD;
    private final Connection rmqConnection;

    private List<QueryBuilder> queryBuilderList = new ArrayList<>();

    Consumer(int numThreads, boolean createTableInput, String dbTableName) throws IOException {
        logger.info("Start main. Initialize values");
        this.nThreads = numThreads;
        this.createTable = createTableInput;
        this.dbTableName = dbTableName;
        this.liftInfoDao = new LiftInfoDao(dbTableName);

        // set values
        setQueryBuilderList();
        setRMQProperty();
        rmqConnection = setRMQConnection(new ConnectionFactory());
        workersPerQueryBuilder = nThreads/MAX_DB_CONNECTION;
        executorService = Executors.newFixedThreadPool(nThreads);
    }

    public void run() {
        // check if empty table is needed
        if (createTable) {
//            liftInfoDao.createTable(dbTableName);
        }

        // empty all rows in the existing table
        liftInfoDao.emptyTable();

        // submit consuming workers (RMQ -> QueryBuilderList)
        logger.info("Summited consuming workers");
        for (int i = 0; i < nThreads; i++) {
            Worker worker = new Worker(rmqConnection, queryBuilderList.get(i/workersPerQueryBuilder), dbTableName);
            executorService.submit(worker);
        }

        // scheduled workers to go check queryBuilders in queryBuilderList and send to db
        logger.info("Starting query checkers and send to db");
        for (int i = 0; i < MAX_DB_CONNECTION; i++) {
            int finalI = i;
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                QueryBuilder qb = queryBuilderList.get(finalI);
                checkQueryBuilderAndSendToDB(qb);
            }, /*initial delay*/ 0, /*period*/ 2000, MILLISECONDS);
        }
    }

    public static void main(String[] args) throws IOException {
        // arg[0]: num threads
        // arg[1]: create table?

        // collect input
        Integer numThreads = null;

        if (args.length > 0) {
            try {
                numThreads = Integer.valueOf(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (numThreads.equals(null)) numThreads = DEFAULT_NUM_THREADS;
        boolean createTableInput = args.length > 1 ? true : false;
        System.out.println(numThreads);

        Consumer consumer = new Consumer(numThreads, createTableInput, SqlScript.RESORTS_TABLE_NAME);
        consumer.run();
    }

    private void checkQueryBuilderAndSendToDB(QueryBuilder qb) {
        if (!qb.isEmpty()) {
//                    System.out.println(String.format("bag size: %d", qb.getCount()));
            String query = qb.getAndClearQuery();
            this.liftInfoDao.executeQuery(query);
        }
    }

    private void setRMQProperty() throws IOException {
        Properties properties = ReadProperty.loadRmqConfig();
        RABBIT_HOST = properties.getProperty("ip");
        if (!RABBIT_HOST.equals(LOCAL_HOST)) {
            RABBIT_USERNAME = properties.getProperty("rabbit_username");
            RABBIT_PASSWORD = properties.getProperty("rabbit_password");
        }
        System.out.println("set prop success");
    }

    private Connection setRMQConnection(ConnectionFactory connectionFactory) {
        connectionFactory.setHost(RABBIT_HOST);
        if (!RABBIT_HOST.equals("localhost")) {
            connectionFactory.setUsername(RABBIT_USERNAME);
            connectionFactory.setPassword(RABBIT_PASSWORD);
        }
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException e) {
            logger.info("IOException when setting up new connection to: " + RABBIT_HOST);
            e.printStackTrace();
        } catch (TimeoutException e) {
            logger.info("Timeout when setting up new connection to: " + RABBIT_HOST);
            e.printStackTrace();
        }
        return connection;
    }

    private void setQueryBuilderList() {
        for (int i = 0; i < MAX_DB_CONNECTION; i++) {
            queryBuilderList.add(new QueryBuilder(dbTableName));
        }
    }
}
