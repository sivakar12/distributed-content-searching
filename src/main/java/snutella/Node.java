package snutella;

import snutella.logging.LogMessage;
import snutella.logging.LogMessageType;
import snutella.logging.LogsManager;
import snutella.messages.JoinMessage;
import snutella.messages.LeaveMessage;
import snutella.messages.Query;
import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListManager;
import snutella.queryresults.QueryResultItem;
import snutella.queryresults.QueryResultsManager;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    private SocketManager socketManager;

    private String username;

    private NeighborListManager neighborManager;
    private FileManager fileManager;
    private BSSClient bssClient;
    private MessageHandler messageHandler;
    private PingSender pingSender;
    private FileServer fileServer;
    private QueryResultsManager queryResultsManager;

    public Node(String address, int port, String bssAddress, int bssPort) {
        this.username = "team19";
        this.neighborManager = new NeighborListManager();
        this.fileManager = new FileManager();
        this.fileServer = FileServer.getInstance();
        this.queryResultsManager = QueryResultsManager.getInstance();

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        try {
            DatagramSocket socket = new DatagramSocket(port, inetAddress);
            this.socketManager = new SocketManager(socket);
        } catch (Exception e) {
            System.err.println(e);
            return;
        }

        try {
            this.bssClient = new BSSClient(bssAddress, bssPort);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void start() {
        try {
            this.registerToBSServer();
        } catch (Exception e) {
            System.err.println("Error registering with Bootstrap server");
            e.printStackTrace();
            return;

        }
        this.join();
        this.fileServer.start();
        this.listenForMessages();
        this.sendPings();
    }

    public InetAddress getAddress() {
        return this.socketManager.getAddress();
    }
    public int getPort() {
        return this.socketManager.getPort();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public NeighborListManager getNeighborManager() {
        return neighborManager;
    }

    public void registerToBSServer() throws Exception {

        List<Neighbor> neighbors = this.bssClient.register(
                this.getAddress(), this.getPort(), this.username);
        neighbors.forEach(n -> n.setIsConnected(true));
        neighborManager.addNeighbors(neighbors);
        System.out.println("Neighbors: " + neighbors);
    }

    public void unregisterFromBSServer() throws Exception {
        this.bssClient.unregister(this.getAddress(), this.getPort(), this.username);
    }

    public void join() {
        JoinMessage joinMessage = new JoinMessage(this.getAddress(), this.getPort());
        this.neighborManager.getNeighbors().stream()
                .forEach(n -> {
                    LogMessage log = new LogMessage(false, LogMessageType.JOIN,
                            this.getAddress(), this.getPort(), n.getAddress(), n.getPort(),
                            new Date(), joinMessage.toString());
                    LogsManager.getInstance().log(log);
                    socketManager.sendMessage(joinMessage.toString(), n.getAddress(), n.getPort());
                });
        System.out.println("Sent join message");
    }

    public void leave() {
        LeaveMessage leaveMessage = new LeaveMessage(this.getAddress(), this.getPort());
        this.neighborManager.getNeighbors().stream()
//                .filter(n -> n.getIsConnected())
                .forEach(n -> {
                    LogMessage log = new LogMessage(false, LogMessageType.LEAVE,
                            this.getAddress(), this.getPort(), n.getAddress(), n.getPort(),
                            new Date(), leaveMessage.toString());
                    LogsManager.getInstance().log(log);
                    socketManager.sendMessage(leaveMessage.toString(), n.getAddress(), n.getPort());
                });
    }
    public void listenForMessages() {
        try {
            this.messageHandler = new MessageHandler(this.socketManager,
                    this.neighborManager, this.fileManager);
            this.messageHandler.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public void sendPings() {
        try {
            this.pingSender = new PingSender(this.socketManager, this.neighborManager);
            this.pingSender.start();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public void sendQuery(String queryString) {
        queryResultsManager.reset();
        Query query = new Query(this.getAddress(), this.getPort(), queryString);
        LogsManager logsManager = LogsManager.getInstance();
        this.neighborManager.getNeighbors().stream()
            .filter(n -> n.getIsConnected())
            .forEach(n -> {
                LogMessage log = new LogMessage(false, LogMessageType.QUERY,
                        this.getAddress(), this.getPort(), n.getAddress(),
                        n.getPort(), new Date(), query.toString());
                logsManager.log(log);
                this.socketManager.sendMessage(query.toString(), n.getAddress(),
                        n.getPort());

            });
    }

    public void download(QueryResultItem queryResult) {
        FileDownloader downloader = new FileDownloader(
                queryResult.getAddress(), queryResult.getPort(),
                queryResult.getFilename());
        downloader.start();
    }
    public void stop() {
        try {
            this.unregisterFromBSServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.leave();
        System.out.println("Sent leave message");
        this.messageHandler.interrupt();
        this.pingSender.interrupt();
    }
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Input port, and bootstrap sever config correctly");
            return;
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        String bssAddress = args[2];
        int bssPort = Integer.parseInt((args[3]));

        new Node(address, port, bssAddress, bssPort);

    }
}