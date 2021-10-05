package scenarioSimplifier.utils;

import ads.ADSScenario;
import applicationProperties.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import logger.CustomLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.FileHandler;

public class Utils {
    public static final String ACCEPTED_FOLDER_NAME = "accepted";
    public static final String GENERATED_FOLDER_NAME = "generated";
    public static final String LOGS_FOLDER_NAME = "logs";
    public static final String LOG_FILENAME = "log.txt";

    public static String saveScenarioToTempFile(ADSScenario scenario, String prefix) throws IOException {
        return saveScenarioToFile(scenario, File.createTempFile(prefix, ".json"));
    }

    public static String saveScenarioToFile(ADSScenario scenario, Path scenarioPath) throws IOException {
        return saveScenarioToFile(scenario, scenarioPath.toFile());
    }

    public static String saveScenarioToFile(ADSScenario scenario, String scenarioPath) throws IOException {
        return saveScenarioToFile(scenario, new File(scenarioPath));
    }

    public static String saveScenarioToFile(ADSScenario scenario, File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, scenario);
        return file.getAbsolutePath();
    }

    public static void setupLoggerAndPathPP() {
        CustomLogger.configureLogger();
        Map<String, Object> properties = ApplicationProperties.getApplicationProperties(null, false);
        //PathPlannerRunner.exeRunnerPath = (String) properties.get("exeRunnerPath");
        //PathPlannerRunner.pathPlannerPath = (String) properties.get("pathPlannerPath");
    }

    public static void closeFileHandler() {
        Arrays.stream(CustomLogger.getLogger().getHandlers()).forEach(handler -> {
            if (handler instanceof FileHandler) {
                handler.close();
            }
        });
    }
}
