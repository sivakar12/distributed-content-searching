package snutella.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class JoinMessage {
    public static final String MESSAGE_START = "JOIN";
    private InetAddress address;
    private int port;

    public JoinMessage(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static JoinMessage fromString(String message) {
        System.out.println(message);
        String[] parts = message.split(" ");
        System.out.println(parts[1]);
        try {
            InetAddress address = InetAddress.getByName(parts[1]);
            int port = Integer.parseInt(parts[2]);
            return new JoinMessage(address, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        System.out.println(address.toString());
        return MESSAGE_START + " " + address.toString().substring(1) +
                " " + port;
    }
}
