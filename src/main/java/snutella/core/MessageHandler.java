package snutella.core;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    
    private DatagramSocket socket;

    public MessageHandler(DatagramSocket socket) {
        this.socket = socket;
        this.buffer = new byte[BUFFER_SIZE];
    }
    
    public void run()  {
        try {
            this.listenForMessages();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    public void listenForMessages() throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Message received: " + response);
        }
    }
}