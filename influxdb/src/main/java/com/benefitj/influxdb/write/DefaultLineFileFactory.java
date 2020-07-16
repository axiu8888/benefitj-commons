package com.benefitj.influxdb.write;


import com.benefitj.influxdb.file.FileWriterPair;
import com.benefitj.influxdb.file.LineFileFactory;

import java.io.File;

/**
 * 默认的Line文件工厂
 */
public class DefaultLineFileFactory implements LineFileFactory {

  @Override
  public FileWriterPair create(File dir) {
    return LineFileFactory.newFile(dir);
  }

}

