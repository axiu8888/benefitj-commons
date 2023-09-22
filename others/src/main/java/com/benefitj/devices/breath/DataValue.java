package com.benefitj.devices.breath;

/**
 * 数据
 */
public class DataValue {

  private Type type;
  private String descriptor;
  private Object value;
  private String hex;

  public DataValue() {

  }

  public DataValue(Type type, String descriptor, Object value, String hex) {
    this.type = type;
    this.descriptor = descriptor;
    this.value = value;
    this.hex = hex;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getHex() {
    return hex;
  }

  public void setHex(String hex) {
    this.hex = hex;
  }

  @Override
  public String toString() {
    return String.format("%s(%s, %s, %s, %s)", type.getItem(), type.name(), value, type.getDescriptor(), hex);
  }

}
