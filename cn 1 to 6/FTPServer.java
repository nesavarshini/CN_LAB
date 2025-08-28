import java.io.*; 
import java.net.*; 
public class FTPServer { 
    public static void main(String[] args) { 
        try { 
            ServerSocket ss = new ServerSocket(1024); 
            System.out.println("Server is running and waiting for connection..."); 
            Socket socket = ss.accept(); 
            System.out.println("Client connected."); 
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); 
            PrintStream out = new PrintStream(socket.getOutputStream()); 
            System.out.print("Enter a file name to send: "); 
            String fileName = consoleReader.readLine(); 
            File file = new File(fileName); 
            if (file.exists()) { 
                BufferedReader fileReader = new BufferedReader(new FileReader(file)); 
                String line; 
                while ((line = fileReader.readLine()) != null) { 
                    out.println(line); 
                } 
                fileReader.close(); 
                System.out.println("File sent successfully."); 
            } else { 
                out.println("ERROR: File not found."); 
                System.out.println("File not found."); 
            } 
            out.close(); 
            socket.close(); 
            ss.close(); 
        } catch (IOException e) { 
            System.out.println("Server Error: " + e.getMessage()); 
        } 
    } 
} 