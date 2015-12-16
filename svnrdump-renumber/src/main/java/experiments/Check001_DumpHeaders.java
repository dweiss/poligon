package experiments;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.TreeSet;

public class Check001_DumpHeaders {
  public static void main(String[] args) throws Exception {
    HashSet<String> allHeaderKeys = new HashSet<>();
    SvnDumpHelper.forEach(Paths.get(args[0]), (path, change) -> {
      allHeaderKeys.addAll(change.headers.keySet());
      if (change.headers.containsKey("Node-action")) {
        allHeaderKeys.add("Node-action => " + change.headers.get("Node-action"));
      }
      return true;
    });
    
    new TreeSet<>(allHeaderKeys).stream().forEachOrdered((h) -> System.out.println(h));
  }
}
