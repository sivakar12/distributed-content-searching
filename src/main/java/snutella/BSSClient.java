package snutella;

import snutella.neighbors.Neighbor;

import java.util.List;
import java.util.ArrayList;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BSSClient {

    private final int BUFFER_SIZE = 1024;
    private final int TIMEOUT = 2000;
    private InetAddress address;
    private int port;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private DatagramSocket socket;


    public BSSClient(String address, int port) throws Exception {
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    private String addLength(String message) {
        int length = message.length();
        String paddedLength = String.format("%04d", length);
        return paddedLength + " " + message;
    }

    private String sendMessage(String message, InetAddress address, int port)
            throws Exception {
        message = addLength(message);
        DatagramSocket socket = new DatagramSocket();
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes,
            messageBytes.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(TIMEOUT);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        socket.close();
        return received;
    }

    public List<Neighbor> register(InetAddress address, int port,
                                   String username) throws Exception {
        String message = String.format("REG %s %d %s", 
            address.getHostAddress(), port, username);
        String response = sendMessage(message, this.address, this.port);
        String[] tokens = response.split(" ");

        List<Neighbor> neighbors = new ArrayList<Neighbor>();
        for (int i = 3; i < tokens.length; i+=2) {
            try {
                Neighbor newNeighbor = new Neighbor(InetAddress.getByName(tokens[i]), 
                    Integer.parseInt(tokens[i+1]));
                neighbors.add(newNeighbor);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return neighbors;
    }

    public void unregister(InetAddress address, int port, String username)
            throws Exception{
        String message = String.format("UNREG %s %d %s",
            address.getHostAddress(), port, username);
        sendMessage(message, this.address, this.port);

    }
}