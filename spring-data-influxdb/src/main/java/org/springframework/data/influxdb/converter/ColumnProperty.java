package org.springframework.data.influxdb.converter;

import java.lang.reflect.Field;

public class ColumnProperty {

  /**
   * field
   */
  private final Field field;
  /**
   * MEASUREMENT name
   */
  private String measurement;
  /**
   * 字段名
   */
  private String column;
  /**
   * 是否为tag
   */
  private boolean tag = false;
  /**
   * 是否允许为 null
   */
  private boolean tagNullable = false;

  public ColumnProperty(Field field) {
    this.field = field;
  }

  public Field getField() {
    return field;
  }

  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public boolean isTag() {
    return tag;
  }

  public void setTag(boolean tag) {
    this.tag = tag;
  }

  public boolean isTagNullable() {
    return tagNullable;
  }

  public void setTagNullable(boolean tagNullable) {
    this.tagNullable = tagNullable;
  }

}
