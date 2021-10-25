import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Main {
    private final static String HOST = "localhost";
    private final static int NUM_THREADS = 5;
    private static ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        ConcurrentHashMap<Integer, List<LiftInfo>> liftInfoMap = new ConcurrentHashMap<>();

        final Connection connection = connectionFactory.newConnection();

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread worker = new Thread(new Worker(connection, liftInfoMap));
            executorService.submit(worker);
        }
    }
}
