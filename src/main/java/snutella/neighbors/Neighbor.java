package snutella.neighbors;

import java.util.Date;
import java.net.InetAddress;

public class Neighbor {
    private InetAddress address;
    private int port;
    private Date lastPing;
    private boolean isConnected;

    public Neighbor(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.lastPing = new Date();
        this.isConnected = true;
    }

    public InetAddress getAddress() {
        return this.address;
    }
    public int getPort() {
        return this.port;
    }

    public void setLastPing(Date date) {
        this.lastPing = date;
    }
    public Date getLastPing() {
        return this.lastPing;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    public String getStatusString() {
        return String.format("Node: %s, Last Ping: %s, Connected: %s",
                this.toString(), this.getLastPing().toString(), this.getIsConnected());
    }
    @Override
    public String toString() {
        return this.address.getHostAddress() + ":" + this.port;
    }
}