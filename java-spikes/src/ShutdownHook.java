
public class ShutdownHook
{
    public static void main(String [] args)
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                System.out.println("shutdown hook.");
                while (true) { 
                    try { Thread.sleep(1000); } catch (Exception e) {}
                }
            }
        });
        
        System.exit(0);
    }
}
