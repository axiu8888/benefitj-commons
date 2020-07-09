package com.benefitj.influxdb.converter;

import org.influxdb.dto.QueryResult;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Point Converter Factory
 */
public abstract class AbstractConverterFactory<U> implements ConverterFactory<U> {

  /**
   * PoJo的转换类
   */
  private final InfluxDBResultMapperPlus resultMapper = new InfluxDBResultMapperPlus();

  public AbstractConverterFactory() {
  }

  @Override
  public abstract <T> Converter<T, U> getConverter(Class<T> type);

  @Override
  public <T> List<U> convert(Collection<T> items) {
    return items.stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .collect(Collectors.toList());
  }

  public InfluxDBResultMapperPlus getResultMapper() {
    return resultMapper;
  }

  @Override
  public <T> List<T> mapperTo(QueryResult result, Class<T> type) {
    return getResultMapper().toPOJO(result, type);
  }

}
