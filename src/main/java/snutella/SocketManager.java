package snutella;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketManager {
    private DatagramSocket socket;

    public SocketManager(DatagramSocket socket) {
        this.socket = socket;
    }
    public DatagramSocket getSocket() {
        return this.socket;
    }
    public InetAddress getAddress() {
        return this.socket.getLocalAddress();
    }
    public int getPort() {
        return this.socket.getLocalPort();
    }

    public  void sendMessage(String message, InetAddress destinationAddress,
                             int destinationPort) {
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
                destinationAddress, destinationPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public DatagramPacket receiveMessage() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            this.socket.receive(packet);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
