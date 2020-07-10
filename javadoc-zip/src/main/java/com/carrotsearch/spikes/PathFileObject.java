package com.carrotsearch.spikes;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PathFileObject implements FileObject {
  private Path path;

  public PathFileObject(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  @Override
  public URI toUri() {
    return path.toUri();
  }

  @Override
  public String getName() {
    return path.toString();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    throw new IOException("Not implemented");
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    Files.createDirectories(path.getParent());
    return Files.newOutputStream(path);
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    throw new IOException("Not implemented");
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    throw new IOException("Not implemented");
  }

  @Override
  public Writer openWriter() throws IOException {
    throw new IOException("Not implemented");
  }

  @Override
  public long getLastModified() {
    throw new UncheckedIOException(new IOException("Not implemented."));
  }

  @Override
  public boolean delete() {
    return false;
  }
}
