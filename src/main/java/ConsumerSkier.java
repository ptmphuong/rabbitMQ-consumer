import java.io.IOException;

public class ConsumerSkier {
    public static void main(String[] args) throws IOException {
        int numThreads = 128;
        boolean createTableInput = false;
        String dbTableName = SqlScript.RESORTS_TABLE_NAME;
        Consumer consumer = new Consumer(numThreads, createTableInput, dbTableName);
        consumer.run();
    }
}
