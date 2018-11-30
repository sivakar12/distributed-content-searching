package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class FileServer extends Thread {
    private static FileServer instance;
    private ServerSocket serverSocket;
    private static final String FILE_DIRECTORY = "files";

    private FileServer() {

        int randomPort = new Random().nextInt(0xFFFF);
        try {
            serverSocket = new ServerSocket(randomPort);
            System.out.println("File server started at port " +
                    serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static FileServer getInstance() {
        if (instance == null) {
            instance = new FileServer();
        }
        return instance;
    }

    public void run() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                (new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    public void stop() {
//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        public void run() {
            try {
                BufferedReader socketIn = new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream()));
                OutputStream socketOut = clientSocket.getOutputStream();

                String filename = socketIn.readLine();

                LogMessage log = new LogMessage(true, LogMessageType.DOWNLOAD,
                        clientSocket.getInetAddress(), clientSocket.getPort(),
                        clientSocket.getLocalAddress(), clientSocket.getLocalPort(),
                        new Date(), "GET " + filename);
                LogsManager.getInstance().log(log);

                System.out.println("File: " + filename);
                System.out.println("Serving file " + filename);
                File file = new File(FILE_DIRECTORY, filename);
                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    socketOut.write((new String(buffer)).getBytes(), 0, bytesRead);
                }
                fileInputStream.close();
                socketIn.close();
                socketOut.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error serving file");
                e.printStackTrace();
            }
        }

    }

    public int getServingPort() {
        return this.serverSocket.getLocalPort();
    }
}
