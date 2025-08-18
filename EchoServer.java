import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            Scanner input = new Scanner(System.in);

            String senddata = "", receivedata = "";

            while (true) {
                receivedata = din.readUTF();
                if (receivedata.equalsIgnoreCase("stop")) {
                    System.out.println("Client terminated the chat.");
                    break;
                }
                System.out.println("CLIENT SAYS: " + receivedata);
                System.out.print("TO CLIENT: ");
                senddata = input.nextLine();
                dout.writeUTF(senddata);
                if (senddata.equalsIgnoreCase("stop")) {
                    System.out.println("Server terminated the chat.");
                    break;
                }
            }

            din.close();
            dout.close();
            socket.close();
            serverSocket.close();
            input.close();

        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}
