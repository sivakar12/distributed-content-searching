package snutella.core;

public class Neighbor {
    private String address;
    private int port;
    
    public Neighbor(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return this.address;
    }
    public int getPort() {
        return this.port;
    }
    
    @Override
    public String toString() {
        return this.address + ":" + this.port;
    }
}