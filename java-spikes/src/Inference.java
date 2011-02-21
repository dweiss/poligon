import java.util.List;


public class Inference
{
    /*
    public <A> A x() {
        return null;
    }

    public <B> void y() {
        B o = x();
    }
    */
    
    private <A> List<A> x2() {
        return null;
    }

    private <B> void y2() {
        B o = x2();
    }
}
