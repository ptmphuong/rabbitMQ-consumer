import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());
    private static String HOST = "18.206.249.74";
    private static String RABBIT_USERNAME = "carrot";
    private static String RABBIT_PASSWORD = "carrot";
    private static int NUM_THREADS = 5;
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException, TimeoutException {
        logger.info("Start main. Initialize values");
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
