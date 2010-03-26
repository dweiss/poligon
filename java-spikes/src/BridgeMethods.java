import java.util.Arrays;

public class BridgeMethods
{
    public interface Identifiable<ID>
    {
        public ID getId();
    }

    public interface IntegerIdentifiable extends Identifiable<Integer>
    {
    }

    public abstract class CIdentifiable<ID>
    {
        public abstract ID getId();
    }

    public class CIntegerIdentifiable extends CIdentifiable<Integer>
    {
        public Integer getId()
        {
            return null;
        }
    }
    

    public static void main(String [] args)
    {
        dumpMethods(Identifiable.class);
        dumpMethods(IntegerIdentifiable.class);
        dumpMethods(CIdentifiable.class);
        dumpMethods(CIntegerIdentifiable.class);
    }


    private static void dumpMethods(Class<?> c)
    {
        System.out.println("Class: " + c.getName() 
            + "\n  Declared>" + Arrays.toString(c.getDeclaredMethods())
            + "\n  Methods >" + Arrays.toString(c.getMethods()));
        
    }
}
