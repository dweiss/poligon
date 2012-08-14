import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.Version;


public class CheckHunspell
{
    public static void main(String [] args)
        throws Exception
    {
        ClassLoader cl = CheckHunspell.class.getClassLoader();
        HunspellDictionary dict = new HunspellDictionary(
            cl.getResourceAsStream("hr_HR.aff"),
            cl.getResourceAsStream("hr_HR.dic"),
            Version.LUCENE_CURRENT,
            true);

        String in = "Hrvatska (službeni naziv: Republika Hrvatska) europska je država, zemljopisno smještena na prijelazu iz Srednje u Jugoistočnu Europu. Hrvatska graniči na sjeveru sa Slovenijom i Mađarskom, na istoku sa Srbijom, na jugu i istoku s Bosnom i Hercegovinom i Crnom Gorom. S Italijom ima morsku granicu. Tijekom hrvatske povijesti najznačajniji kulturološki utjecaji dolazili su iz srednjoeuropskog i sredozemnog kulturnog kruga.";
        
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        TokenStream tokenStream = analyzer.tokenStream("", new StringReader(in));
        HunspellStemFilter filter = new HunspellStemFilter(tokenStream, dict);

        CharTermAttribute term = filter.getAttribute(CharTermAttribute.class);
        PositionIncrementAttribute pos = filter.getAttribute(PositionIncrementAttribute.class);
        while (filter.incrementToken()) {
            System.out.println(
                term.toString() + ": " +
                pos.getPositionIncrement());
        }
    }
}
