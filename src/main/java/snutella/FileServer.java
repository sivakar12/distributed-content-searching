package snutella;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class FileServer extends Thread {
    private static FileServer instance;
    private ServerSocket serverSocket;

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
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();

                String filename = in.readLine();

                File file = new File("files", filename);
                FileInputStream fileInputStream = new FileInputStream(file);

                char[] buffer = new char[8 * 1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write((new String(buffer)).getBytes(), 0, len);
                }
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public int getServingPort() {
        return this.serverSocket.getLocalPort();
    }
}
