package snutella.logging;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage {
    private boolean incoming;
    private LogMessageType messageType;

    private InetAddress sourceAddress;
    private int sourcePort;
    private InetAddress destinationAddress;
    private int destinationPort;

    private Date time;
    private String message;

    public LogMessage(boolean incoming, LogMessageType messageType,
                      InetAddress sourceAddress, int sourcePort,
                      InetAddress destinationAddress, int destinationPort,
                      Date time, String message) {
        this.incoming = incoming;
        this.messageType = messageType;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        this.time = time;
        this.message = message;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public LogMessageType getMessageType() {
        return messageType;
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public Date getTime() {
        return time;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        String timeString = (new SimpleDateFormat("HH:mm:ss")).format(this.time);
        String type = this.incoming ? "Incoming" : "Outgoing";
        String address = this.incoming ? this.sourceAddress.getHostAddress() : this.destinationAddress.getHostAddress();
        String port = this.incoming ? String.valueOf(this.sourcePort) : String.valueOf(this.destinationPort);
        String message = this.message;
        return String.format("%s %s %s:%s %s", timeString, type, address, port, message);
    }
}
