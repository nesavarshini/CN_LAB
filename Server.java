import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(1309);
            System.out.println("Server is running...");

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receiver = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                server.receive(receiver);

                String received = new String(receiver.getData(), 0, receiver.getLength()).trim();
                System.out.println("Received: " + received);

                InetAddress clientAddress = receiver.getAddress();
                int clientPort = receiver.getPort();

                String[] ipList = {"165.165.80.80", "165.165.79.1"};
                String[] nameList = {"www.aptitudeguru.com", "www.downloadcyclone.blogspot.com"};

                boolean found = false;

                for (int i = 0; i < ipList.length; i++) {
                    String response = "";

                    if (received.equals(ipList[i])) {
                        response = nameList[i];
                        found = true;
                    } else if (received.equals(nameList[i])) {
                        response = ipList[i];
                        found = true;
                    }

                    if (found) {
                        byte[] sendBuffer = response.getBytes();
                        DatagramPacket sender = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                        server.send(sender);
                        break;
                    }
                }

                if (!found) {
                    String response = "Not Found";
                    byte[] sendBuffer = response.getBytes();
                    DatagramPacket sender = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                    server.send(sender);
                }
            }

        } catch (Exception e) {
            System.out.println("Server error: " + e);
        }
    }
}
