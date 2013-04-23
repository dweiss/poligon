import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.Version;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.DefaultTokenizerFactory;
import org.carrot2.text.util.MutableCharArray;


public class DumpTerms
{
    public static void main(String [] args)
        throws Exception
    {
        InputStreamReader reader = new InputStreamReader(
            new BufferedInputStream(
                new FileInputStream("C:\\carrot2\\resources\\corpora\\searchresults\\dcs-txt\\combined.txt")),
            "UTF-8");

        // parseWithLucene(reader);
        // parseWithC2(reader);

        TokenStream ts = new Tokenizer(reader)
        {
            final ExtendedWhitespaceTokenizer ewt;
            final CharTermAttribute termAtt;
            final MutableCharArray buffer = new MutableCharArray();

            {
                ewt = new ExtendedWhitespaceTokenizer();
                ewt.reset(input);

                termAtt = addAttribute(CharTermAttribute.class);
            }
            
            @Override
            public boolean incrementToken() throws IOException
            {
                short tokenType = ewt.nextToken();
                if (tokenType == ITokenizer.TT_EOF) {
                    return false;
                }
                
                ewt.setTermBuffer(buffer);
                termAtt.resizeBuffer(buffer.length());
                System.arraycopy(
                    buffer.getBuffer(), 0,
                    termAtt.buffer(), 0, 
                    buffer.length());
                termAtt.setLength(buffer.length());
                return true;
            }
        };

        CharTermAttribute cht = ts.getAttribute(CharTermAttribute.class);
        for (Iterator<Class<? extends Attribute>> i = ts.getAttributeClassesIterator(); i.hasNext();) {
            System.out.println("> " + i.next());
        }
        long overall = 0;
        long start = System.currentTimeMillis();
        while (ts.incrementToken()) {
            overall += cht.length();
        }
        System.out.println(String.format("%,d chars, %,d millis",
            overall,
            System.currentTimeMillis() - start));
    }

    private static void parseWithC2(InputStreamReader reader) throws IOException
    {
        ITokenizer tokenizer = new DefaultTokenizerFactory().getTokenizer(LanguageCode.ENGLISH);
        tokenizer.reset(reader);
        long overall = 0;
        long start = System.currentTimeMillis();
        int ttype;
        MutableCharArray tokenImage = new MutableCharArray();
        while ((ttype = tokenizer.nextToken()) != ITokenizer.TT_EOF) {
            tokenizer.setTermBuffer(tokenImage);
            overall += tokenImage.length();
        }
        System.out.println(String.format("%,d chars, %,d millis",
            overall,
            System.currentTimeMillis() - start));
    }

    private static void parseWithLucene(InputStreamReader reader) throws IOException
    {
        Tokenizer ts = new StandardTokenizer(Version.LUCENE_40, reader);
        CharTermAttribute cht = ts.getAttribute(CharTermAttribute.class);
        for (Iterator<Class<? extends Attribute>> i = ts.getAttributeClassesIterator(); i.hasNext();) {
            System.out.println("> " + i.next());
        }
        long overall = 0;
        long start = System.currentTimeMillis();
        while (ts.incrementToken()) {
            overall += cht.length();
        }
        System.out.println(String.format("%,d chars, %,d millis",
            overall,
            System.currentTimeMillis() - start));
    }
}
