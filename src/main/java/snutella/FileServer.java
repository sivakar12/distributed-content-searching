package snutella;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private static FileServer instance;
    private ServerSocket serverSocket;

    private FileServer() {
        try {
            serverSocket = new ServerSocket();
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

    public void start() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                (new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
