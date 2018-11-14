package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingSender extends Thread {
    private static final int DELAY = 5000;
    private static final int PERIOD = 5000;
    private static final int DEFAULT_TTL = 5;

    private DatagramSocket socket;
    private NeighborListManager neighborListManager;
    private LogsManager logsManager;

    public PingSender(DatagramSocket socket, NeighborListManager neighborListManager) {
        this.socket = socket;
        this.neighborListManager = neighborListManager;
        this.logsManager = LogsManager.getInstance();
    }

    private void sendPingToConnectedPeers() {
        for (Neighbor peer: this.neighborListManager.getNeighbors()) {
            if (peer.getIsConnected())
                this.sendPing(peer);
        }
    }
    private void sendPing(Neighbor neighbor) {
        try {
            InetAddress address = neighbor.getAddress();
            int port = neighbor.getPort();

            Ping ping = new Ping(this.socket.getLocalAddress(),
                this.socket.getLocalPort(), DEFAULT_TTL);

            LogMessage logMessage = new LogMessage(false, LogMessageType.PING,
                    this.socket.getLocalAddress(), this.socket.getLocalPort(),
                    neighbor.getAddress(), neighbor.getPort(), new Date(), ping.toString());
            this.logsManager.log(logMessage);

            byte[] messageBytes = ping.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(
                messageBytes, messageBytes.length, address, port);

            socket.send(packet);
            System.out.println("Sent ping to " + address.toString() + ":" + port);
        } catch (Exception e) {
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