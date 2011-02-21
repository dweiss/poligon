
public class GenericArrayCasts<T extends Number>
{
    
    public static void main(String [] args)
    {
        new GenericArrayCasts<Integer>().foo(
            new Integer [] {1});
    }

    private T foo(T [] boo)
    {
        return boo[0];
    }
}
