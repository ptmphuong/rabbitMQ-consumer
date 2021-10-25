import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable{
    private final static String QUEUE_NAME = "LiftInfo";
    private Connection connection;
    private ConcurrentHashMap<Integer, List<LiftInfo>> liftInfoMap;

    public Worker(Connection connection, ConcurrentHashMap<Integer, List<LiftInfo>> liftInfoMap) {
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
                processMessage(message);
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

    private void processMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        Integer skierID = jsonObject.getInt("skierID");
        Integer liftID = jsonObject.getInt("liftID");
        Integer time = jsonObject.getInt("time");
        LiftInfo liftInfo = new LiftInfo(liftID, time);

        List<LiftInfo> liftInfoList = this.liftInfoMap.getOrDefault(skierID, new ArrayList<>());
        liftInfoList.add(liftInfo);
        this.liftInfoMap.put(skierID, liftInfoList);
    }
}
