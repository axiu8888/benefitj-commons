package com.benefitj.influxdb.write;

import com.benefitj.influxdb.file.FileWriterPair;
import com.benefitj.influxdb.file.LineFileListener;
import com.benefitj.influxdb.template.InfluxDBTemplate;

import java.io.File;

/**
 * 上传到InfluxDB
 */
public class InfluxDBLineFileListener implements LineFileListener {

  private InfluxDBTemplate template;

  public InfluxDBLineFileListener(InfluxDBTemplate template) {
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

  public InfluxDBTemplate getTemplate() {
    return template;
  }

  public void setTemplate(InfluxDBTemplate template) {
    this.template = template;
  }
}
