package experiments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

class Changeset {
  static final byte [] EMPTY = new byte [0];

  LinkedHashMap<String, String> headers;
  Map<String, String> properties;
  byte[] body;

  Changeset(LinkedHashMap<String, String> headers, Map<String, String> props, byte[] body) {
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
        byte [] key = SvnDumpHelper.ascii(e.getKey());
        if (e.getValue() == null) {
          baos.write(SvnDumpHelper.ascii("D " + (key.length) + "\n"));
          baos.write(key);
          baos.write('\n');
        } else {
          baos.write(SvnDumpHelper.ascii("K " + (key.length) + "\n"));
          baos.write(key);
          baos.write('\n');
          byte [] value = SvnDumpHelper.ascii(e.getValue());
          baos.write(SvnDumpHelper.ascii("V " + (value.length) + "\n"));
          baos.write(value);
          baos.write('\n');
        }
      }
      baos.write(SvnDumpHelper.ascii("PROPS-END\n"));
      props = baos.toByteArray();
    }

    // headers
    for (Map.Entry<String, String> e : headers.entrySet()) {
      if (e.getKey().equals("Content-length") ||
          e.getKey().equals("Prop-content-length")) {
        continue;
      } else {
        os.write(SvnDumpHelper.ascii(e.getKey() + ": " + e.getValue()));
      }
      os.write('\n');
    }

    if (props.length > 0) {
      os.write(SvnDumpHelper.ascii("Prop-content-length: " + (props.length)));
      os.write('\n');
    }
    if (props.length + body.length > 0) {
      os.write(SvnDumpHelper.ascii("Content-length: " + (props.length + body.length)));
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
      os.write('\n');
    }

    os.write('\n');
  }
}