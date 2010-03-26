import java.lang.management.*;

public class MemoryConsumptionMeasurements
{
    public static void main(String [] args) throws Exception
    {
        byte [] tab = new byte [0];

        while (true)
        {
            tab = new byte [tab.length + 1024 * 1024];
            Thread.sleep(1000);
            usedMemory();
            System.out.println(tab.length / 1024 + "K");
        }
    }

    static void usedMemory()
    {
        System.gc();
        MemoryMXBean memoryMXBean = java.lang.management.ManagementFactory
            .getMemoryMXBean();
        System.out.println(memoryMXBean.getHeapMemoryUsage());

        for (MemoryPoolMXBean b : ManagementFactory.getMemoryPoolMXBeans())
        {
            System.out.println(b.getName() + " " + b.getPeakUsage());
        }
    }
}
