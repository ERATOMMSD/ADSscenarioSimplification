package applicationProperties;

import logger.CustomLogger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationProperties {
    private static final String DEFAULT_PROPERTIES_FILENAME = "properties-default.yaml";
    private static final String CUSTOM_PROPERTIES_FILENAME = "properties-custom.yaml";
    private static final Logger logger = CustomLogger.getLogger();
    private static Map<String, Object> properties;

    /**
     * Populates a property map with the result of loadApplicationProperties().
     * Doesn't load properties twice, instead returns the same object if it was already created,
     * UNLESS force == true, in which case we do read the properties file again from disk.
     *
     * @param force whether to read the file again from disk, in case it already exited.
     * @return the application properties map.
     */
    public static Map<String, Object> getApplicationProperties(String[] args, boolean force) {
        if (properties == null || force) {
            properties = loadApplicationProperties(args);
        }
        return properties;
    }

    /**
     * Loads properties stored in the default file (DEFAULT_PROPERTIES_FILENAME), and then
     * replaces existing ones from the custom file (CUSTOM_PROPERTIES_FILENAME).
     * After that, if any program arguments are present, they replace any of the above.
     */
    private static Map<String, Object> loadApplicationProperties(String[] args) {
        Yaml yaml = new Yaml();
        // read the default properties
        // note: the leading "/" is used to ignore this package and correctly find the file in the resources folder.
        Map<String, Object> properties = yaml.load(ApplicationProperties.class.getResourceAsStream("/" + DEFAULT_PROPERTIES_FILENAME));

        if (properties.isEmpty()) {
            logger.log(Level.SEVERE, "Could not read properties file '" + DEFAULT_PROPERTIES_FILENAME + "'!");
        }

        // read the custom properties, if it exists
        InputStream fileStream = ApplicationProperties.class.getResourceAsStream("/" + CUSTOM_PROPERTIES_FILENAME);
        if (fileStream != null) {
            Map<String, Object> customProperties = yaml.load(fileStream);
            // and replace any entries in the default properties
            properties.putAll(customProperties);
        }
        return properties;
    }
}
