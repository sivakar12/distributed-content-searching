package snutella;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    
    private NeighborListManager neighborManager;
    private DatagramSocket socket;

    public MessageHandler(DatagramSocket socket, NeighborListManager neighborManager) {
        this.socket = socket;
        this.buffer = new byte[BUFFER_SIZE];
        this.neighborManager = neighborManager;
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

        String pingMessage = new String(packet.getData());
        pingMessage = pingMessage.replaceAll("\\x00", "");
        Ping ping = Ping.fromString(pingMessage);

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
    }

    public void handleNeighborsQuery(DatagramPacket packet) {
        InetAddress sourceAddress = packet.getAddress();
        int sourcePort = packet.getPort();
        System.out.println("Neighbors query received from " +
                sourceAddress.getHostName() + ":" + sourcePort);
        String response = neighborManager.getNeighborDetails();
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(),
                response.getBytes().length, sourceAddress, sourcePort);
        try {
            this.socket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void listenForMessages() throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            socket.receive(packet);
            String message = new String(packet.getData());
            if (message.startsWith("PING")) {           // TODO: Remove hardcoded string
                handlePing(packet);
            } else if (message.startsWith("NEIGHBORS")) {
                handleNeighborsQuery(packet);
            }
        }
    }
}