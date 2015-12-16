package experiments;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Check002_CrossBranchChanges {
  public static void main(String[] args) throws Exception {
    ArrayList<Changeset> blocks = new ArrayList<>();

    SvnDumpHelper.forEach(Paths.get(args[0]), (path, chset) -> {
      if (chset.headers.containsKey("Revision-number")) {
        // new revision, flush previous block.
        flush(blocks);
      }

      chset.body = null;
      blocks.add(chset);      

      return true;
    });
    flush(blocks);
  }

  static String [] normalizedStarts = {
      "lucene/site",
      "lucene/nutch",
      "lucene/lucy",
      "lucene/tika",
      "lucene/hadoop",
      "lucene/mahout",
      "lucene/pylucene",
      "lucene/lucene.net",
      "lucene/old_versioned_docs",
      "lucene/openrelevance",
      "lucene/board-reports",
      
      "lucene/java/site",
      "lucene/java/nightly",

      "lucene/dev/nightly",
      "lucene/dev/lucene2878",

      "lucene/sandbox/luke",
      "lucene/solr/nightly",
  };

  private static void flush(ArrayList<Changeset> changesets) {
    HashSet<String> movedPaths = new HashSet<>();
    HashSet<String> changedPaths = new HashSet<>();
    changesets.forEach((chset) -> {
      String path = chset.headers.get("Node-path");
      String from = chset.headers.get("Node-copyfrom-path");
      if (from != null && path == null) {
        throw new RuntimeException();
      }
      
      if (path != null) {
        path = normalize(path);
        from = normalize(from);
        if (from != null && !from.equals(path)) {
          movedPaths.add(from + " => " + path);
          changedPaths.add(path);
        } else {
          changedPaths.add(path);
        }
      }
    });
    
    if (movedPaths.size() > 1) {
      System.out.println("> " + changesets.get(0).headers.get("Revision-number"));
      movedPaths.forEach((p) -> System.out.println("  " + p));
    }
    
    changesets.clear();
  }

  protected static String normalize(String v) {
    if (v == null) return null;

    for (String prefix : normalizedStarts) {
      if (v.startsWith(prefix)) {
        v = prefix;
      }
    }

    v = v.replaceAll("(branches/[^/]+)(.*)", "$1");
    v = v.replaceAll("(tags/[^/]+)(.*)", "$1");
    v = v.replaceAll("(/trunk).*", "$1");
    return v;
  }
}
