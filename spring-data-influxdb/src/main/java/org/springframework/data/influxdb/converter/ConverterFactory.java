package org.springframework.data.influxdb.converter;

import org.influxdb.dto.QueryResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Converter的管理类
 */
public interface ConverterFactory<U> {

  /**
   * 创建新的 PointConverter 对象
   *
   * @param type bean类型
   * @return 返回 PointConverter 对象
   */
  <T> Converter<T, U> getConverter(Class<T> type);

  /**
   * 将对象转换成Point
   *
   * @param t 对象
   * @return return Point object
   */
  @SuppressWarnings("unchecked")
  default <T> U convert(T t) {
    return ((Converter<T, U>) getConverter(t.getClass())).convert(t);
  }

  /**
   * 将对象转换成Point
   *
   * @param items item array
   * @return return Point list
   */
  default <T> List<U> convert(T[] items) {
    return convert(Arrays.asList(items));
  }

  /**
   * 将对象转换成Point
   *
   * @param items item array
   * @return return Point list
   */
  <T> List<U> convert(Collection<T> items);

  /**
   * 转换成bean对象
   *
   * @param result 查询的结果集
   * @param type   bean类型
   * @param <T>    泛型类型
   * @return 返回解析的对象
   */
  <T> List<T> mapperTo(QueryResult result, Class<T> type);

}
