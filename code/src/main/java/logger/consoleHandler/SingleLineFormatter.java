package logger.consoleHandler;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}
