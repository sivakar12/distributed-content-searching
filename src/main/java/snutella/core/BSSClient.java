package snutella.core;

import java.util.List;
import java.util.ArrayList;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BSSClient {

    private final int BUFFER_SIZE = 1024;
    private final int TIMEOUT = 2000;
    private String address = "localhost";
    private int port = 55555;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private DatagramSocket socket;

    public static BSSClient getInstance() {
        return new BSSClient("localhost", 55555);
    }

    private BSSClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private String addLength(String message) {
        int length = message.length();
        String paddedLength = String.format("%04d", length);
        return paddedLength + " " + message;
    }

    private String sendMessage(String message, String address, int port) {
        message = addLength(message);
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] messageBytes = message.getBytes();
            InetAddress inetAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(messageBytes, 
                messageBytes.length, inetAddress, port);
            socket.send(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(TIMEOUT);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            socket.close();
            return received;
        } catch (Exception e) {
            System.err.println("Error: " + e);
            return "ERROR!";
        }
    }

    public List<Neighbor> register(String address, int port, String username) {
        String message = String.format("REG %s %d %s", 
            address, port, username);
        String response = sendMessage(message, this.address, this.port);
        String[] tokens = response.split(" ");

        List<Neighbor> neighbors = new ArrayList<Neighbor>();
        for (int i = 3; i < tokens.length; i+=2) {
            Neighbor newNeighbor = new Neighbor(tokens[i], Integer.parseInt(tokens[i+1]));
            neighbors.add(newNeighbor);
        }
        return neighbors;
    }

    public boolean unregister(String address, int port, String username) {
        String message = String.format("UNREG %s %d %s",
            address, port, username);
        String response = sendMessage(message, this.address, this.port);
        if (response.equals("ERROR!")) {
            return false;
        } else {
            return true;
        }
    }
}