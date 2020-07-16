package com.benefitj.influxdb.converter;

import com.benefitj.influxdb.InfluxPointUtils;

/**
 * 行协议转换器
 *
 * @param <T>
 */
public class LineProtocolConverter<T> extends AbstractConverter<T, String> {

  public LineProtocolConverter(Class<T> type) {
    super(type);
  }

  @Override
  public String convert(T item) {
    return InfluxPointUtils.toLineProtocol(this, item);
  }

}
