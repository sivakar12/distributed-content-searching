package snutella;

import java.util.List;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class Node {
    
    private String username;
    private int port;
    private InetAddress address;

    private List<Neighbor> neighbors;
    private BSSClient bssClient;

    private DatagramSocket socket;

    public Node(String address, int port, String bssAddress, int bssPort) {
        this.username = "team19";
        try {
            this.address = InetAddress.getByName(address);
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
        this.port = port;

        try {
            this.socket = new DatagramSocket(this.port, this.address);
        } catch (Exception e) {
            System.err.println(e);
            return;
        }

        try {
            this.bssClient = new BSSClient(bssAddress, bssPort);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            this.registerToBSServer();
        } catch (Exception e) {
            System.err.println("Error registering with Bootstrap server");
            e.printStackTrace();
            return;

        }
        this.listenForMessages();
        this.sendPings();
    }
    public void setBSSClient(BSSClient client) {
        this.bssClient = client;
    }

    public void registerToBSServer() throws Exception {
        this.neighbors = this.bssClient.register(this.address, this.port, this.username);
        System.out.println("Neighbors: " + this.neighbors);
    }

    public void unregisterFromBSServer() throws Exception {
        this.bssClient.unregister(this.address, this.port, this.username);
    }

    public void listenForMessages() {
        try {
            Thread thread = new MessageHandler(this.socket, this.neighbors);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }
    public void sendPings() {
        try {
            Thread thread = new PingSender(this.socket, this.neighbors);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }
    @Override
    public void finalize() {
        this.socket.disconnect();
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Input IP address and port number correctly");
            return;
        }

        String ipAddress = args[0];
        int port = Integer.parseInt(args[1]);
        String bssAddress = args[2];
        int bssPort = Integer.parseInt((args[3]));

        new Node(ipAddress, port, bssAddress, bssPort);

    }
}