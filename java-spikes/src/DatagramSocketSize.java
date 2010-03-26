import java.net.*;

public class DatagramSocketSize
{
    public static void main(String [] args) throws Exception
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    int size = 1024;
                    while (true)
                    {
                        final DatagramSocket sender = new DatagramSocket();
                        sender.setBroadcast(true);
                        sender.setReuseAddress(true);

                        System.out.println("SNDBUF: " + sender.getSendBufferSize());

                        final DatagramPacket p = new DatagramPacket(new byte [size], size);
                        p.setAddress(Inet4Address.getByName("255.255.255.255"));
                        p.setPort(50000);
                        sender.send(p);
                        sender.close();

                        Thread.sleep(1000);
                        size += 1024;
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Buhu, terminating: " + e.getMessage());
                }
            }
        }.start();

        final DatagramSocket receiver = new DatagramSocket(50000);
        receiver.setBroadcast(true);

        final DatagramPacket receivedPacket = new DatagramPacket(new byte [0x10000],
            0x10000);
        System.out.println("RCVBUF: " + receiver.getReceiveBufferSize());
        while (true)
        {
            receiver.receive(receivedPacket);
            System.out.println("Received packet from: " + receivedPacket.getAddress()
                + ", len: " + receivedPacket.getLength());
        }
    }
}
