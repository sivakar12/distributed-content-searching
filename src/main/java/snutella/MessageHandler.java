package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Optional;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    
    private NeighborListManager neighborManager;
    private SocketManager socketManager;
    private LogsManager logsManager;

    public MessageHandler(SocketManager socketManager, NeighborListManager neighborManager) {
        this.socketManager = socketManager;
        this.buffer = new byte[BUFFER_SIZE];
        this.neighborManager = neighborManager;
        this.logsManager = LogsManager.getInstance();
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

        String pingMessage = new String(packet.getData());
        pingMessage = pingMessage.replaceAll("\\x00", "");
        Ping ping = Ping.fromString(pingMessage);

        LogMessage logMessage = new LogMessage(true, LogMessageType.PING,
                packet.getAddress(), packet.getPort(),
                this.socketManager.getSocket().getLocalAddress(),
                this.socketManager.getSocket().getPort(), new Date(), pingMessage);
        this.logsManager.log(logMessage);

        System.out.println("Ping received from " + address.getHostName() + ":" + port);
        Optional<Neighbor> match = this.neighborManager.getNeighbors().stream()
                .filter(n ->
                        n.getAddress().equals(ping.getSourceAddress()) &&
                        n.getPort() == ping.getSourcePort()
                )
                .findAny();
        match.ifPresent(neighbor -> {
            neighbor.setLastPing(new Date());
        });
        if (!match.isPresent()) {
            Neighbor newNeighbor = new Neighbor(
                    ping.getSourceAddress(), ping.getSourcePort());
            this.neighborManager.addNeighbor(newNeighbor);
        }

        Ping forwardingPing = ping.reducedTtl();
        if (forwardingPing.getTtl() != 0) {
           this.neighborManager.getNeighbors().stream()
                   .filter(n -> n.getIsConnected())
                   .filter(n -> n.getAddress().equals(ping.getSourceAddress())
                           && n.getPort() != ping.getSourcePort())
                   .forEach(n -> {
                        socketManager.sendMessage(forwardingPing.toString(),
                                n.getAddress(), n.getPort());
                        LogMessage logItem = new LogMessage(false, LogMessageType.PING,
                                this.socketManager.getAddress(), this.socketManager.getPort(),
                                n.getAddress(), n.getPort(), new Date(),
                                forwardingPing.toString());
                        this.logsManager.log(logItem);
                   });
        }
    }

    public void handleNeighborsQuery(DatagramPacket packet) {
        InetAddress sourceAddress = packet.getAddress();
        int sourcePort = packet.getPort();
        System.out.println("Neighbors query received from " +
                sourceAddress.getHostName() + ":" + sourcePort);
        String response = neighborManager.getNeighborDetails();
        socketManager.sendMessage(response, sourceAddress, sourcePort);

    }
    public void listenForMessages() throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            String message = this.socketManager.receiveMessage();
            if (message.startsWith("PING")) {           // TODO: Remove hardcoded string
                handlePing(packet);
            } else if (message.startsWith("NEIGHBORS")) {
                handleNeighborsQuery(packet);
            }
        }
    }
}