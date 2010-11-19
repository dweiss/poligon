package lucene.experiments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import morfologik.fsa.FSA;
import morfologik.fsa.FSA5Serializer;
import morfologik.fsa.FSABuilder;
import morfologik.fsa.FSATraversal;
import morfologik.fsa.State;

/**
 * Compare the long-value lookup speed using binary search and a finite state automaton.
 */
public class CompareSpeed
{
    /*
     * 
     */
    public static void main(String [] args) throws Exception
    {
        ArrayList<byte []> input;

        // Get the input from a text file.

        // log("Reading input...");
        // ArrayList<byte[]> input = readInput(
        // new BufferedInputStream(new FileInputStream(args[0])));

        // Or get it from a Polish dictionary (simpler).
        log("Generating input...");
        input = new ArrayList<byte []>();

        int inputSequences = 2000000;
        final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        for (ByteBuffer bb : FSA.read(ccl.getResourceAsStream("morfologik/dictionaries/pl.dict")))
        {
            input.add(Arrays.copyOf(bb.array(), bb.remaining()));
            if (--inputSequences <= 0) break;
        }

        log("Sorting "+ input.size() + " sequences...");
        Collections.sort(input, FSABuilder.LEXICAL_ORDERING);

        /*
         * Our "index data" will be 75% of the overall data. Also, generate
         * some random longs for the "values".
         */
        log("Generating rnd values...");
        int dataSize = (int) (0.75 * input.size());
        byte [][] data = input.subList(0, dataSize).toArray(new byte [dataSize] []);
        long [] values = generateRandomLongs(dataSize);

        /*
         * Construct FSA
         */
        log("Building FSA with perfect hashes...");
        long start = System.currentTimeMillis();
        State root = FSABuilder.build(data);
        byte [] serializedFsa = new FSA5Serializer()
            .withNumbers()
            .withBreadthFirstOrder(5)
            .serialize(root, new ByteArrayOutputStream()).toByteArray();
        FSA fsa = FSA.read(new ByteArrayInputStream(serializedFsa));
        long end = System.currentTimeMillis();
        log("FSA built in: " + String.format("%.2f", (end - start) / 1000.0) + " sec.");
        log("FSA size (bytes): " + serializedFsa.length);

        /*
         * Repeat using these
         */
        long seed = 0x11223344;
        int REPEATS = 5000000;

        /*
         * Searches using binary search. 
         */
        Random rnd = new Random(seed);
        int found = 0;
        long cumulativeCheck = 0;

        /*
         * Binary search.
         */
        log("\nSpeed test, binary search...");
        start = System.currentTimeMillis();
        for (int i = 0; i < REPEATS; i++)
        {
            byte [] key = input.get(rnd.nextInt(input.size()));
            int keyIndex = java.util.Arrays.binarySearch(data, key, FSABuilder.LEXICAL_ORDERING);
            if (keyIndex >= 0)
            {
                cumulativeCheck += values[keyIndex];
                found++;
            }
        }
        end = System.currentTimeMillis();
        log("Found sequences: " + found + ", cum. check: " + cumulativeCheck);
        log("bsearch: " + String.format("%.2f", (end - start) / 1000.0) + " sec.");

        /*
         * FSA, with perfect hashes. 
         */
        log("\nSpeed test, FSA search w/perfect hash...");
        found = 0;
        cumulativeCheck = 0;
        rnd = new Random(seed);
        start = System.currentTimeMillis();
        FSATraversal t = new FSATraversal(fsa);
        for (int i = 0; i < REPEATS; i++)
        {
            byte [] key = input.get(rnd.nextInt(input.size()));

            int keyIndex = t.perfectHash(key);
            if (keyIndex >= 0)
            {
                cumulativeCheck += values[keyIndex];
                found++;
            }
        }
        end = System.currentTimeMillis();
        log("Found sequences: " + found + ", cum. check: " + cumulativeCheck);
        log("fsa: " + String.format("%.2f", (end - start) / 1000.0) + " sec.");
        

        /*
         * FSA, hit/miss only. 
         */
        log("\nSpeed test, FSA search, hit/miss only...");
        found = 0;
        rnd = new Random(seed);
        start = System.currentTimeMillis();
        for (int i = 0; i < REPEATS; i++)
        {
            byte [] key = input.get(rnd.nextInt(input.size()));

            if (hasMatch(fsa, key))
            {
                found++;
            }
        }
        end = System.currentTimeMillis();
        log("Found sequences: " + found);
        log("fsa: " + String.format("%.2f", (end - start) / 1000.0) + " sec.");
    }

    /**
     * 
     */
    private static long [] generateRandomLongs(int dataSize)
    {
        Random rnd = new Random(0x11223344);
        long [] result = new long [dataSize];
        for (int i = 0; i < dataSize; i++)
            result[i] = rnd.nextLong();

        return result;
    }

    /**
     * 
     */
    private static boolean hasMatch(FSA fsa, byte [] key)
    {
        int node = fsa.getRootNode();
        final int max = key.length;
        for (int i = 0; i < max; i++)
        {
            final int arc = fsa.getArc(node, key[i]);
            if (arc != 0)
            {
                if (fsa.isArcFinal(arc) && i + 1 == max)
                {
                    return true;
                }

                if (fsa.isArcTerminal(arc)) return false;

                // make a transition along the arc.
                node = fsa.getEndNode(arc);
            }
            else
            {
                // The label was not found.
                return false;
            }
        }

        return false;
    }

    /**
     * Read the input as an array of bytes.
     */
    private static ArrayList<byte []> readInput(InputStream is) throws IOException
    {
        final ArrayList<byte []> input = new ArrayList<byte []>();

        boolean warned = false;
        byte [] buffer = new byte [0];
        int line = 0, b, pos = 0;
        while ((b = is.read()) != -1)
        {
            if (b == '\r' && !warned)
            {
                log("Warning: input contains carriage returns?");
                warned = true;
            }

            if (b == '\n')
            {
                processLine(input, buffer, pos);
                pos = 0;
                if ((line++ % 10000) == 0)
                {
                    log("Lines read: " + (line - 1));
                }
            }
            else
            {
                if (pos >= buffer.length)
                {
                    buffer = Arrays.copyOf(buffer, buffer.length + 1);
                }
                buffer[pos++] = (byte) b;
            }
        }
        processLine(input, buffer, pos);
        return input;
    }

    /**
     * Process a single line.
     */
    private static void processLine(ArrayList<byte []> input, byte [] buffer, int pos)
    {
        if (pos == 0) return;
        input.add(Arrays.copyOf(buffer, pos));
    }

    private static void log(String msg)
    {
        System.out.println(msg);
    }
}