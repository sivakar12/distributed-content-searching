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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryResultItem that = (QueryResultItem) o;

        if (port != that.port) return false;
        if (!filename.equals(that.filename)) return false;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + port;
        return result;
    }
}
