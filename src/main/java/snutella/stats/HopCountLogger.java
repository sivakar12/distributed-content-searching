package snutella.stats;

import java.io.*;
import java.net.InetAddress;

public class HopCountLogger {
    private static HopCountLogger ourInstance = new HopCountLogger();

    private File logFile;
    public static HopCountLogger getInstance() {
        return ourInstance;
    }

    private HopCountLogger() {
        logFile = new File("hop-counts.csv");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                FileWriter fileWriter = new FileWriter(logFile);
                fileWriter.write("Source,Destination,Query,Hops\n");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void logHops(InetAddress sourceAddress, int sourcePort, InetAddress destinationAddress,
                        int destinationPort, String query, int hops) {
        String sourceDetail = "\"" + sourceAddress.getHostAddress() + ":" + sourcePort + "\"";
        String destinationDetail = "\"" + destinationAddress.getHostAddress() + ":" + destinationPort + "\"";
        query = "\"" + query + "\"";
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(sourceDetail + ","  + destinationDetail + "," +
                    query + "," + hops + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
