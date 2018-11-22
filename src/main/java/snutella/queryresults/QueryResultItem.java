package snutella.queryresults;

import java.net.InetAddress;

public class QueryResultItem {
    private String filename;
    private InetAddress address;
    private int port;

    public QueryResultItem(InetAddress address, int port, String filename) {
        this.address = address;
        this.port = port;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "\"" + filename + "\" : " + address.toString().substring(1) + ":" + port;
    }
}
