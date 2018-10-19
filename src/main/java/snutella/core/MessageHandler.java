package snutella.core;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    
    private String address;
    private int port;

    private byte[] buffer;
    private DatagramSocket socket;

    public MessageHandler(String address, int port) {
        this.address = address;
        this.port = port;

        this.buffer = new byte[BUFFER_SIZE];
        try {
            this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.address));        
        } catch (Exception e) {
            System.err.println(e);
        }
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
            System.out.println(response);
        }
    }
}