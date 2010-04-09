package dk.brics.automaton;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

public class DaciukMihovTest
{
    static final Random rnd = new Random(0x11223344);

    private final static class MinMax
    {
        public final int min;
        public final int max;

        public MinMax(int min, int max)
        {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
        }

        public int range()
        {
            return max - min;
        }
    }

    @Test
    public void testSimpleFourWords()
    {
        String [] input =
        {
            "art", "fart", "flirt", "start"
        };
        Automaton a = Automaton.makeStringUnion(input);
        Automaton b = makeUnion(input);
        Assert.assertEquals(a, b);
    }

    @Test
    public void testEmpty()
    {
        String [] input = {};
        Automaton a = Automaton.makeStringUnion(input);
        Automaton b = makeUnion(input);
        Assert.assertEquals(a, b);
    }

    @Test
    public void testRandom2000()
    {
        String [] input = generateRandom(2000, new MinMax(2, 10), new MinMax('a', 'z'));
        long start = System.currentTimeMillis();
        Automaton a = Automaton.makeStringUnion(input);
        long daciuk = System.currentTimeMillis();
        Automaton b = makeUnion(input);
        long union = System.currentTimeMillis();
        System.out.println("Daciuk: " + (daciuk - start) + ", union: " + (union - daciuk));
        Assert.assertEquals(a, b);
    }

    private Automaton makeUnion(String [] input)
    {
        ArrayList<Automaton> automata = new ArrayList<Automaton>();
        for (String s : input)
            automata.add(Automaton.makeString(s));
        return Automaton.minimize(Automaton.union(automata));
    }

    /**
     * Generate a sorted list of random sequences.
     */
    static String [] generateRandom(int count, MinMax length, MinMax alphabet)
    {
        final String [] input = new String [count];
        for (int i = 0; i < count; i++)
        {
            input[i] = randomString(rnd, length, alphabet);
        }
        Arrays.sort(input);
        return input;
    }

    /**
     * Generate a random string.
     */
    private static String randomString(Random rnd, MinMax length, MinMax alphabet)
    {
        char [] chars = new char [length.min + rnd.nextInt(length.range())];
        for (int i = 0; i < chars.length; i++)
        {
            chars[i] = (char) (alphabet.min + 2 * rnd.nextInt(alphabet.range()));
        }
        return new String(chars);
    }
}
