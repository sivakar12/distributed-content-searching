import java.util.List;
import java.util.ArrayList;


public class Node {
    private String username;
    private int port;
    private String address;

    private List<Neighbor> neigibors;
    private BSSClient bssClient;

    public Node(int port) {
        this.username = "team19";
        this.address = "localhost";
        this.port = port;
        this.bssClient = BSSClient.getInstance();
    }
    public Node(String address, int port) {
        this.address = address;
        this.port = port;
    }
    public void setBSSClient(BSSClient client) {
        this.bssClient = client;
    }

    public void registerToBSServer() {
        String response = this.bssClient.register(this.address, this.port, this.username);
        System.out.println(response);
    }

    public void unregisterFromBSServer() {
        String response = this.bssClient.unregister(this.address, this.port, this.username);
        System.out.println(response);
    }
    public static void main(String[] args) {
        Node node = new Node(88888);
        node.registerToBSServer();
        
        Node node2 = new Node(88889);
        node2.registerToBSServer();
        node.unregisterFromBSServer();
        node2.unregisterFromBSServer();
    }
}