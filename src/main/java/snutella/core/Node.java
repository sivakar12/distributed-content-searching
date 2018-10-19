package snutella.core; 

import java.util.List;
import java.util.ArrayList;


public class Node {
    
    private String username;
    private int port;
    private String address;

    private List<Neighbor> neigibors;
    private BSSClient bssClient;

    public Node(String address, int port) {
        this.username = "team19";
        this.address = "localhost";
        this.port = port;
        this.bssClient = BSSClient.getInstance();
        this.listenForMessages();
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
            Thread thread = new MessageHandler(this.address, this.port);
        } catch (Exception e) {
            System.err.println(e);  
        }
    }
}