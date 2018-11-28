package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;
import snutella.messages.Ping;
import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListManager;

import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingSender extends Thread {
    private static final int DELAY = 0;
    private static final int PERIOD = 10000;
    private static final int DEFAULT_TTL = 3;

    private SocketManager socketManager;
    private NeighborListManager neighborListManager;
    private LogsManager logsManager;

    public PingSender(SocketManager socketManager, NeighborListManager neighborListManager) {
        this.socketManager = socketManager;
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

            Ping ping = new Ping(this.socketManager.getAddress(),
                this.socketManager.getPort(), DEFAULT_TTL);

            LogMessage logMessage = new LogMessage(false, LogMessageType.PING,
                    this.socketManager.getAddress(), this.socketManager.getPort(),
                    neighbor.getAddress(), neighbor.getPort(), new Date(), ping.toString());
            this.logsManager.log(logMessage);

            this.socketManager.sendMessage(ping.toString(),
                    neighbor.getAddress(), neighbor.getPort());
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