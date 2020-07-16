package com.benefitj.influxdb.converter;

import org.influxdb.dto.Point;
import com.benefitj.influxdb.InfluxPointUtils;

/**
 * Point Converter Factory
 */
public class PointConverterFactory extends AbstractConverterFactory<Point> {

  public static final PointConverterFactory INSTANCE = new PointConverterFactory();

  public PointConverterFactory() {
  }

  @Override
  public <T> PointConverter<T> getConverter(Class<T> type) {
    return InfluxPointUtils.getPointConverter(type);
  }

}
