import test.CallOrNot;


public class Test1
{
    public static void main(String [] args) throws Exception
    {
        ((CallOrNot) Class.forName("test.ReferencesSaxon").newInstance())
            .doCall(args.length > 0);
    }
}