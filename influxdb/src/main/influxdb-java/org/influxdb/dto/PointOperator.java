package org.influxdb.dto;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PointOperator {

  private final Point point;


  public PointOperator(Point point) {
    this.point = point;
  }

  private Point getPoint() {
    return this.point;
  }

  /**
   * @param measurement the measurement to set
   */
  public void setMeasurement(final String measurement) {
    this.getPoint().setMeasurement(measurement);
  }

  /**
   * @param time the time to set
   */
  public void setTime(final Long time) {
    this.getPoint().setTime(time);
  }

  /**
   * @param tags the tags to set
   */
  public void setTags(final Map<String, String> tags) {
    this.getPoint().setTags(tags);
  }

  /**
   * @param tag   the tag key
   * @param value the tag value
   */
  public void setTag(String tag, String value) {
    this.getTags().put(tag, value);
  }

  /**
   * @return the tags
   */
  public Map<String, String> getTags() {
    return this.getPoint().getTags();
  }

  /**
   * @return the tags
   */
  public String getTagValue(String tag) {
    return this.getTags().get(tag);
  }

  /**
   * @param precision the precision to set
   */
  public void setPrecision(final TimeUnit precision) {
    this.getPoint().setPrecision(precision);
  }

  /**
   * @return the fields
   */
  public Map<String, Object> getFields() {
    return this.getPoint().getFields();
  }

  /**
   * @param fields the fields to set
   */
  public void setFields(final Map<String, Object> fields) {
    this.getPoint().setFields(fields);
  }

  public void setField(String field, Object value) {
    getFields().put(field, value);
  }

  public Object getField(String field) {
    return getFields().get(field);
  }

  public String lineProtocol() {
    return lineProtocol(null);
  }

  public String lineProtocol(final TimeUnit precision) {
    return getPoint().lineProtocol(precision);
  }

}
