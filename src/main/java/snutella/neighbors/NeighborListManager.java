package snutella.neighbors;

import snutella.Configuration;
import snutella.neighbors.Neighbor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NeighborListManager implements NeighborChangeListener {

    private List<Neighbor> neighbors;
    private List<NeighborListListener> listeners;

    public NeighborListManager() {
        this.neighbors = new ArrayList<>();
        this.listeners = new ArrayList<>();

        Runnable refreshConnection = () -> {
            while (true) {
                this.refreshConnections();
                try {
                    Thread.sleep(Configuration.NEIGHBOR_LIST_REFRESH_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        (new Thread(refreshConnection)).start();
    }

    public synchronized void addNeighbor(Neighbor neighbor) {
        neighbor.addListener(this);
        this.neighbors.add(neighbor);
        this.notifyListeners();
    }

    public void addNeighbors(List<Neighbor> neighbors) {
        for (Neighbor n: neighbors)
            this.addNeighbor(n);
    }
    public void addListener(NeighborListListener listener) {
        this.listeners.add(listener);
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
        if (numberOfConnections > Configuration.MAX_CONNECTIONS) {
            long numberToDisconnect = numberOfConnections - Configuration.MAX_CONNECTIONS;
            this.neighbors.stream()
                    .filter(n -> n.getIsConnected())
                    .limit(numberToDisconnect)
                    .forEach(neighbor -> neighbor.setIsConnected(false));
        } else if (numberOfConnections < Configuration.MAX_CONNECTIONS) {
            long numberToConnect = Configuration.MAX_CONNECTIONS - numberOfConnections;
            this.neighbors.stream()
                    .filter(n -> !n.getIsConnected())
                    .limit(numberToConnect)
                    .forEach(neighbor -> neighbor.setIsConnected(true));
        }
        Date currentTime = new Date();
        List<Neighbor> inactiveNeighbors = this.neighbors.stream().filter(n -> {
            long difference = currentTime.getTime() - n.getLastPing().getTime();
            return difference > Configuration.MAX_INTERVAL_BETWEEN_PINGS;
        }).collect(Collectors.toList());
        for (Neighbor n: inactiveNeighbors) {
            this.neighbors.remove(n);
        }
        this.notifyListeners();
    }
    public void disconnectOne() {
        if (this.neighbors.size() < Configuration.MAX_CONNECTIONS) {
            return;
        }
        Optional<Neighbor> match = this.neighbors.stream()
                .filter(n -> n.getIsConnected())
                .findAny();
        if (match.isPresent()) {
            match.get().setIsConnected(false);
        }
    }
    public void notifyListeners() {
        for(NeighborListListener listener: this.listeners) {
            listener.onUpdate(this.neighbors);
        }
    }

    @Override
    public void onUpdate(Neighbor neighbor) {
        this.notifyListeners();
    }
}
