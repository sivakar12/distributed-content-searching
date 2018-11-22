package snutella;

import java.net.InetAddress;
import java.util.List;

public class QueryRespone {
    private static final int FILE_SERVER_PORT = 8888;
    private InetAddress address;
    private int port;
    private int hops;
    private List<String> files;

    public QueryRespone(InetAddress address, int port, int hops,
                        List<String> files) {
        this.address = address;
        this.port = port;
        this.hops = hops;
        this.files = files;
    }

    public String toString() {
        String files = String.join(" ", this.files);
        return String.format("SEROK %d %s %d %d %s", this.files.size(),
                this.address.toString().substring(1), this.port, this.hops, files);
    }
}
