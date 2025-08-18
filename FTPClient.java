import java.io.*; 
import java.net.*; 
public class FTPClient { 
    public static void main(String[] args) { 
        try { 
            InetAddress ia = InetAddress.getLocalHost(); 
            Socket socket = new Socket(ia, 1024); 
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); 
            System.out.print("Enter a file name to save received data: "); 
            String saveAsFileName = consoleReader.readLine(); 
            PrintWriter fileWriter = new PrintWriter(new FileWriter(saveAsFileName)); 
            BufferedReader socketReader = new BufferedReader(new 
InputStreamReader(socket.getInputStream())); 
            String line; 
            while ((line = socketReader.readLine()) != null) { 
                fileWriter.println(line); 
            } 
            fileWriter.close(); 
            socketReader.close(); 
            socket.close(); 
            System.out.println("File received and saved as: " + saveAsFileName); 
        } catch (IOException e) { 
            System.out.println("Client Error: " + e.getMessage()); 
        } 
    }
}