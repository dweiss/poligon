import java.net.ServerSocket;
import java.net.Socket;


public class SocketTest
{
    public static void main(String [] args) throws Exception
    {
        final ServerSocket ss = new ServerSocket(50000);

        new Thread() {
            public void run() {
                try {
                    while (true) {
                        Socket s = new Socket("localhost", 50000);
                        s.close();
                    }
                } catch (Exception e) {
                    System.out.println("Buhu, terminating: " + e.getMessage());
                }
            }
        }.start();

        for (Socket s = ss.accept(); s != null; s = ss.accept()) {
            s.close();
        }        
    }
}
