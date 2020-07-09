package com.benefitj.influxdb.converter;

import com.benefitj.influxdb.InfluxUtils;

/**
 * LineProtocol Converter Factory
 */
public class LineProtocolConverterFactory extends AbstractConverterFactory<String> {

  public static final LineProtocolConverterFactory INSTANCE = new LineProtocolConverterFactory();

  public LineProtocolConverterFactory() {
  }

  @Override
  public <T> LineProtocolConverter<T> getConverter(Class<T> type) {
    return InfluxUtils.getLineProtocolConverter(type);
  }

}
