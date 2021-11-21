package com.benefitj.core.file.slicer;

import com.benefitj.core.file.IWriter;
import com.benefitj.core.file.FileWriterImpl;

import java.io.File;

/**
 * 文件写入器
 */
public class SliceFileWriter extends FileWriterImpl implements IWriter {

  public SliceFileWriter(File file) {
    super(file);
  }

}
