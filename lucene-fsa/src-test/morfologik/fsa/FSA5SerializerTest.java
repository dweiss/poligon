package morfologik.fsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;


public class FSA5SerializerTest extends SerializerTestBase<FSA5Serializer> {
	protected FSA5Serializer createSerializer() {
        return new FSA5Serializer();
    }

    /**
     * 
     */
    @Test
    public void testBreadthFirstReorder() throws IOException {
        byte[][] input = new byte[][] { 
                { 'a' }, 
                { 'a', 'b', 'a' },
                { 'a', 'c' }, 
                { 'b' }, 
                { 'b', 'a' }, 
                { 'c' }, };
    
        Arrays.sort(input, FSABuilder.LEXICAL_ORDERING);
        State s = FSABuilder.build(input);
        
        createSerializer().serialize(s, new ByteArrayOutputStream());
        createSerializer().withBreadthFirstOrder(1).serialize(s, new ByteArrayOutputStream());
        createSerializer().withBreadthFirstOrder(2).serialize(s, new ByteArrayOutputStream());
        createSerializer().withBreadthFirstOrder(3).serialize(s, new ByteArrayOutputStream());
        createSerializer().withBreadthFirstOrder(10).serialize(s, new ByteArrayOutputStream());
    }
}
