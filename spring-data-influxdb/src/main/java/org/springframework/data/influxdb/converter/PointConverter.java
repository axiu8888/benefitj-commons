package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxUtils;

/**
 * InfluxDB Point 转换器
 *
 * @param <T>
 */
public class PointConverter<T> extends AbstractConverter<T, Point> {

  public PointConverter(Class<T> type) {
    super(type);
  }

  @Override
  public Point convert(T item) {
    return InfluxUtils.toPoint(this, item);
  }

}
