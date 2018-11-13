package snutella;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogsManager {
    private List<LogMessage> logs;
    private LogsManager instance;

    private LogsManager() {
        this.logs = new ArrayList<>();
    }

    public synchronized LogsManager getInstance() {
        if (this.instance == null) {
            this.instance = new LogsManager();
        }
        return this.instance;
    }

    public void log(LogMessage item) {
        this.logs.add(item);
    }
    public List<LogMessage> getLogs() {
        return logs;
    }

    public List<LogMessage> filterByType(List<LogMessageType> types) {
        return this.logs.stream().filter(
                logMessage -> types.stream().anyMatch(
                        t -> logMessage.getMessageType() == t
                )
        ).collect(Collectors.toList());
    }
}
