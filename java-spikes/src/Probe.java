import org.junit.Test;

import static org.junit.Assert.*;

public class Probe
{
    /**
     * The test is as follows: a hash probe should test EVERY slot in a hash table EXACTLY once.
     * 
     * We do exactly table.length iterations from a constant hash, checking if a slot has 
     * been visited (if so, interrupt) and if all slots have been visited after the probe finishes
     * (which must be fulfilled anyway). 
     */
    @Test
    public void testMikesProbe1()
    {
        int h = /* hash */ 10;
        int c = 1;
        
        int [] table = new int [1 << 8];
        int mask = table.length - 1;

        for (int i = 0; i < table.length; i++) {
          final int pos = h & mask;

          System.out.println(pos);
          if (table[pos] != 0)
              throw new RuntimeException("Visited twice: " + i + " " + pos);

          table[pos] = 1;

          // quadratic probe
          h += (c + c * c)/2;
          c++;
        }

        for (int v : table)
            assertEquals(1, v);
    }
    
    /**
     * How many rounds to fill all the slots? 
     */
    @Test
    public void testMikesExhaustive()
    {
        int h = /* hash */ 10;
        int c = 1;
        
        int [] table = new int [1 << 8];
        int mask = table.length - 1;
        int count = 0;

        for (int i = 0;; i++) {
          final int pos = h & mask;

          if (table[pos] == 0) {
              if (++count == table.length)
                  fail("Took: " + i + " iterations to fill the array.");
          }

          table[pos] = 1;

          // quadratic probe
          h += (c + c * c)/2;
          c++;
        }
    }    

    /**
     * 
     */
    @Test
    public void testRegularProbe2()
    {
        int h = /* hash */ 10;
        int c = 0;

        int [] table = new int [1 << 8];
        int mask = table.length - 1;

        h = h & mask;
        for (int i = 0; i < table.length; i++) {
          final int pos = h;

          System.out.println(pos);
          if (table[pos] != 0)
              throw new RuntimeException("Visited twice: " + i + " " + pos);

          table[pos] = 1;

          h = (h + (++c)) & mask;
        }        
        
        for (int v : table)
            assertEquals(1, v);
    }
}
