import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable{
    private final static String QUEUE_NAME = "LiftInfo";
    private final static String EXCHANGE_NAME = "LiftInfoExchange";

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
            System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//            String qName = channel.queueDeclare().getQueue();
            channel.queueDeclare(dbName, true, false, false, null);
            channel.queueBind(dbName, EXCHANGE_NAME, "");
            channel.basicQos(1); // max one message per receiver

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                processMessage(message);
            };
            // process messages
            channel.basicConsume(dbName, false, deliverCallback, consumerTag -> { });
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
