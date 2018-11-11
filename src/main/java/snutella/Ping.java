package snutella;

import java.net.InetAddress;

public class Ping {
    private InetAddress sourceAddress;
    private int sourcePort;
    private int ttl;

    public Ping(InetAddress sourceAddress, int sourcePort, int ttl) {
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.ttl = ttl;
    }

    public InetAddress getSourceAddress() {
        return this.sourceAddress;
    }
    public int getSourcePort() {
        return this.sourcePort;
    }

    // Deserializing
    public static Ping fromString(String messageString) {
        return null;
    }

    // Serializing
    @Override
    public String toString() {
        return "PING Source: " + this.sourceAddress.getHostName() + ":" + 
            this.sourcePort + " TTL: " + this.ttl; 
    }

    public Ping reducedTtl() {
        return new Ping(this.sourceAddress, this.sourcePort, this.ttl - 1);
    }
}