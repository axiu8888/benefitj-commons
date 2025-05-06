package com.benefitj.core.device;

/**
 * 设备
 */
public class SimpleDevice extends AbstractDevice<String> {

  public SimpleDevice() {
    setActive(true);// 默认是可用
  }

  public SimpleDevice(String id) {
    this();
    this.setId(id);
  }

  public SimpleDevice(String id, String name) {
    this(id);
    this.setName(name);
  }

  public SimpleDevice(String id, String name, String type) {
    this(id, name);
    this.setType(type);
  }

}
