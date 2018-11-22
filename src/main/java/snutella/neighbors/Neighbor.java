package snutella.neighbors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.InetAddress;
import java.util.List;

public class Neighbor {
    private InetAddress address;
    private int port;
    private Date lastPing;
    private boolean isConnected;

    private List<NeighborChangeListener> listeners;

    public Neighbor(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.lastPing = new Date();
        this.isConnected = false;

        this.listeners = new ArrayList<>();
    }

    public void addListener(NeighborChangeListener listener) {
        this.listeners.add(listener);
    }
    public void notifyListeners() {
        for (NeighborChangeListener listener: listeners) {
            listener.onUpdate(this);
        }
    }

    public InetAddress getAddress() {
        return this.address;
    }
    public int getPort() {
        return this.port;
    }

    public void setLastPing(Date date) {
        this.lastPing = date;
        notifyListeners();
    }
    public Date getLastPing() {
        return this.lastPing;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
        notifyListeners();
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    public String getStatusString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return String.format("Node: %s, Last Ping: %s, Connected: %s",
                this.toString(), timeFormat.format(this.getLastPing()),
                this.getIsConnected());
    }
    @Override
    public String toString() {
        return this.address.getHostAddress() + ":" + this.port;
    }
}