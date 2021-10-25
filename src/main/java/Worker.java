import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable{
    private final static String QUEUE_NAME = "LiftInfo";
    private Connection connection;
    private ConcurrentHashMap<String, String> liftInfoMap;

    public Worker(Connection connection, ConcurrentHashMap<String, String> liftInfoMap) {
        this.connection = connection;
        this.liftInfoMap = liftInfoMap;
    }

    @Override
    public void run() {
        try {
            final Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // max one message per receiver
            channel.basicQos(1);
            System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                this.liftInfoMap.put(message, "some value");
                System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
                // do work
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            // process messages
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
