package snutella;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
    private InetAddress sourceAddress;
    private int sourcePort;
    private int hops;
    private String filename;

    private static final String MESSAGE_START = "SER";
    private static final int MAX_HOPS = 5;

    public Query(InetAddress sourceAddress,
                 int sourcePort, int hops, String filename) {
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.filename = filename;
        this.hops = hops;
    }

    public Query(InetAddress sourceAddress, int sourcePort, String filename) {
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.filename = filename;
        this.hops = MAX_HOPS;
    }
    public InetAddress getSourceAddress() {
        return this.sourceAddress;
    }

    public int getSourcePort() {
        return this.sourcePort;
    }

    public int getHops() {
        return this.hops;
    }

    public String getFilename() {
        return this.filename;
    }

    public String toString() {
        return String.format("%s %s %d \"%s\" %d",
                MESSAGE_START, this.getSourceAddress().toString().substring(1),
                this.getSourcePort(), this.getFilename(), this.getHops());
    }

    public static Query fromString(String messageString) {
        String regex = MESSAGE_START + " (.*) (.*) \"(.*)\" (.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageString);
        if (!matcher.matches()) {
            return null;
        }
        try {
            InetAddress address = InetAddress.getByName(matcher.group(1));
            int port = Integer.parseInt(matcher.group(2));
            String filename = matcher.group(3);
            int hops = Integer.parseInt(matcher.group(4));

            return new Query(address, port, hops, filename);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;

    }
    public Query reducedHop() {
        return new Query(this.sourceAddress, this.sourcePort,
                this.hops -1, this.filename);
    }
}


