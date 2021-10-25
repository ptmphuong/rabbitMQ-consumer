import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static String HOST = "localhost";
    private final static int NUM_THREADS = 5;
    private static ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        ConcurrentHashMap<String, String> liftInfoMap = new ConcurrentHashMap<>();

        final Connection connection = connectionFactory.newConnection();

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread worker = new Thread(new Worker(connection, liftInfoMap));
            executorService.submit(worker);
        }


    }
}
