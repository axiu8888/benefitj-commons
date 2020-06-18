package org.springframework.data.influxdb.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 默认的转换器
 *
 * @param <T>
 * @param <U>
 */
public abstract class AbstractConverter<T, U> implements Converter<T, U> {

  private final Class<T> type;
  /**
   * measurement
   */
  private String measurement;
  /**
   * column
   */
  private final Map<String, ColumnProperty> columns = new ConcurrentHashMap<>();
  /**
   * TAG
   */
  private final Map<String, ColumnProperty> tags = new ConcurrentHashMap<>();
  /**
   * timestamp
   */
  private ColumnProperty timestamp;
  /**
   * 时间戳的单位
   */
  private TimeUnit timestampUnit = TimeUnit.MILLISECONDS;

  public AbstractConverter(Class<T> type) {
    this.type = type;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  @Override
  public Map<String, ColumnProperty> getColumns() {
    return columns;
  }

  public void putColumns(Map<String, ColumnProperty> columns) {
    this.columns.putAll(columns);
  }

  @Override
  public Map<String, ColumnProperty> getTags() {
    return tags;
  }

  public void putTags(Map<String, ColumnProperty> tags) {
    this.tags.putAll(tags);
  }

  @Override
  public ColumnProperty getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ColumnProperty timestamp) {
    // 时间戳为null
    this.timestamp = timestamp;
  }

  @Override
  public void setTimestampUnit(TimeUnit timestampUnit) {
    this.timestampUnit = timestampUnit;
  }

  @Override
  public TimeUnit getTimestampUnit() {
    return timestampUnit;
  }

  public ColumnProperty getColumn(String name) {
    return getColumns().get(name);
  }

  public ColumnProperty getTag(String name) {
    return getTags().get(name);
  }

  public void putColumn(ColumnProperty property) {
    putColumn(property.getColumn(), property);
  }

  public void putColumn(String name, ColumnProperty property) {
    this.columns.put(name, property);
  }

  public void putTag(ColumnProperty property) {
    putTag(property.getColumn(), property);
  }

  public void putTag(String name, ColumnProperty property) {
    this.tags.put(name, property);
  }

}
