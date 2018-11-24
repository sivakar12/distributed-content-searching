package snutella.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LeaveMessage {
    public static final String MESSAGE_START = "LEAVE";
    private InetAddress address;
    private int port;

    public LeaveMessage(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static LeaveMessage fromString(String message) {
        String[] parts = message.split(" ");
        try {
            InetAddress address = InetAddress.getByName(parts[1]);
            int port = Integer.parseInt(parts[2]);
            return new LeaveMessage(address, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return MESSAGE_START + " " + address.toString().substring(1) +
                " " + port;
    }
}
