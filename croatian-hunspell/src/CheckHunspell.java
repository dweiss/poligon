import java.util.List;

import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemmer;
import org.apache.lucene.analysis.hunspell.HunspellStemmer.Stem;
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
        HunspellStemmer stemmer = new HunspellStemmer(dict);
        
        List<Stem> stems = stemmer.stem("admiralom");
        for (Stem s : stems) {
            System.out.println("> " + s.getStemString());
        }
    }
}
