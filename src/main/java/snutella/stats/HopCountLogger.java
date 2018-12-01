package snutella.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
                BufferedWriter fileOut = new BufferedWriter(new FileWriter(logFile));
                fileOut.write("Node,Query,Hops");
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
