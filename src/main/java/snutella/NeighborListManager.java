package snutella;

import java.util.ArrayList;
import java.util.List;

public class NeighborListManager {
    private List<Neighbor> neighbors;

    public NeighborListManager() {
        this.neighbors = new ArrayList<Neighbor>();
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
}
