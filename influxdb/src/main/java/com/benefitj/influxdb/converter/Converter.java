package com.benefitj.influxdb.converter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Converter<T, U> {

  /**
   * 转换为行协议
   *
   * @param item
   * @return
   */
  U convert(T item);

  /**
   * 转换为行协议
   *
   * @param items
   * @return
   */
  default List<U> convert(T[] items) {
    return convert(Arrays.asList(items));
  }

  /**
   * 转换为行协议
   *
   * @param items
   * @return
   */
  default List<U> convert(Collection<T> items) {
    return items.stream()
            .filter(Objects::nonNull)
            .flatMap((Function<T, Stream<U>>) t -> Stream.of(convert(t)))
            .collect(Collectors.toList());
  }

  /**
   * 类型
   */
  Class<T> getType();

  /**
   * MEASUREMENT
   */
  String getMeasurement();

  /**
   * 获取 TAG
   */
  Map<String, ColumnProperty> getTags();

  /**
   * 获取 column
   */
  Map<String, ColumnProperty> getColumns();

  /**
   * 时间戳
   */
  ColumnProperty getTimestamp();

  /**
   * 时间戳单位
   */
  TimeUnit getTimestampUnit();

  /**
   * 设置时间戳单位
   */
  void setTimestampUnit(TimeUnit unit);

}
