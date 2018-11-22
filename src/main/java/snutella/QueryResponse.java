package snutella;

import snutella.queryresults.QueryResultItem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryResponse {
    private static final int FILE_SERVER_PORT = 8888;
    private InetAddress address;
    private int port;
    private int hops;
    private List<String> files;

    public QueryResponse(InetAddress address, int port, int hops,
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

    public static QueryResponse fromString(String message) {
        String[] messageComponents = message.split(" ");
        InetAddress address = null;
        try {
            address = InetAddress.getByName(messageComponents[2]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = Integer.parseInt(messageComponents[3]);
        int hops = Integer.parseInt(messageComponents[4]);
        String[] files = Arrays.copyOfRange(messageComponents, 5,
                messageComponents.length);
        return new QueryResponse(address, port, hops, Arrays.asList(files));
    }

    public List<QueryResultItem> getItems() {
        return this.files.stream()
                .map(filename -> new QueryResultItem(
                        this.address, this.port, filename))
                .collect(Collectors.toList());
    }
}
