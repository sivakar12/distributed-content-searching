package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;
import snutella.messages.*;
import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListManager;
import snutella.queryresults.QueryResultsManager;
import snutella.stats.HopCountLogger;
import snutella.stats.ResponseTimeLogger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MessageHandler extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    
    private NeighborListManager neighborManager;
    private SocketManager socketManager;
    private LogsManager logsManager;
    private FileManager fileManager;

    public MessageHandler(SocketManager socketManager, NeighborListManager
            neighborManager, FileManager fileManager) {
        this.socketManager = socketManager;
        this.buffer = new byte[BUFFER_SIZE];
        this.neighborManager = neighborManager;
        this.logsManager = LogsManager.getInstance();
        this.fileManager = fileManager;
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
            this.neighborManager.notifyListeners();
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
                   .filter(n -> !(n.getAddress().equals(ping.getSourceAddress())
                           && n.getPort() == ping.getSourcePort()))
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

    public void handleQuery(DatagramPacket packet) {
        String message = new String(packet.getData());
        message = message.trim();
        Query query = Query.fromString(message);

        LogMessage log = new LogMessage(true, LogMessageType.QUERY,
                packet.getAddress(), packet.getPort(),
                this.socketManager.getAddress(), this.socketManager.getPort(),
                new Date(), message);
        this.logsManager.log(log);

        Query forwardingQuery = query.reducedHop();
        if (forwardingQuery.getHops() > 0) {
            this.neighborManager.getNeighbors().stream()
                    .filter(n -> n.getIsConnected())
                    .filter(n -> !(n.getAddress().equals(packet.getAddress())
                            && n.getPort() == packet.getPort()))
                    .forEach(n -> {
                        LogMessage logMessage = new LogMessage(false, LogMessageType.QUERY,
                                this.socketManager.getAddress(), this.socketManager.getPort(),
                                n.getAddress(), n.getPort(), new Date(), forwardingQuery.toString());
                        this.logsManager.log(logMessage);
                        this.socketManager.sendMessage(forwardingQuery.toString(),
                                n.getAddress(), n.getPort());
                    });
        }

        List<String> results = this.fileManager.search(query.getFilename());
        if (results.size() > 0) {
            HopCountLogger.getInstance().logHops(query.getSourceAddress(),
                    query.getSourcePort(), socketManager.getAddress(),
                    socketManager.getPort(), query.getFilename(), query.getHops());
            QueryResponse response = new QueryResponse(this.socketManager.getAddress(),
                    FileServer.getInstance().getServingPort(), query.getHops(), results);

            LogMessage queryResponseLog = new LogMessage(false, LogMessageType.QUERY_RESPOSNE,
                    this.socketManager.getAddress(), this.socketManager.getPort(),
                    query.getSourceAddress(), query.getSourcePort(), new Date(),
                    response.toString());
            this.logsManager.log(queryResponseLog);

            this.socketManager.sendMessage(response.toString(), query.getSourceAddress(),
                    query.getSourcePort());
        }
    }

    public void handleQueryResponse(DatagramPacket packet) {
        String responseMessage = new String(packet.getData());
        responseMessage = responseMessage.trim();

        LogMessage log = new LogMessage(true, LogMessageType.QUERY_RESPOSNE,
                packet.getAddress(), packet.getPort(), socketManager.getAddress(),
                socketManager.getPort(), new Date(), responseMessage);
        logsManager.log(log);

        QueryResponse queryResponse = QueryResponse.fromString(responseMessage);
        QueryResultsManager.getInstance().addItems(queryResponse.getItems());
        System.out.println("Response for search: " + responseMessage);
    }

    public void handleJoin(DatagramPacket packet) {
        String messageString = new String(packet.getData());
        messageString = messageString.trim();

        LogMessage log = new LogMessage(true, LogMessageType.JOIN,
                packet.getAddress(), packet.getPort(),
                socketManager.getAddress(), socketManager.getPort(),
                new Date(), messageString);
        this.logsManager.log(log);

        JoinMessage joinMessage = JoinMessage.fromString(messageString);
        Neighbor neighbor = new Neighbor(joinMessage.getAddress(), joinMessage.getPort());
        neighbor.setIsConnected(true);
        this.neighborManager.disconnectOne();
        this.neighborManager.getNeighbors().add(neighbor);
        this.neighborManager.notifyListeners();
    }

    public void handleLeave(DatagramPacket packet) {
        String messageString = new String(packet.getData());
        messageString = messageString.trim();


        LogMessage log = new LogMessage(true, LogMessageType.LEAVE,
                packet.getAddress(), packet.getPort(), socketManager.getAddress(),
                socketManager.getPort(), new Date(), messageString);
        logsManager.log(log);

        LeaveMessage leaveMessage = LeaveMessage.fromString(messageString);
        Optional<Neighbor> match = this.neighborManager.getNeighbors().stream()
                .filter(n -> n.getAddress().equals(leaveMessage.getAddress())
                    && n.getPort() == leaveMessage.getPort())
                .findAny();
        if (match.isPresent()) {
            this.neighborManager.getNeighbors().remove(match.get());
            this.neighborManager.notifyListeners();
        }
    }

    public void listenForMessages() throws Exception {
        while (true) {
            DatagramPacket packet = this.socketManager.receiveMessage();
            String message = new String(packet.getData());
            if (message.startsWith("PING ")) {           // TODO: Remove hardcoded string
                handlePing(packet);
            } else if (message.startsWith("SER ")) {
                handleQuery(packet);
            } else if (message.startsWith("SEROK ")) {
                handleQueryResponse(packet);
            } else if (message.startsWith("JOIN ")) {
                handleJoin(packet);
            } else if (message.startsWith("LEAVE ")) {
                handleLeave(packet);
            }
        }
    }
}