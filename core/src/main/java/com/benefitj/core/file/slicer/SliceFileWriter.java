package com.benefitj.core.file.slicer;

import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;

import java.io.File;

/**
 * 文件写入器
 */
public class SliceFileWriter extends FileWriterImpl implements IWriter {

  public SliceFileWriter(File file) {
    this(file, false);
  }

  public SliceFileWriter(File file, boolean append) {
    super(file, append);
  }

}
