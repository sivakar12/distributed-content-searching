package snutella;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CLI {
    private static String sendAndGetResponse(String message, InetAddress address, int port) {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message.getBytes(),
                    message.getBytes().length, address, port);
            socket.send(packet);
            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(10000);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData());
            response = response.trim();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public static void main(String[] args) {
        switch (args[0]) {
            case "nl":
                try {
                    InetAddress address = InetAddress.getByName(args[1]);
                    int port = Integer.parseInt(args[2]);
                    String queryResult = sendAndGetResponse("NEIGHBORS", address, port);
                    System.out.println(queryResult);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

        }
    }
}
