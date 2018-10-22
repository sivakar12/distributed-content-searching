package snutella.core; 

import java.util.List;
import java.util.ArrayList;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    
    private String username;
    private int port;
    private InetAddress address;

    private List<Neighbor> neighbors;
    private BSSClient bssClient;

    private DatagramSocket socket;

    public Node(String address, int port) {
        this.username = "team19";
        try {
            this.address = InetAddress.getByName(address);
        } catch (Exception e) {
            System.err.println(e);
        }
        this.port = port;

        try {
            this.socket = new DatagramSocket(this.port, this.address);
        } catch (Exception e) {
            System.err.println(e);
        }

        this.bssClient = BSSClient.getInstance();
        this.registerToBSServer();
        this.listenForMessages();
        this.sendPings();
    }
    public void setBSSClient(BSSClient client) {
        this.bssClient = client;
    }

    public void registerToBSServer() {
        this.neighbors = this.bssClient.register(this.address, this.port, this.username);
        System.out.println("Neighbors: " + this.neighbors);

    }

    public void unregisterFromBSServer() {
        boolean response = this.bssClient.unregister(this.address, this.port, this.username);
        System.out.println(response);
    }

    public void listenForMessages() {
        try {
            Thread thread = new MessageHandler(this.socket, this.neighbors);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);  
        }
    }
    public void sendPings() {
        try {
            Thread thread = new PingSender(this.socket, this.neighbors);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}