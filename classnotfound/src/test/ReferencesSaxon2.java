package test;


public class ReferencesSaxon2 implements CallOrNot
{
    static 
    {
        // this should throw an exception.
        new ReferencesSaxon().doCall(true);
    }
    
    @Override
    public void doCall(boolean call)
    {
        // ignore.
    }
}
