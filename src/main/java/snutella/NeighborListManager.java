package snutella;

import java.util.ArrayList;
import java.util.List;

public class NeighborListManager {
    private final static int MAX_CONNECTIONS = 2;

    private List<Neighbor> neighbors;

    public NeighborListManager() {
        this.neighbors = new ArrayList<Neighbor>();
        Runnable refreshConnection = () -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.refreshConnections();
            }
        };
        (new Thread(refreshConnection)).start();
    }

    public synchronized void addNeighbor(Neighbor neighbor) {
        this.neighbors.add(neighbor);
    }

    public void addNeighbors(List<Neighbor> neighbors) {
        for (Neighbor n: neighbors)
            this.addNeighbor(n);
    }

    public List<Neighbor> getNeighbors() {
        return this.neighbors;
    }

    public long getNumberOfConnections() {
        return this.neighbors.stream().filter(
                n -> n.getIsConnected()).count();
    }
    public String getNeighborDetails() {
        return this.neighbors.stream()
                .map(n -> n.getStatusString())
                .reduce("", (s1, s2) -> s1 + s2 + "\n");
    }

    public void refreshConnections() {
        long numberOfConnections = getNumberOfConnections();
        if (numberOfConnections > MAX_CONNECTIONS) {
            long numberToDisconnect = numberOfConnections - MAX_CONNECTIONS;
            this.neighbors.stream()
                    .filter(n -> n.getIsConnected())
                    .limit(numberToDisconnect)
                    .forEach(neighbor -> neighbor.setIsConnected(false));
        } else if (numberOfConnections < MAX_CONNECTIONS) {
            long numberToConnect = MAX_CONNECTIONS - numberOfConnections;
            this.neighbors.stream()
                    .filter(n -> !n.getIsConnected())
                    .limit(numberToConnect)
                    .forEach(neighbor -> neighbor.setIsConnected(true));
        }
    }
}
