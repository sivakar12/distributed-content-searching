package snutella.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogsManager {
    private static LogsManager instance;

    private List<LogMessage> logs;
    private List<LogMessageListener> listeners;

    private LogsManager() {
        this.logs = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public static synchronized LogsManager getInstance() {
        if (instance == null) {
            instance = new LogsManager();
        }
        return instance;
    }
    public void addListener(LogMessageListener listener) {
        this.listeners.add(listener);
    }

    public void log(LogMessage item) {
        this.logs.add(item);
        for (LogMessageListener listener: this.listeners) {
            listener.onNewLogMessage(item);
        }
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
