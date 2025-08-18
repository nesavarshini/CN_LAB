import java.io.*;
import java.net.*;

class rarpServer {
    public static void main(String args[]) {
        try {
            ServerSocket obj = new ServerSocket(3000);
            Socket obj1 = obj.accept();

            BufferedReader din = new BufferedReader(new InputStreamReader(obj1.getInputStream()));
            DataOutputStream dout = new DataOutputStream(obj1.getOutputStream());

            String[] ip = {"165.165.80.80", "165.165.79.1"};
            String[] mac = {"6A:08:AA:C2", "8A:BC:E3:FA"};

            while (true) {
                String str = din.readLine();
                if (str == null) {
                    break;
                }

                boolean found = false;
                for (int i = 0; i < mac.length; i++) {
                    if (str.equals(mac[i])) {
                        dout.writeBytes(ip[i] + '\n');
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    dout.writeBytes("IP not found\n");
                }
            }

            obj1.close();
            obj.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
