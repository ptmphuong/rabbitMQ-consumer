import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ReadProperty {

    private final static Logger logger = Logger.getLogger(ReadProperty.class.getName());
    public static Properties load() throws IOException {
        Properties prop = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("config.properties");

        if (input.equals(null)) throw new IOException("cannot find config.properties file");
        try {
//            InputStream input = new FileInputStream("config.properties");
            prop.load(input);
        } catch (IOException e) {
            logger.info("cannot load config");
            e.printStackTrace();
        }
        return prop;
    }

}
