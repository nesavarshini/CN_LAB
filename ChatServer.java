import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("Server is waiting for client connection...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            Scanner input = new Scanner(System.in);

            String sendData = "";
            String receiveData = "";

            while (!receiveData.equalsIgnoreCase("stop")) {
                receiveData = din.readUTF();
                System.out.println("CLIENT SAYS: " + receiveData);
                System.out.print("TO CLIENT: ");
                sendData = input.nextLine();
                dout.writeUTF(sendData);
                dout.flush();
            }

            input.close();
            din.close();
            dout.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
