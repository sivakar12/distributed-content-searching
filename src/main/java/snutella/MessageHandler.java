package snutella;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    
    private List<Neighbor> neighbors;
    private DatagramSocket socket;

    public MessageHandler(DatagramSocket socket, List<Neighbor> neighbors) {
        this.socket = socket;
        this.buffer = new byte[BUFFER_SIZE];
        this.neighbors = neighbors;
    }
    
    public void run()  {
        try {
            this.listenForMessages();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void handlePing(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        System.out.println("Ping received from " + address.getHostName() + ":" + port);
    }
    public void listenForMessages() throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            if (response.startsWith("PING")) {           // TODO: Remove hardcoded string
                handlePing(packet);
            }
            System.out.println("Message received: " + response);
        }
    }
}