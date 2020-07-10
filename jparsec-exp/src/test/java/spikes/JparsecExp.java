package spikes;

import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JparsecExp {
  @Test
  public void t1() throws IOException {
    Terminals OPERATORS =
        Terminals.operators().words(Scanners.IDENTIFIER).keywords("foo", "bar").build();

    Parser<String> t1 =
        Parsers.or(OPERATORS.token("bar").retn("bar"), OPERATORS.token("foo").retn("foo"));

    Parser<List<String>> p = t1.many();
    p = p.from(OPERATORS.tokenizer(), Scanners.WHITESPACES);
    System.out.println(parse(p, "foo bar foo"));
  }

  @Test
  public void t2() throws IOException {
    Terminals OPERATORS =
        Terminals.operators().words(Scanners.IDENTIFIER).keywords("foo", "bar").build();

    Parser<String> t1 =
        Parsers.or(OPERATORS.token("bar").retn("bar"), OPERATORS.token("foo").retn("foo"));

    Parser<List<String>> p = t1.many();
    p = p.from(OPERATORS.tokenizer(), Scanners.WHITESPACES);
    System.out.println(parse(p, "foo bar foo"));
  }

  private <T> T parse(Parser<T> p, String input) {
    return p.parse(input);
  }
}
