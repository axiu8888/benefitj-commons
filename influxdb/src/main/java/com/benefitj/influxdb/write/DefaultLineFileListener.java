package com.benefitj.influxdb.write;

import com.benefitj.influxdb.file.FileWriterPair;
import com.benefitj.influxdb.file.LineFileListener;
import com.benefitj.influxdb.template.RxJavaInfluxDBTemplate;

import java.io.File;

public class DefaultLineFileListener implements LineFileListener {

  private RxJavaInfluxDBTemplate template;

  public DefaultLineFileListener(RxJavaInfluxDBTemplate template) {
    this.template = template;
  }

  @Override
  public void onHandleLineFile(FileWriterPair pair, File file) {
    try {
      if (file.length() > 0) {
        getTemplate().write(file);
      }
    } finally {
      file.delete();
    }
  }

  public RxJavaInfluxDBTemplate getTemplate() {
    return template;
  }

  public void setTemplate(RxJavaInfluxDBTemplate template) {
    this.template = template;
  }
}
