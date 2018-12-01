package snutella.stats;

import java.io.*;
import java.net.InetAddress;
import java.util.Date;

public class ResponseTimeLogger {
    private static ResponseTimeLogger ourInstance = new ResponseTimeLogger();

    private File logFile;
    private Date querySentTime;
    private String queryString;

    public static ResponseTimeLogger getInstance() {
        return ourInstance;
    }

    private ResponseTimeLogger() {
        logFile = new File("query-response-times.csv");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                BufferedWriter fileOut = new BufferedWriter(new FileWriter(logFile));
                fileOut.write("SourceNode,RespondingNode,StartTime,EndTime,Duration(ms),QueryName,FileName\n");
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendQuery(String queryString) {
        this.queryString = queryString;
        this.querySentTime = new Date();
    }

    public void logResponse(InetAddress sourceAddress, int sourcePort,
            InetAddress destinationAddress, int destinationPort,
            Date receivingTime, String fileName) {
        String sourceDetail = "\"" + sourceAddress.getHostAddress() + ":" + sourcePort +"\"";
        String responderDetail = "\"" + destinationAddress.getHostAddress() + ":" + destinationPort +"\"";
        long duration = receivingTime.getTime() - querySentTime.getTime();
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(sourceDetail + "," + responderDetail + "," + querySentTime + "," +
                    receivingTime + "," + duration + "," + queryString + "," + fileName + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

