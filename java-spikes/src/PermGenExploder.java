import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 
 */
public class PermGenExploder
{
    public static class MyClazz
    {
        static
        {
            try
            {
                System.loadLibrary("fufu");
            }
            catch (Throwable t)
            {
                // do nothing.
            }
        }
        public enum MyEnum
        {
            ONE, TWO
        }

        public String toString()
        {
            return "I am " + this.getClass().hashCode() + ", loaded with: "
                + this.getClass().getClassLoader().hashCode() + ", enum class: "
                + MyEnum.ONE.getClass().hashCode();
        }
    }

    private static class MyClassLoader extends ClassLoader
    {
        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException
        {
            if (name.matches(".+MyClazz.*"))
            {
                try
                {
                    InputStream is = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(name.replace('.', '/') + ".class");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = is.read()) != -1)
                        baos.write(i);
                    is.close();
                    byte [] code = baos.toByteArray();

                    Class<?> clazz = super.defineClass(null, code, 0, code.length);
                    return clazz;
                }
                catch (Exception e0)
                {
                    throw new RuntimeException(e0);
                }
            }
            return super.loadClass(name, resolve);
        }
    }

    public static void main(String [] args) throws Exception
    {
        for (int i = 0; i < 100000; i++)
        {
            MyClassLoader classLoader = new MyClassLoader();
            Class<?> clazz = classLoader.loadClass(MyClazz.class.getName());
            System.out.println(clazz.newInstance().toString());
        }

        Thread.sleep(1000000);
    }
}
