package com.benefitj.core.file.slicer;

import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 文件写入器
 */
public class SliceFileWriter extends FileWriterImpl implements IWriter<FileWriterImpl> {

  public SliceFileWriter(File file) {
    this(file, false);
  }

  public SliceFileWriter(File file, Charset charset) {
    this(file, charset, false);
  }

  public SliceFileWriter(File file, Charset charset, boolean append) {
    super(file, charset, append);
  }

  public SliceFileWriter(File file, boolean append) {
    super(file, append);
  }

}
