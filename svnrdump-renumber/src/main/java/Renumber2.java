import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.ByteArrayList;
import com.google.common.io.ByteStreams;

public class Renumber2 {
  public static void main(String[] args) throws Exception {
    Path revs = Paths.get(args[0]);
    Path outputPath = revs.resolveSibling(revs.getFileName().toString() + ".rewritten");

    try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(outputPath))) {
      if (Files.isRegularFile(revs)) {
        // assume the input is a ZIP file.
        try (FileSystem fs = FileSystems.newFileSystem(revs, null)) {
          Iterator<Path> roots = fs.getRootDirectories().iterator();
          Path root = roots.next();
          if (roots.hasNext()) {
            throw new IOException("More than one root of a ZIP file?");
          }

          processDumpFiles(os, root);
        }
      } else {
        // assume the input is a folder.
        processDumpFiles(os, revs);
      }
    }
  }

  private static void processDumpFiles(OutputStream os, Path root) throws IOException {
    List<Path> revDumps = Files.walk(root)
      .filter((p) -> Files.isRegularFile(p) &&
                     p.getFileName().toString().matches("r[0-9]+\\.rev"))
      .sorted((a, b) -> Long.compare(revNumFromFileName(a), revNumFromFileName(b)))
      .collect(Collectors.toList());
    printf("%,d input dump files.", revDumps.size());

    Map<Long, Long> revRemap = new HashMap<>(); 
    revDumps.forEach((file) -> {
      long revNum = revNumFromFileName(file);
      revRemap.put(revNum, revRemap.size() + 1L);
    });
    long [] revisions = revRemap.keySet().stream().mapToLong(Long::longValue).sorted().toArray();

    for (Path revDump : revDumps) {
      visitDumpFile(revDump, (block) -> {
        // Check sanity.
        if (block.headers.containsKey("Revision-number")) {
          if (Long.parseLong(block.headers.get("Revision-number")) !=
              revNumFromFileName(revDump)) {
            throw new RuntimeException("Rev num mismatch: " + revDump);
          }
        }

        // Remap headers.
        block.headers.entrySet().stream().forEach((e) -> {
          String k = e.getKey();
          if (k.equals("Node-copyfrom-rev") ||
              k.equals("Revision-number")) {
            e.setValue(Long.toString(revMap(revRemap, revisions, Long.parseLong(e.getValue()))));
          }});

        try {
          block.writeTo(os);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private static long revMap(Map<Long, Long> revRemap, long [] revisions, long rev) {
    if (!revRemap.containsKey(rev)) {
      // Copied from an intermediate, non-Lucene revision (outside dumped path).
      int slot = Arrays.binarySearch(revisions, rev);
      slot = -slot - 1;
      assert revisions[slot] > rev &&
             revisions[slot - 1] < rev : 
             revisions[slot - 1] + " " + rev + " " + revisions[slot];

      // Take the largest Lucene-tree touching revision.
      rev = revisions[slot - 1];
      assert revRemap.containsKey(rev);
    }
    return revRemap.get(rev);
  }

  private static long revNumFromFileName(Path path) {
    String p = path.getFileName().toString();
    if (!p.startsWith("r") ||
        !p.endsWith(".rev")) {
      throw new RuntimeException("Invalid revision file name: " + p);
    }
    return Long.parseLong(p.substring(1, p.length() - /* ".rev".length() */ 4));
  }

  private static void printf(String fmt, Object... args) {
    System.out.println(String.format(Locale.ROOT, fmt, args));
  }

  static class Block {
    LinkedHashMap<String, String> headers;
    Map<String, String> properties;
    byte[] body;

    Block(LinkedHashMap<String, String> headers, Map<String, String> props, byte[] body) {
      this.headers = headers;
      this.properties = props;
      this.body = body;
    }

    public void writeTo(OutputStream os) throws IOException {
      byte[] props = EMPTY;
      if (properties.size() > 0 ||
          headers.containsKey("Prop-content-length")) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Map.Entry<String, String> e : properties.entrySet()) {
          byte [] key = ascii(e.getKey());
          if (e.getValue() == null) {
            baos.write(ascii("D " + (key.length) + "\n"));
            baos.write(key);
            baos.write('\n');
          } else {
            baos.write(ascii("K " + (key.length) + "\n"));
            baos.write(key);
            baos.write('\n');
            byte [] value = ascii(e.getValue());
            baos.write(ascii("V " + (value.length) + "\n"));
            baos.write(value);
            baos.write('\n');
          }
        }
        baos.write(ascii("PROPS-END\n"));
        props = baos.toByteArray();
      }

      // headers
      for (Map.Entry<String, String> e : headers.entrySet()) {
        if (e.getKey().equals("Content-length")) {
          if (props.length + body.length > 0) {
            os.write(ascii(e.getKey() + ": " + (props.length + body.length)));
          }
        } else if (e.getKey().equals("Prop-content-length")) {
          if (props.length > 0) {
            os.write(ascii(e.getKey() + ": " + (props.length)));
          }
        } else {
          os.write(ascii(e.getKey() + ": " + e.getValue()));
        }
        os.write('\n');
      }

      if (props.length + body.length > 0) {
        os.write('\n');
      }

      // props
      if (props.length > 0) {
        os.write(props);
      }

      // body
      if (body.length > 0) {
        os.write(body);
      }

      os.write('\n');
    }

    private byte[] ascii(String key) {
      return key.getBytes(StandardCharsets.ISO_8859_1);
    }
  }

  private static final byte [] EMPTY = new byte [0];

  private static void visitDumpFile(Path path, 
                                    Consumer<Block> consumer) throws IOException {
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
          printf("Odd record in %s: %s (skipping)", path, headers);
          if (headers.containsKey("Content-length")) {
            long length = Long.parseLong(headers.get("Content-length"));
            ByteStreams.skipFully(is, length);
          }
        } else {
          byte [] body = EMPTY;
          Map<String, String> props = Collections.emptyMap();
          if (headers.containsKey("Content-length")) {
            long plength = 0;
            if (headers.containsKey("Prop-content-length")) {
              plength = Long.parseLong(headers.get("Prop-content-length"));
              byte[] propSection = new byte [(int) plength];
              ByteStreams.readFully(is, propSection);
              props = parseProps(propSection);
            }

            long blength = Long.parseLong(headers.get("Content-length")) - plength;
            body = new byte [(int) blength];
            ByteStreams.readFully(is, body);
          }

          consumer.accept(new Block(headers, props, body));
        }
      } while (true);
    }
  }

  private static Map<String, String> parseProps(byte[] propSection) throws IOException {
    Map<String, String> props = new LinkedHashMap<>();
    InputStream is = new ByteArrayInputStream(propSection);
    // System.out.println(">>" + new String(propSection, StandardCharsets.US_ASCII) + "<<");
    do {
      String line = readLine(is);
      if (line.startsWith("PROPS-END")) {
        return props;
      }
      if (line.startsWith("D")) {
        int klen = Integer.parseInt(line.substring(2));
        String key = ascii(is, klen);
        consumeEol(is);
        props.put(key, null); // Deleted key
      } else if (line.startsWith("K")) {
        int klen = Integer.parseInt(line.substring(2));
        String key = ascii(is, klen);
        consumeEol(is);
        line = readLine(is);
        if (!line.startsWith("V")) {
          throw new IOException("Bad: " + line);
        }
        int vlen = Integer.parseInt(line.substring(2));
        String value = ascii(is, vlen);
        consumeEol(is);
        // System.out.println(key + " => " + value);
        if (props.put(key, value) != null) {
          throw new IOException();
        }
      } else {
        throw new IOException("Unexpected input: " + line);
      }
    } while (true);
  }

  private static String ascii(InputStream is, int klen) throws IOException {
    byte [] buf = new byte [klen];
    ByteStreams.readFully(is, buf);
    String v = new String(buf, StandardCharsets.ISO_8859_1);
    assert v.getBytes(StandardCharsets.ISO_8859_1).length == klen;
    return v;
  }

  private static String readLine(InputStream is) throws IOException {
    ByteArrayList line = new ByteArrayList();
    int b;
    while ((b = is.read()) >= 0) {
      if (b == '\n') {
        return new String(line.buffer, 0, line.size(), StandardCharsets.ISO_8859_1);
      }
      line.add((byte) b);
    }
    throw new IOException();
  }

  private static void consumeEol(InputStream is) throws IOException {
    if (is.read() != '\n') {
      throw new IOException("Expected CR.");
    }
  }

  private static void headers(InputStream is, LinkedHashMap<String, String> headers, ByteArrayList lineBuffer) throws IOException {
    headers.clear();
    outer: while (true) {
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

      String headerLine = new String(lineBuffer.buffer, 0, lineBuffer.size(), StandardCharsets.ISO_8859_1);
      int sep = headerLine.indexOf(": ");
      if (sep < 0) {
        throw new IOException("No separator: " + headerLine);
      }

      String key = headerLine.substring(0, sep);
      String value = headerLine.substring(sep + 2);
      if (headers.containsKey(key)) {
        throw new IOException("Dup. key " + key + ":\n 1: " + value + "\n 2: " + headers.get(key));
      }
      headers.put(key, value);
    }
  }
  
}
