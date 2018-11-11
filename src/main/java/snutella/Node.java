package snutella;

import java.net.UnknownHostException;
import java.util.List;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    private DatagramSocket socket;

    private String username;

    private List<Neighbor> neighbors;
    private BSSClient bssClient;

    public Node(String address, int port, String bssAddress, int bssPort) {
        this.username = "team19";

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.socket = new DatagramSocket(port, inetAddress);
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

    public InetAddress getAddress() {
        return this.socket.getLocalAddress();
    }
    public int getPort() {
        return this.socket.getLocalPort();
    }

    public void registerToBSServer() throws Exception {

        this.neighbors = this.bssClient.register(this.getAddress(), this.getPort(), this.username);
        System.out.println("Neighbors: " + this.neighbors);
    }

    public void unregisterFromBSServer() throws Exception {
        this.bssClient.unregister(this.getAddress(), this.getPort(), this.username);
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
            System.err.println("Input port, and bootstrap sever config correctly");
            return;
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        String bssAddress = args[2];
        int bssPort = Integer.parseInt((args[3]));

        new Node(address, port, bssAddress, bssPort);

    }
}