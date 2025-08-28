import java.io.*; 
import java.net.*; 
import java.util.*; 
public class ChatClient { 
    public static void main(String[] args) { 
        try { 
            Socket socket = new Socket("localhost", 6666); 
            DataInputStream din = new DataInputStream(socket.getInputStream()); 
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream()); 
            Scanner input = new Scanner(System.in); 
            String sendData = ""; 
            String receiveData = ""; 
            while (!sendData.equalsIgnoreCase("stop")) { 
                System.out.print("TO SERVER: "); 
                sendData = input.nextLine(); 
                dout.writeUTF(sendData);   
                dout.flush();              
                receiveData = din.readUTF();  
                System.out.println("SERVER SAYS: " + receiveData); 
            } 
            input.close(); 
            din.close(); 
            dout.close(); 
            socket.close(); 
        } catch (IOException e) { 
            System.out.println("Error: " + e.getMessage()); 
        } 
    } 
} 
