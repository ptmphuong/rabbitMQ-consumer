import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable{
    private final static String QUEUE_NAME = "LiftInfo";
    private Connection connection;
    private QueryBuilder queryBuilder;
    private String dbName;

    public Worker(Connection connection, QueryBuilder queryBuilder, String dbName) {
        this.connection = connection;
        this.queryBuilder = queryBuilder;
        this.dbName = dbName;
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
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                processMessage(message);
            };
            // process messages
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processMessage(String message) {
        LiftInfo liftInfo = LiftInfo.fromJsonStr(message);
        if (this.dbName.equals(SqlScript.SKIERS_TABLE_NAME)) {
            this.queryBuilder.addInsertQuery(liftInfo.getSkiersDBValue());
        } else if (this.dbName.equals(SqlScript.RESORTS_TABLE_NAME)) {
            this.queryBuilder.addInsertQuery(liftInfo.getResortsDBValue());
        }
//        System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
    }


}
