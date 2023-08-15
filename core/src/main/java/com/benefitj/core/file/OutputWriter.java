package com.benefitj.core.file;

import java.nio.charset.StandardCharsets;

public interface OutputWriter<T extends OutputWriter<T>> extends IWriter {

  T write(byte[] buf, int offset, int len, boolean flush);

  @Override
  default T write(String str) {
    return write(str.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  default T write(String... strings) {
    synchronized (this) {
      for (String string : strings) {
        write(string);
      }
    }
    return (T) this;
  }

  @Override
  default T writeAndFlush(String str) {
    byte[] buf = str.getBytes(StandardCharsets.UTF_8);
    return write(buf, 0, buf.length, true);
  }

  @Override
  default T writeAndFlush(String... strings) {
    synchronized (this) {
      for (int i = 0; i < strings.length; i++) {
        byte[] buf = strings[i].getBytes(StandardCharsets.UTF_8);
        write(buf, 0, buf.length, i == strings.length - 1);
      }
    }
    return (T) this;
  }

  @Override
  default T write(byte[] buf) {
    return write(buf, 0, buf.length, false);
  }

  @Override
  default T write(byte[]... array) {
    synchronized (this) {
      for (byte[] buf : array) {
        write(buf, 0, buf.length);
      }
    }
    return (T) this;
  }

  @Override
  default T write(byte[] buf, int offset, int len) {
    return write(buf, offset, len, false);
  }

  @Override
  default T writeAndFlush(byte[] buf) {
    return writeAndFlush(buf, 0, buf.length);
  }

  @Override
  default T writeAndFlush(byte[]... array) {
    synchronized (this) {
      for (int i = 0; i < array.length; i++) {
        byte[] buf = array[i];
        write(buf, 0, buf.length, i == array.length - 1);
      }
    }
    return null;
  }

  @Override
  default T writeAndFlush(byte[] buf, int offset, int len) {
    return write(buf, offset, len, true);
  }

  @Override
  void flush();

  @Override
  void close();
}
