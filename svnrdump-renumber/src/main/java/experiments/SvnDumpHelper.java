package experiments;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import com.carrotsearch.hppc.ByteArrayList;
import com.google.common.io.ByteStreams;

public class SvnDumpHelper {
  public static BiPredicate<Path, Changeset> onlyValid(BiPredicate<Path, Changeset> chained) {
    return (path, block) -> {
      LinkedHashMap<String, String> headers = block.headers;
      if (!headers.containsKey("Node-path") && 
          !headers.containsKey("Revision-number") && 
          !headers.containsKey("SVN-fs-dump-format-version") && 
          !headers.containsKey("UUID")) {
        // svnrdump produces broken dump nodes for a path copy from the incubator (tika). ignore any nodes that are invalid.
        LogManager.getLogger(SvnDumpHelper.class).debug("Odd record in %s: %s (skipping)", path, headers);
        return true;
      } else {
        return chained.test(path, block);
      }
    };
  }
  
  public static void forEach(Path path, BiPredicate<Path, Changeset> consumer) throws IOException {
    if (Files.isRegularFile(path)) {
      // assume the input is a ZIP file.
      try (FileSystem fs = FileSystems.newFileSystem(path, null)) {
        Iterator<Path> roots = fs.getRootDirectories().iterator();
        Path root = roots.next();
        if (roots.hasNext()) {
          throw new IOException("More than one root of a ZIP file?");
        }

        visitDumpRoot(root, consumer);
      }
    } else {
      // assume the input is a folder.
      visitDumpRoot(path, consumer);
    }
  }

  private static void visitDumpRoot(Path root, BiPredicate<Path, Changeset> consumer) throws IOException {
    List<Path> revDumps = Files.walk(root)
        .filter((p) -> Files.isRegularFile(p) && p.getFileName().toString().matches("r[0-9]+\\.rev"))
        .sorted((a, b) -> Long.compare(revNumOf(a), revNumOf(b))).collect(Collectors.toList());

    for (Path p : revDumps) {
      if (!parseDumpFile(p, consumer)) {
        break;
      }
    }
  }

  private static long revNumOf(Path path) {
    String p = path.getFileName().toString();
    if (!p.startsWith("r") || !p.endsWith(".rev")) {
      throw new RuntimeException("Invalid dump revision file name: " + p);
    }
    return Long.parseLong(p.substring(1, p.length() - /* ".rev".length() */ 4));
  }

  private static boolean parseDumpFile(Path path, BiPredicate<Path, Changeset> consumer) throws IOException {
    ByteArrayList lineBuffer = new ByteArrayList();
    try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
      // read headers
      do {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers(is, headers, lineBuffer);
        if (headers.isEmpty()) {
          if (is.read() != -1) {
            System.out.println(path + "\n" + new String(ByteStreams.toByteArray(is)));
            throw new IOException();
          }
          break;
        }

        byte[] body = Changeset.EMPTY;
        Map<String, String> props = Collections.emptyMap();
        if (headers.containsKey("Content-length")) {
          long plength = 0;
          if (headers.containsKey("Prop-content-length")) {
            plength = Long.parseLong(headers.get("Prop-content-length"));
            byte[] propSection = new byte[(int) plength];
            ByteStreams.readFully(is, propSection);
            props = parseProps(propSection);
          }

          long blength = Long.parseLong(headers.get("Content-length")) - plength;
          body = new byte[(int) blength];
          ByteStreams.readFully(is, body);
        }

        if (!consumer.test(path, new Changeset(headers, props, body))) {
          return false;
        }
      } while (true);
    }

    return true;
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

  static byte[] ascii(String key) {
    return key.getBytes(StandardCharsets.ISO_8859_1);
  }
}
