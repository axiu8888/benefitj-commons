package com.benefitj.jdbc.sql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TableStatus {

  @JsonProperty("Comment")
  private String comment;

  @JsonProperty("Data_free")
  private Integer dataFree;

  @JsonProperty("Create_options")
  private String createOptions;

  @JsonProperty("Check_time")
  private String checkTime;

  @JsonProperty("Collation")
  private String collation;

  @JsonProperty("Create_time")
  private String createTime;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("Avg_row_length")
  private Integer avgRowLength;

  @JsonProperty("Row_format")
  private String rowFormat;

  @JsonProperty("Temporary")
  private String temporary;

  @JsonProperty("Version")
  private Integer version;

  @JsonProperty("Checksum")
  private Integer checksum;

  @JsonProperty("Update_time")
  private String updateTime;

  @JsonProperty("Max_data_length")
  private Integer maxDataLength;

  @JsonProperty("Index_length")
  private Integer indexLength;

  @JsonProperty("Max_index_length")
  private Integer maxIndexLength;

  @JsonProperty("Auto_increment")
  private Integer autoIncrement;

  @JsonProperty("Engine")
  private String engine;

  @JsonProperty("Data_length")
  private Integer dataLength;

  @JsonProperty("Rows")
  private Integer rows;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Integer getDataFree() {
    return dataFree;
  }

  public void setDataFree(Integer dataFree) {
    this.dataFree = dataFree;
  }

  public String getCreateOptions() {
    return createOptions;
  }

  public void setCreateOptions(String createOptions) {
    this.createOptions = createOptions;
  }

  public String getCheckTime() {
    return checkTime;
  }

  public void setCheckTime(String checkTime) {
    this.checkTime = checkTime;
  }

  public String getCollation() {
    return collation;
  }

  public void setCollation(String collation) {
    this.collation = collation;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAvgRowLength() {
    return avgRowLength;
  }

  public void setAvgRowLength(Integer avgRowLength) {
    this.avgRowLength = avgRowLength;
  }

  public String getRowFormat() {
    return rowFormat;
  }

  public void setRowFormat(String rowFormat) {
    this.rowFormat = rowFormat;
  }

  public String getTemporary() {
    return temporary;
  }

  public void setTemporary(String temporary) {
    this.temporary = temporary;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Integer getChecksum() {
    return checksum;
  }

  public void setChecksum(Integer checksum) {
    this.checksum = checksum;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getMaxDataLength() {
    return maxDataLength;
  }

  public void setMaxDataLength(Integer maxDataLength) {
    this.maxDataLength = maxDataLength;
  }

  public Integer getIndexLength() {
    return indexLength;
  }

  public void setIndexLength(Integer indexLength) {
    this.indexLength = indexLength;
  }

  public Integer getMaxIndexLength() {
    return maxIndexLength;
  }

  public void setMaxIndexLength(Integer maxIndexLength) {
    this.maxIndexLength = maxIndexLength;
  }

  public Integer getAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(Integer autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Integer getDataLength() {
    return dataLength;
  }

  public void setDataLength(Integer dataLength) {
    this.dataLength = dataLength;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }
}
