package snutella.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public int getTtl() {
        return this.ttl;
    }
    // Deserializing
    public static Ping fromString(String messageString) {
        String regex = "PING Source: (.*):(.*) TTL: (.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageString);
        if (!matcher.matches()) {
            return null;
        }
        try {
            InetAddress address = InetAddress.getByName(matcher.group(1));
            int port = Integer.parseInt(matcher.group(2));
            int ttl = Integer.parseInt(matcher.group(3));
            return new Ping(address, port, ttl);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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