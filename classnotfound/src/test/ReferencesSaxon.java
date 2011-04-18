package test;


public class ReferencesSaxon implements CallOrNot
{
    @Override
    public void doCall(boolean call)
    {
        Object factory = null;
        if (call) {
            new net.sf.saxon.TransformerFactoryImpl();
        }
        System.out.println("Factory: " + factory);
    }
}
