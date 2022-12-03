package com.benefitj.jdbc.sql;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 表字段
 */
public class TableField {

  @JSONField(name = "Key")
  @JsonProperty("Key")
  private String key;

  @JSONField(name = "Field")
  @JsonProperty("Field")
  private String field;

  @JSONField(name = "Type")
  @JsonProperty("Type")
  private String type;

  @JSONField(name = "Extra")
  @JsonProperty("Extra")
  private String extra;

  @JSONField(name = "Default")
  @JsonProperty("Default")
  private String defaultValue;

  @JSONField(name = "Null")
  @JsonProperty("Null")
  private String nullable;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getExtra() {
    return extra;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getNullable() {
    return nullable;
  }

  public void setNullable(String nullable) {
    this.nullable = nullable;
  }
}
