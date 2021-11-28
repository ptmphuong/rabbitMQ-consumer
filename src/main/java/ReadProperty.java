import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ReadProperty {

    private final static Logger logger = Logger.getLogger(ReadProperty.class.getName());

    public static Properties load(String fileName) throws IOException {
        Properties prop = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(fileName);

        if (input.equals(null)) throw new IOException("cannot find " + fileName);
        try {
//            InputStream input = new FileInputStream("config.properties");
            prop.load(input);
        } catch (IOException e) {
            logger.info("cannot load " + fileName);
            e.printStackTrace();
        }
        return prop;
    }

    public static Properties loadRmqConfig() throws IOException {
        String configName = "config.properties";
        return load(configName);
    }

    public static Properties loadSkiersDBConfig() throws IOException {
        String configName = "skiersdb.properties";
        return load(configName);
    }

    public static Properties loadResortsDBConfig() throws IOException {
        String configName = "resortsdb.properties";
        return load(configName);
    }

}
