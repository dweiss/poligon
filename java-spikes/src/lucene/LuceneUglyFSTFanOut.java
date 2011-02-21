package lucene;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.fst.*;

/**
 * Create a synthetic FST that has a weird, abnormal state fanout.
 */
public class LuceneUglyFSTFanOut
{
    public static void main(String [] args) throws IOException
    {
        ArrayList<String> out = new ArrayList<String>();
        StringBuilder b = new StringBuilder();
        generate(out, b, 'a', 'k', 10);
        String [] input = out.toArray(new String [out.size()]);

        Arrays.sort(input);

        FST<Object> fst = compile0(input);

        Util.toDot(fst, new OutputStreamWriter(System.out), true, false);
    }

    private static void generate(ArrayList<String> out, StringBuilder b, char from,
        char to, int depth)
    {
        if (depth == 0 || from == to)
        {
            String seq = b.toString() + "_" + out.size() + "_end";
            out.add(seq);
            System.err.println(seq);
        }
        else
        {
            for (char c = from; c <= to; c++)
            {
                b.append(c);
                generate(out, b, from, c == to ? to : from, depth - 1);
                b.deleteCharAt(b.length() - 1);
            }
        }
    }

    public static FST<Object> compile0(String [] lines) throws IOException
    {
        final NoOutputs outputs = NoOutputs.getSingleton();
        final Object nothing = outputs.getNoOutput();
        final Builder<Object> b = new Builder<Object>(FST.INPUT_TYPE.BYTE1, 0, 0, true,
            outputs);

        // TODO: this will actually stop and ignore \r, does it occur by itself anywhere?
        int line = 0;
        final BytesRef term = new BytesRef();
        while (line < lines.length)
        {
            String w = lines[line++];
            if (w == null)
            {
                break;
            }
            term.copy(w);
            b.add(term, nothing);
        }

        return b.finish();
    }
}
