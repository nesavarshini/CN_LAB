import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            DatagramSocket client = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the IP address or Domain name: ");
            String userInput = input.readLine();

            byte[] sendBuffer = userInput.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, 1309);
            client.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            client.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Response from server: " + response);

            client.close();
        } catch (Exception e) {
            System.out.println("Client error: " + e);
        }
    }
}
