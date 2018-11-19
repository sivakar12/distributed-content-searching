package snutella;

import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListManager;

import java.net.UnknownHostException;
import java.util.List;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    private SocketManager socketManager;

    private String username;

    private NeighborListManager neighborManager;
    private FileManager fileManager;
    private BSSClient bssClient;
    private MessageHandler messageHandler;
    private PingSender pingSender;

    public Node(String address, int port, String bssAddress, int bssPort) {
        this.username = "team19";
        this.neighborManager = new NeighborListManager();
        this.fileManager = new FileManager();

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        try {
            DatagramSocket socket = new DatagramSocket(port, inetAddress);
            this.socketManager = new SocketManager(socket);
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
        return this.socketManager.getAddress();
    }
    public int getPort() {
        return this.socketManager.getPort();
    }

    public void registerToBSServer() throws Exception {

        List<Neighbor> neighbors = this.bssClient.register(
                this.getAddress(), this.getPort(), this.username);
        neighborManager.addNeighbors(neighbors);
        System.out.println("Neighbors: " + neighbors);
    }

    public void unregisterFromBSServer() throws Exception {
        this.bssClient.unregister(this.getAddress(), this.getPort(), this.username);
    }

    public void listenForMessages() {
        try {
            this.messageHandler = new MessageHandler(this.socketManager, this.neighborManager);
            this.messageHandler.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }
    public void sendPings() {
        try {
            this.pingSender = new PingSender(this.socketManager, this.neighborManager);
            this.pingSender.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public NeighborListManager getNeighborManager() {
        return neighborManager;
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
    public void stop() {
        try {
            this.unregisterFromBSServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.messageHandler.interrupt();
        this.pingSender.interrupt();
    }
}