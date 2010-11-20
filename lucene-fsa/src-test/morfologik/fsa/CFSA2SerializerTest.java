package morfologik.fsa;

import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.Arrays;

import org.junit.Test;

/*
 * 
 */
public class CFSA2SerializerTest extends SerializerTestBase {
    /**
     * 
     */
    @Test
    public void testFSA5SerializerSimple() throws IOException {
        byte[][] input = new byte[][] { 
            { 'a' }, 
            { 'b' }, 
        };
    
        Arrays.sort(input, FSABuilder.LEXICAL_ORDERING);
        State s = FSABuilder.build(input);
    
        checkSerialization(input, s);
    }

    /*
     * 
     */
    @Test
    public void testBinaryIsCFSA() throws IOException {
        byte[][] input = new byte[][] {};
        State s = FSABuilder.build(input);

        FSA fsa = FSA.read(new ByteArrayInputStream(
                createSerializer().serialize(s, new ByteArrayOutputStream()).toByteArray()));
        
        assertTrue(fsa instanceof CFSA2);
    }

    /*
     * 
     */
	protected FSASerializer createSerializer() {
        return new CFSA2Serializer();
    }
}
