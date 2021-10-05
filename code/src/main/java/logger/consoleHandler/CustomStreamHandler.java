package logger.consoleHandler;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class CustomStreamHandler extends StreamHandler {

    public CustomStreamHandler() {
        super(System.out, new SingleLineFormatter());
        this.setLevel(Level.ALL);
    }

    @Override
    public void publish(LogRecord record) {
        record.setMessage(record.getMessage());
        super.publish(record);
        super.flush();
    }
}
