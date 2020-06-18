package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxUtils;

/**
 * Point Converter Factory
 */
public class PointConverterFactory extends AbstractConverterFactory<Point> {

  public static final PointConverterFactory INSTANCE = new PointConverterFactory();

  public PointConverterFactory() {
  }

  @Override
  public <T> PointConverter<T> getConverter(Class<T> type) {
    return InfluxUtils.getPointConverter(type);
  }

}
