package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class FileDownloader extends Thread {
    private InetAddress address;
    private int port;
    private String filename;

    private static final String DESTINATION_FOLDER = "downloads";

    public FileDownloader(InetAddress address, int port, String filename) {
        this.address = address;
        this.port = port;
        this.filename = filename;
    }

    public void run() {
        File outputFile = new File(DESTINATION_FOLDER, filename);
        outputFile.getParentFile().mkdirs();
        try {
            outputFile.createNewFile();
            Socket socket = new Socket(address, port);
            LogMessage log = new LogMessage(false, LogMessageType.DOWNLOAD,
                    socket.getLocalAddress(), socket.getLocalPort(), this.address,
                    this.port, new Date(), "GET " + filename);
            LogsManager.getInstance().log(log);
            InputStream socketInputStream = socket.getInputStream();
            OutputStream socketOutputStream = socket.getOutputStream();
            socketOutputStream.write((filename + "\n").getBytes());

            OutputStream fileOutputStream = new FileOutputStream(outputFile);

            int in;
            byte[] buffer = new byte[8 * 1024];

            while ((in = socketInputStream.read(buffer)) > 0) {
                System.out.println(new String(buffer));
                fileOutputStream.write(buffer);
            }
            fileOutputStream.close();
            socketInputStream.close();
            socketOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
