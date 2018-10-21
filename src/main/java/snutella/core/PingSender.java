package snutella.core;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import snutella.core.Neighbor;
import snutella.core.Message;

public class PingSender extends Thread {
    private static final int DELAY = 5000;
    private static final int PERIOD = 5000;

    private DatagramSocket socket;
    private List<Neighbor> connectedPeers;

    public PingSender(DatagramSocket socket, List<Neighbor> connectedPeers) {
        this.socket = socket;
        this.connectedPeers = connectedPeers;
    }

    public void changeConnectedPeers(List<Neighbor> connectedPeers) {
        this.connectedPeers = connectedPeers;
    }

    private void sendPingToAll() {
        for (Neighbor peer: connectedPeers) {
            this.sendPing(peer);
        }
    }
    private void sendPing(Neighbor neighbor) {
        try {
            String message = Message.ping();
            byte[] messageBytes = message.getBytes();
            InetAddress address = InetAddress.getByName(neighbor.getAddress());
            int port = neighbor.getPort();
            DatagramPacket packet = new DatagramPacket(
                messageBytes, messageBytes.length, address, port);
            socket.send(packet);
            System.out.println("Sent ping to " + port);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void run() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPingToAll();
            }
        }, DELAY, PERIOD);
    }
}