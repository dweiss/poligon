import java.lang.management.*;
import java.util.List;
import java.util.Locale;



public class PeakMemoryUseTest
{
    public static void main(String [] args)
    {
        final List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean bean : beans)
        {
            bean.resetPeakUsage();
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                final float MB = 1024 * 1024;

                System.err.println("Peak memory usage:");
                for (MemoryPoolMXBean bean : beans)
                {
                    final MemoryUsage mu = bean.getPeakUsage();
                    System.err.println(
                        String.format(Locale.ENGLISH, "Pool: %s, Committed: %.2f, Used: %.2f", 
                            bean.getName(), mu.getCommitted() / MB, mu.getUsed() / MB));
                }
                
                for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans())
                {
                    System.err.println(
                        String.format(Locale.ENGLISH, "GC: %s, Collections: %d, Time: %.2f s.", 
                            gc.getName(), gc.getCollectionCount(), gc.getCollectionTime() / 1000.0d));
                }
            }
        });

        int [] t = new int [10 * 1024 * 1024];

        System.gc();
    }
}
