
public class ClassNotFound
{
    public static void main(String [] args) throws Exception
    {
        Class<?> c = Class.forName("org.pentaho.di.job.entries.xslt.JobEntryXSLT", false, 
            Thread.currentThread().getContextClassLoader());
        System.out.println(c);
        
        c.getConstructor(new Class [0]).newInstance();
    }
}
