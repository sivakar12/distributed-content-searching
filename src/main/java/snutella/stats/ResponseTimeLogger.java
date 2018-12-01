package snutella.stats;

import java.io.*;

public class ResponseTimeLogger {
    private static ResponseTimeLogger ourInstance = new ResponseTimeLogger();

    private File logFile;
    public static ResponseTimeLogger getInstance() {
        return ourInstance;
    }

    private ResponseTimeLogger() {
        logFile = new File("query-response-times.csv");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                BufferedWriter fileOut = new BufferedWriter(new FileWriter(logFile));
                fileOut.write("Node,StartTime,EndTime,Duration,QueryName");
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

