package snutella.core;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import snutella.core.Neighbor;
import snutella.core.Ping;

public class PingSender extends Thread {
    private static final int DELAY = 5000;
    private static final int PERIOD = 5000;
    private static final int DEFAULT_TTL = 5;

    private DatagramSocket socket;
    private List<Neighbor> peers;

    public PingSender(DatagramSocket socket, List<Neighbor> peers) {
        this.socket = socket;
        this.peers = peers;
    }

    public void changePeers(List<Neighbor> peers) {
        this.peers = peers;
    }

    private void sendPingToConnectedPeers() {
        for (Neighbor peer: peers) {
            if (peer.getIsConnected())
                this.sendPing(peer);
        }
    }
    private void sendPing(Neighbor neighbor) {
        try {
            System.out.println("Addr: " + this.socket.getLocalAddress());
            System.out.println("POrt: " + this.socket.getLocalPort());
            Ping ping = new Ping(this.socket.getLocalAddress(), 
                this.socket.getLocalPort(), DEFAULT_TTL);
            byte[] messageBytes = ping.toString().getBytes();
            InetAddress address = neighbor.getAddress();
            int port = neighbor.getPort();
            DatagramPacket packet = new DatagramPacket(
                messageBytes, messageBytes.length, address, port);
            socket.send(packet);
            System.out.println("Sent ping to " + port);
        } catch (Exception e) {
            System.out.println("error here");
            e.printStackTrace();
        }
    }

    public void run() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPingToConnectedPeers();
            }
        }, DELAY, PERIOD);
    }
}