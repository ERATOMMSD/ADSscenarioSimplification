package logger;

import logger.consoleHandler.CustomStreamHandler;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CustomLogger {
    private static final Logger logger = Logger.getLogger("ADSscenarioSimplifier");

    public static Logger getLogger() {
        return logger;
    }

    public static void logException(Throwable e) {
        logger.log(Level.SEVERE, e.toString());
        Arrays.stream(e.getStackTrace()).forEach(traceElement -> logger.log(Level.SEVERE, "\tat " + traceElement.toString()));
    }

    public static void configureLogger() {
        logger.setLevel(Level.INFO);
        setHandler();
    }

    private static void setHandler() {
        // remove all existing handlers
        Arrays.stream(logger.getHandlers()).forEach(handler -> logger.removeHandler(handler));
        Arrays.stream(logger.getParent().getHandlers()).forEach(handler -> logger.getParent().removeHandler(handler));
        // instead use a new handler with a custom formatter
        logger.addHandler(new CustomStreamHandler());
    }
}
