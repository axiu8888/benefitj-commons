package com.benefitj.core.file.slicer;

import com.benefitj.core.IdUtils;
import com.benefitj.core.CatchUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件工厂
 */
public interface FileFactory<T extends SliceFileWriter> {

  /**
   * 创建新文件
   *
   * @param dir 目录
   * @return 返回创建的文件
   */
  T create(File dir);


  /**
   * 创建工厂对象
   */
  static FileFactory<SliceFileWriter> newFactory(String suffix) {
    return new FileFactoryImpl(suffix);
  }


  class FileFactoryImpl implements FileFactory<SliceFileWriter> {

    private String suffix;

    public FileFactoryImpl(String suffix) {
      this.suffix = suffix;
    }

    @Override
    public SliceFileWriter create(File dir) {
      File file = createFile(dir, IdUtils.uuid() + suffix);
      return new SliceFileWriter(file);
    }
  }

  static File createFile(File dir, String filename) {
    File file = new File(dir, filename);
    try {
      File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }
      file.createNewFile();
      return file;
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

}
