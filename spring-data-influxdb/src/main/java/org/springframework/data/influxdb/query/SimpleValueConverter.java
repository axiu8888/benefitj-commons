package org.springframework.data.influxdb.query;

import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 值转换器
 */
public class SimpleValueConverter implements ValueConverter {


  private QueryResult.Series series;
  /**
   * Measurement名称
   */
  private String name;
  /**
   * 标签
   */
  private Map<String, String> tags;
  /**
   * 存储映射Column位置的集合
   */
  private Map<String, Integer> columnMap = new HashMap<>();
  /**
   * 列
   */
  private List<String> columns;
  /**
   * 存储值的集合
   */
  private volatile List<List<Object>> values;
  /**
   * 当前值的索引位置
   */
  private int position = -1;

  public SimpleValueConverter() {
  }

  public void setupSeries(QueryResult.Series series) {
    this.series = series;
    this.name = series.getName();
    this.tags = series.getTags();
    this.columnMap.clear();
    this.columns = series.getColumns();
    this.values = series.getValues();
    for (int i = 0; i < columns.size(); i++) {
      columnMap.put(columns.get(i), i);
    }
  }

  public void iterator(BiConsumer<String, Object> consumer) {
    this.getColumnMap().forEach((column, index) -> consumer.accept(column, getValue(index)));
  }

  public void iterator(String column, BiConsumer<Integer, Object> consumer) {
    Integer index = getColumnMap().getOrDefault(column, -1);
    consumer.accept(index, getValue(index));
  }

  public QueryResult.Series getSeries() {
    return series;
  }

  public String getName() {
    return name;
  }

  /**
   * 标签Map
   */
  public Map<String, String> getTags() {
    return tags;
  }

  /**
   * 标签
   */
  public String getTag(String name) {
    return tags != null ? tags.get(name) : null;
  }

  public Map<String, Integer> getColumnMap() {
    return columnMap;
  }

  /**
   * 获取列
   */
  public List<String> getColumns() {
    return columns;
  }

  /**
   * 获取列名
   */
  public String getColumn(int index) {
    return columns.get(index);
  }

  public List<List<Object>> getValues() {
    return values;
  }

  public List<Object> getValue(int position) {
    return position >= 0 ? values.get(position) : null;
  }

  /**
   * 设置当前值的位置
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * 获取当前值的位置
   */
  public int getPosition() {
    return position;
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  public Object getValue(List<Object> value, String column, Object defaultValue) {
    Integer position = columnMap.get(column);
    if (position != null) {
      Object o = value.get(position);
      return o != null ? o : defaultValue;
    }
    return defaultValue;
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  @Override
  public Object getValue(String column, Object defaultValue) {
    if (getPosition() < 0) {
      throw new IllegalStateException("未设置当前值的位置");
    }
    return getValue(values.get(getPosition()), column, defaultValue);
  }


}
