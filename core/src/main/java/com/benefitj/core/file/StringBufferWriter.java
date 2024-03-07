package com.benefitj.core.file;

import java.io.Writer;

public class StringBufferWriter extends Writer {

  final StringBuffer buf = new StringBuffer();

  public StringBuffer getBuf() {
    return buf;
  }

  @Override
  public void write(char[] cbuf, int off, int len) {
    buf.append(cbuf, off, len);
  }

  @Override
  public void flush() {
    // ignore
  }

  @Override
  public void close() {
    // ignore
  }

  @Override
  public String toString() {
    return buf.toString();
  }
}
