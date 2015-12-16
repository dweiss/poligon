package experiments;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.LongLongHashMap;
import com.google.common.io.ByteStreams;

public class Renumber {
  public static void main(String[] args) throws Exception {
    ArrayList<Path> names = new ArrayList<>();
    Path revsDir = Paths.get("d:\\data\\lucene-svn\\revs");
    Files.walkFileTree(revsDir, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith(".rev")) {
          names.add(file);
        }
        return FileVisitResult.CONTINUE;
      }
    });

    // Read all revisions.
    LongLongHashMap revisions = new LongLongHashMap();
    int cnt = 0;
    for (Path p : names) {
      if ((++cnt % 10000) == 0) {
        System.out.println("1> " + cnt);
      }
      visitDumpFile(p,
          (headers) -> {
            String revNum = headers.get("Revision-number");
            if (revNum != null) {
              revisions.put(Long.parseLong(revNum), 0);
              String revName = "r" + revNum + ".rev";
              if (!p.getFileName().toString().equals(revName)) {
                throw new RuntimeException("Expected identical rev. numbers and files: "
                    + p.getFileName() + " " + revName);
              }
            }
          },
          (body) -> {});
    }

    // Remap revisions to be contiguous and start at 1.
    long [] originalRevs = Arrays.stream(revisions.keys().toArray()).sorted().toArray();
    System.out.println("Revs: " + originalRevs.length);
    
    long rev = 1;
    for (long key : originalRevs) {
      revisions.put(key, rev++);
    }

    // Rewrite individual revisions into a contiguous dump file.
    cnt = 0;
    try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(revsDir.resolveSibling("renamed.dump")))) {
      os.write("SVN-fs-dump-format-version: 3\n\n".getBytes(StandardCharsets.US_ASCII));
      os.write("UUID: 13f79535-47bb-0310-9956-ffa450edef68\n\n".getBytes(StandardCharsets.US_ASCII));

      for (long origRev : originalRevs) {
        long newRev = revisions.getOrDefault(origRev, -1);
        if (newRev < 0) throw new RuntimeException();
  
        if ((++cnt % 10000) == 0) {
          System.out.println("2> " + cnt);
        }
        
        Path p = revsDir.resolve("r" + origRev + ".rev");
        visitDumpFile(p,
            (headers) -> {
              if (headers.size() == 1) {
                if (headers.containsKey("SVN-fs-dump-format-version") ||
                    headers.containsKey("UUID")) {
                  return;
                }
              }

              // Emit headers, modify them on the way.
              headers.forEach((k, v) -> {
                if (k.equals("Node-copyfrom-rev") ||
                    k.equals("Revision-number")) {
                  long vv = Long.parseLong(v);
                  if (!revisions.containsKey(vv)) {
                    // Copied from an intermediate, non-Lucene revision.
                    int slot = Arrays.binarySearch(originalRevs, vv);
                    slot = -slot - 1;
                    assert originalRevs[slot] > vv &&
                           originalRevs[slot - 1] < vv : 
                             originalRevs[slot - 1] + " " + vv + " " + originalRevs[slot];
                   
                    // Take the largest Lucene-tree touching revision.
                    vv = originalRevs[slot - 1];
                    assert revisions.containsKey(vv);
                  }
                  v = Long.toString(revisions.get(vv));
                }
                
                if (k.equals(""))

                try {
                  os.write((k + ": "+ v + "\n").getBytes(StandardCharsets.US_ASCII));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
              
              try {
                os.write('\n');
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            },
            (body) -> {
              try {
                ByteStreams.copy(body, os);
                os.write('\n');
                os.write('\n');
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
      }
    }

    System.out.println("Revisions: " + revisions.size());
  }

  private static void visitDumpFile(Path path, 
                                    Consumer<LinkedHashMap<String, String>> headerConsumer,
                                    Consumer<InputStream> bodyConsumer) throws Exception {
    LinkedHashMap<String, String> headers = new LinkedHashMap<>();
    ByteArrayList lineBuffer = new ByteArrayList();
    try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
      // read headers
      do {
        headers(is, headers, lineBuffer);
        if (headers.isEmpty()) {
          if (is.read() != -1) {
            System.out.println(path + "\n" + new String(ByteStreams.toByteArray(is)));
            throw new IOException();
          }
          break;
        }

        if (!headers.containsKey("Node-path") &&
            !headers.containsKey("Revision-number") &&
            !headers.containsKey("SVN-fs-dump-format-version") &&
            !headers.containsKey("UUID")) {
          // svnrdump produces broken dump nodes for a path copy from the incubator (tika). ignore any
          // nodes that are invalid.
          System.err.println("Odd record in "
              + path + ": " + headers);
          if (headers.containsKey("Content-length")) {
            long length = Long.parseLong(headers.get("Content-length"));
            ByteStreams.skipFully(is, length);
          }
        } else {
          headerConsumer.accept(headers);
  
          if (headers.containsKey("Content-length")) {
            long length = Long.parseLong(headers.get("Content-length"));
            InputStream limit = ByteStreams.limit(is, length);
            bodyConsumer.accept(limit);
            while (limit.skip(length) > 0);
          }
        }
      } while (true);
    }
  }

  private static void headers(InputStream is, LinkedHashMap<String, String> headers, ByteArrayList lineBuffer) 
      throws IOException {
    headers.clear();
outer:
    while (true) {
      lineBuffer.clear();
      int b;
      while ((b = is.read()) >= 0) {
        if (b == '\n') { 
          break;
        }
        lineBuffer.add((byte) b);
      }
      
      if (b == -1) {
        return;
      }

      if (lineBuffer.isEmpty()) {
        if (headers.isEmpty()) {
          continue outer;
        }
        return;
      }
      
      String headerLine = new String(lineBuffer.buffer, 0, lineBuffer.size(), StandardCharsets.US_ASCII);
      int sep = headerLine.indexOf(": ");
      if (sep < 0) throw new IOException("No separator: " + headerLine);

      String key = headerLine.substring(0, sep);
      String value = headerLine.substring(sep + 2);
      if (headers.containsKey(key)) {
        throw new IOException("Dup. key " + key + ":\n 1: " + value + "\n 2: " + headers.get(key));
      }
      headers.put(key, value);
    }
  }
}
