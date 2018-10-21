package snutella.core; 

import java.util.List;
import java.util.ArrayList;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    
    private String username;
    private int port;
    private String address;

    private List<Neighbor> neigibors;
    private BSSClient bssClient;

    private DatagramSocket socket;

    public Node(String address, int port) {
        this.username = "team19";
        this.address = "localhost";
        this.port = port;

        try {
            this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.address));
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
        this.neigibors = this.bssClient.register(this.address, this.port, this.username);
        System.out.println(this.neigibors);
    }

    public void unregisterFromBSServer() {
        boolean response = this.bssClient.unregister(this.address, this.port, this.username);
        System.out.println(response);
    }

    public void listenForMessages() {
        try {
            Thread thread = new MessageHandler(this.socket);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);  
        }
    }
    public void sendPings() {
        try {
            Thread thread = new PingSender(this.socket, this.neigibors);
            thread.start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}