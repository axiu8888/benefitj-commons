package com.benefitj.jdbc.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SqlMetaData {

  @JsonProperty("readOnly")
  private boolean readOnly;
  @JsonProperty("writable")
  private boolean writable;
  @JsonProperty("precision")
  private int precision;
  @JsonProperty("signed")
  private boolean signed;
  @JsonProperty("caseSensitive")
  private boolean caseSensitive;
  @JsonProperty("columnLabel")
  private String columnLabel;
  @JsonProperty("columnType")
  private int columnType;
  @JsonProperty("nullable")
  private int nullable;
  @JsonProperty("autoIncrement")
  private boolean autoIncrement;
  @JsonProperty("tableName")
  private String tableName;
  @JsonProperty("scale")
  private int scale;
  @JsonProperty("columnName")
  private String columnName;
  @JsonProperty("catalogName")
  private String catalogName;
  @JsonProperty("schemaName")
  private String schemaName;
  @JsonProperty("currency")
  private boolean currency;
  @JsonProperty("searchable")
  private boolean searchable;
  @JsonProperty("definitelyWritable")
  private boolean definitelyWritable;
  @JsonProperty("columnTypeName")
  private String columnTypeName;
  @JsonProperty("columnDisplaySize")
  private int columnDisplaySize;
  @JsonProperty("columnClassName")
  private String columnClassName;

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public boolean isWritable() {
    return writable;
  }

  public void setWritable(boolean writable) {
    this.writable = writable;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  public boolean isSigned() {
    return signed;
  }

  public void setSigned(boolean signed) {
    this.signed = signed;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public void setColumnLabel(String columnLabel) {
    this.columnLabel = columnLabel;
  }

  public int getColumnType() {
    return columnType;
  }

  public void setColumnType(int columnType) {
    this.columnType = columnType;
  }

  public int getNullable() {
    return nullable;
  }

  public void setNullable(int nullable) {
    this.nullable = nullable;
  }

  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getCatalogName() {
    return catalogName;
  }

  public void setCatalogName(String catalogName) {
    this.catalogName = catalogName;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public boolean isCurrency() {
    return currency;
  }

  public void setCurrency(boolean currency) {
    this.currency = currency;
  }

  public boolean isSearchable() {
    return searchable;
  }

  public void setSearchable(boolean searchable) {
    this.searchable = searchable;
  }

  public boolean isDefinitelyWritable() {
    return definitelyWritable;
  }

  public void setDefinitelyWritable(boolean definitelyWritable) {
    this.definitelyWritable = definitelyWritable;
  }

  public String getColumnTypeName() {
    return columnTypeName;
  }

  public void setColumnTypeName(String columnTypeName) {
    this.columnTypeName = columnTypeName;
  }

  public int getColumnDisplaySize() {
    return columnDisplaySize;
  }

  public void setColumnDisplaySize(int columnDisplaySize) {
    this.columnDisplaySize = columnDisplaySize;
  }

  public String getColumnClassName() {
    return columnClassName;
  }

  public void setColumnClassName(String columnClassName) {
    this.columnClassName = columnClassName;
  }
}
