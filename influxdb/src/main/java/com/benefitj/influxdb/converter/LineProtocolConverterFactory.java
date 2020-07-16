package com.benefitj.influxdb.converter;

import com.benefitj.influxdb.InfluxPointUtils;

/**
 * LineProtocol Converter Factory
 */
public class LineProtocolConverterFactory extends AbstractConverterFactory<String> {

  public static final LineProtocolConverterFactory INSTANCE = new LineProtocolConverterFactory();

  public LineProtocolConverterFactory() {
  }

  @Override
  public <T> LineProtocolConverter<T> getConverter(Class<T> type) {
    return InfluxPointUtils.getLineProtocolConverter(type);
  }

}
