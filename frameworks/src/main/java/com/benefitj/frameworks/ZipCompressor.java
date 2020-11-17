//package com.hsrg.frameworks;
//
//import java.io.*;
//import java.util.zip.CRC32;
//import java.util.zip.CheckedOutputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//public class ZipCompressor {
//
//  private static final int BUFFER = 1024 << 4;
//  /**
//   * 压缩文件
//   */
//  private final File zipFile;
//  /**
//   * 压缩文件的根目录
//   */
//  private String basedir = "";
//  /**
//   * 缓冲大小
//   */
//  private int bufferSize = BUFFER;
//
//  public ZipCompressor(String zipFile) {
//    this(new File(zipFile));
//  }
//
//  public ZipCompressor(File zipFile) {
//    this.zipFile = zipFile;
//  }
//
//  public void compress(File srcFile, String basedir) throws IOException {
//
//  }
//
//  public void compress(String... files) throws IOException {
//    final CRC32 crc32 = new CRC32();
//    try (final FileOutputStream fos = new FileOutputStream(getZipFile());
//         final ZipOutputStream out = new ZipOutputStream(new CheckedOutputStream(fos, crc32));) {
//      for (String filename : files) {
//        compress(new File(filename), out, getBasedir());
//      }
//    }
//  }
//
//  /**
//   * 压缩文件或目录
//   *
//   * @param srcFile 原文件
//   * @param out     输出
//   * @param destDir 目录
//   */
//  public void compress(File srcFile, ZipOutputStream out, String destDir) {
//    if (srcFile.isDirectory()) {
//      File[] files = srcFile.listFiles();
//      if (files != null) {
//        destDir = destDir.isEmpty() || (destDir.endsWith(File.separator) || destDir.endsWith("/"))
//            ? destDir : destDir + File.separator;
//        for (File subFile : files) {
//          compress(subFile, out, destDir + srcFile.getName() + File.separator);
//        }
//      }
//    } else {
//      try (final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));) {
//        ZipEntry entry = new ZipEntry(destDir + srcFile.getName());
//        out.putNextEntry(entry);
//        read(bis, getBufferSize(), true, (buff, len) -> out.write(buff, 0, len));
//      } catch (IOException e) {
//        throw new IllegalStateException(e);
//      }
//    }
//  }
//
//
//  public File getZipFile() {
//    return zipFile;
//  }
//
//  public String getBasedir() {
//    return basedir;
//  }
//
//  public void setBasedir(String basedir) {
//    this.basedir = basedir != null ? basedir : "";
//  }
//
//  public int getBufferSize() {
//    return bufferSize;
//  }
//
//  public void setBufferSize(int bufferSize) {
//    this.bufferSize = Math.max(bufferSize, 256);
//  }
//
//  private static void read(InputStream is,
//                           int size,
//                           boolean autoClose,
//                           ReadBiConsumer consumer) throws IOException {
//    try {
//      byte[] buff = new byte[size];
//      int len;
//      while ((len = is.read(buff)) != -1) {
//        consumer.accept(buff, len);
//      }
//    } finally {
//      if (autoClose) {
//        is.close();
//      }
//    }
//  }
//
//
//  interface ReadBiConsumer {
//    /**
//     * 接受数据
//     *
//     * @param buff 缓冲数据
//     * @param len  有效数据长度
//     */
//    void accept(byte[] buff, int len) throws IOException;
//
//  }
//
//}
