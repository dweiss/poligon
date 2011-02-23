package test;


public class ReferencesSaxon implements CallOrNot
{
    @Override
    public void doCall(boolean call)
    {
        if (call) {
            new net.sf.saxon.TransformerFactoryImpl();
        }
    }
}
