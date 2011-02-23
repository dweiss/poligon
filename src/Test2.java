import test.CallOrNot;


public class Test2
{
    public static void main(String [] args) throws Exception
    {
        ((CallOrNot) Class.forName("test.ReferencesSaxon2").newInstance())
            .doCall(args.length > 0);
    }
}