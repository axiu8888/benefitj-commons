package org.influxdb.dto;

import org.influxdb.impl.Preconditions;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Builder for a new Point.
 *
 * @author stefan.majer [at] gmail.com
 */
public class PointBuilder {
  private String measurement;
  private Long time;
  private TimeUnit precision;

  private final Map<String, String> tags = new TreeMap<>();
  private final Map<String, Object> fields = new TreeMap<>();

  public PointBuilder() {
  }

  /**
   * @param measurement
   */
  public PointBuilder(final String measurement) {
    this.measurement = measurement;
  }

  /**
   * Set a measurement to this point.
   *
   * @param measurement the measurement name
   * @return the Builder instance.
   */
  public PointBuilder measurement(final String measurement) {
    Objects.requireNonNull(measurement, "measurement");
    if (!measurement.isEmpty()) {
      this.measurement = measurement;
    }
    return this;
  }

  /**
   * Add a tag to this point.
   *
   * @param tagName the tag name
   * @param value   the tag value
   * @return the Builder instance.
   */
  public PointBuilder tag(final String tagName, final String value) {
    Objects.requireNonNull(tagName, "tagName");
    Objects.requireNonNull(value, "value");
    if (!tagName.isEmpty() && !value.isEmpty()) {
      tags.put(tagName, value);
    }
    return this;
  }

  /**
   * Add a Map of tags to add to this point.
   *
   * @param tagsToAdd the Map of tags to add
   * @return the Builder instance.
   */
  public PointBuilder tag(final Map<String, String> tagsToAdd) {
    for (Map.Entry<String, String> tag : tagsToAdd.entrySet()) {
      tag(tag.getKey(), tag.getValue());
    }
    return this;
  }

  /**
   * Add a field to this point.
   *
   * @param field the field name
   * @param value the value of this field
   * @return the Builder instance.
   */
  @SuppressWarnings("checkstyle:finalparameters")
  @Deprecated
  public PointBuilder field(final String field, Object value) {
    if (value instanceof Number) {
      if (value instanceof Byte) {
        value = ((Byte) value).doubleValue();
      } else if (value instanceof Short) {
        value = ((Short) value).doubleValue();
      } else if (value instanceof Integer) {
        value = ((Integer) value).doubleValue();
      } else if (value instanceof Long) {
        value = ((Long) value).doubleValue();
      } else if (value instanceof BigInteger) {
        value = ((BigInteger) value).doubleValue();
      }
    }
    fields.put(field, value);
    return this;
  }

  public PointBuilder addField(final String field, final boolean value) {
    fields.put(field, value);
    return this;
  }

  public PointBuilder addField(final String field, final long value) {
    fields.put(field, value);
    return this;
  }

  public PointBuilder addField(final String field, final double value) {
    fields.put(field, value);
    return this;
  }

  public PointBuilder addField(final String field, final Number value) {
    fields.put(field, value);
    return this;
  }

  public PointBuilder addField(final String field, final String value) {
    Objects.requireNonNull(value, "value");

    fields.put(field, value);
    return this;
  }

  /**
   * Add a Map of fields to this point.
   *
   * @param fieldsToAdd the fields to add
   * @return the Builder instance.
   */
  public PointBuilder fields(final Map<String, Object> fieldsToAdd) {
    this.fields.putAll(fieldsToAdd);
    return this;
  }

  /**
   * Add a time to this point.
   *
   * @param timeToSet      the time for this point
   * @param precisionToSet the TimeUnit
   * @return the Builder instance.
   */
  public PointBuilder time(final long timeToSet, final TimeUnit precisionToSet) {
    Objects.requireNonNull(precisionToSet, "precisionToSet");
    this.time = timeToSet;
    this.precision = precisionToSet;
    return this;
  }

  /**
   * Does this builder contain any fields?
   *
   * @return true, if the builder contains any fields, false otherwise.
   */
  public boolean hasFields() {
    return !fields.isEmpty();
  }

  /**
   * Create a new Point.
   *
   * @return the newly created Point.
   */
  public Point build() {
    Preconditions.checkNonEmptyString(this.measurement, "measurement");
    Preconditions.checkPositiveNumber(this.fields.size(), "fields size");
    Point point = new Point();
    point.setFields(this.fields);
    point.setMeasurement(this.measurement);
    if (this.time != null) {
      point.setTime(this.time);
      point.setPrecision(this.precision);
    }
    point.setTags(this.tags);
    return point;
  }

  /**
   * 清空数据
   */
  public void clear() {
    this.measurement = null;
    this.time = null;
    this.precision = null;
    this.tags.clear();
    this.fields.clear();
  }
}

