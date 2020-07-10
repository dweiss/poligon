package com.carrotsearch.spikes;

import javax.tools.DocumentationTool;
import javax.tools.FileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JavadocZip {
  public static void main(String[] args) throws Exception {
    DocumentationTool jdoc = ToolProvider.getSystemDocumentationTool();

    boolean call;
    Path zipFile = Paths.get("javadoc.zip");
    try (FileSystem fs =
        FileSystems.newFileSystem(URI.create("jar:" + zipFile.toUri()), Map.of("create", "true"))) {

      Path root = fs.getRootDirectories().iterator().next();

      StandardJavaFileManager standardFileManager =
          jdoc.getStandardFileManager(null, Locale.ROOT, StandardCharsets.UTF_8);
      StandardJavaFileManager fileManager =
          new ForwardingStandardJavaFileManager(standardFileManager) {
            @Override
            public FileObject getFileForOutput(
                Location location, String packageName, String relativeName, FileObject sibling) {
              Path path = root;
              if (packageName != null && !packageName.isBlank()) {
                path = path.resolve(packageName);
              }
              path = path.resolve(relativeName);
              return new PathFileObject(path);
            }
          };
      String sourcePath = "O:\\repos\\lucene-master\\lucene\\core\\src\\java";
      fileManager.setLocation(StandardLocation.SOURCE_PATH, List.of(new File(sourcePath)));

      List<String> options =
          List.of("-html5", "-subpackages", "com.carrotsearch", "-sourcepath", "src/main/java");

      options =
          List.of(
              "-overview",
              "O:\\repos\\lucene-master\\lucene\\core\\src\\java\\overview.html",
              "-sourcepath",
              "O:\\repos\\lucene-master\\lucene\\core\\src\\java",
              "-subpackages",
              "org.apache.lucene",
              "-d",
              "d:\\_tmp\\javadoc",
              "-protected",
              "-encoding",
              "UTF-8",
              "-charset",
              "UTF-8",
              "-docencoding",
              "UTF-8",
              "-noindex",
              "-author",
              "-version",
              "-use",
              "-locale",
              "en_US",
              "--release",
              "11",
              "-quiet",
              "-tag",
              "lucene.experimental:a:WARNING: This API is experimental and might change in incompatible ways in the next release.",
              "-tag",
              "lucene.internal:a:NOTE: This API is for internal purposes only and might change in incompatible ways in the next release.",
              "-tag",
              "lucene.spi:t:SPI Name (case-insensitive: if the name is \'htmlStrip\', \'htmlstrip\' can be used when looking up the service).",
              "-Xdoclint:all,-missing");

      long start = System.currentTimeMillis();
      DocumentationTool.DocumentationTask task =
          jdoc.getTask(null, fileManager, null, null, options, null);
      call = task.call();
      long end = System.currentTimeMillis();

      System.out.println(String.format("Time: %.2f", (end - start) / 1000.0));
    }

    System.exit(call ? 0 : -1);
  }
}
