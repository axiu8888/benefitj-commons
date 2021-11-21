package com.benefitj.core.file.slicer;

import java.io.File;

/**
 * 监听
 */
public interface FileListener<T extends SliceFileWriter> {

  /**
   * 处理写入好的文件
   *
   * @param writer 文件写入器
   * @param file 文件
   */
  void onHandle(T writer, File file);

}
