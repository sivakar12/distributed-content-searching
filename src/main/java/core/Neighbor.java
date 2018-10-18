public class Neighbor {
    public String address;
    public int port;
    
    public Neighbor(String address, int port) {
        this.address = address;
        this.port = port;
    }
    @Override
    public String toString() {
        return this.address + ":" + this.port;
    }
}