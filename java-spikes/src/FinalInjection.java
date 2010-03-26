import java.lang.reflect.Field;


/**
 * An example of what I had in mind by a more "intuitive" touch focus indicator.
 */
public class FinalInjection
{
    public final String s = null;

    public static void main(String [] args) throws Exception
    {
        Field declaredField = FinalInjection.class.getDeclaredField("s");
        declaredField.setAccessible(true);
        FinalInjection t = new FinalInjection();
        declaredField.set(t, "bubu");
        System.out.println(t.s);
    }
}
