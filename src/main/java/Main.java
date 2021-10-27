import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());
    private static String HOST;
    private static String RABBIT_USERNAME;
    private static String RABBIT_PASSWORD;
    private static int NUM_THREADS = 5;
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException {
        logger.info("Start main. Initialize values");

        setProperty();
        try {
            Integer num_threads = Integer.valueOf(args[0]);
            NUM_THREADS = num_threads;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(NUM_THREADS);
        NUM_THREADS = 5;

        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        final Connection connection = setConnection(connectionFactory);

        ConcurrentHashMap<Integer, List<LiftInfo>> liftInfoMap = new ConcurrentHashMap<>();

        logger.info("Start threads");
        for (int i = 0; i < NUM_THREADS; i++) {
            Worker worker = new Worker(connection, liftInfoMap);
            executorService.submit(worker);
        }
    }

    private static void setProperty() throws IOException {
        Properties properties = ReadProperty.load();
        HOST = properties.getProperty("ip");
        RABBIT_USERNAME = properties.getProperty("rabbit_username");
        RABBIT_PASSWORD = properties.getProperty("rabbit_password");
    }

    private static Connection setConnection(ConnectionFactory connectionFactory) {
        connectionFactory.setHost(HOST);
        if (!HOST.equals("localhost")) {
            connectionFactory.setUsername(RABBIT_USERNAME);
            connectionFactory.setPassword(RABBIT_PASSWORD);
        }
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException e) {
            logger.info("IOException when setting up new connection to: " + HOST);
            e.printStackTrace();
        } catch (TimeoutException e) {
            logger.info("Timeout when setting up new connection to: " + HOST);
            e.printStackTrace();
        }
        return connection;
    }
}
