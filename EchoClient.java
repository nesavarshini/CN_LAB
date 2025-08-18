import java.io.*;
import java.net.*;
import java.util.*;

public class EchoClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6666);
            System.out.println("Connected to server.");

            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            Scanner input = new Scanner(System.in);

            String senddata = "", receivedata = "";

            while (true) {
                System.out.print("TO SERVER: ");
                senddata = input.nextLine();
                dout.writeUTF(senddata);

                if (senddata.equalsIgnoreCase("stop")) {
                    System.out.println("Client terminated the chat.");
                    break;
                }

                receivedata = din.readUTF();
                if (receivedata.equalsIgnoreCase("stop")) {
                    System.out.println("Server terminated the chat.");
                    break;
                }
                System.out.println("SERVER SAYS: " + receivedata);
            }

            din.close();
            dout.close();
            socket.close();
            input.close();

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
