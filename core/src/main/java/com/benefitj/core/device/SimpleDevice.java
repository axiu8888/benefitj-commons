package com.benefitj.core.device;

/**
 * 设备
 */
public class SimpleDevice extends AbstractDevice<String> {

  public SimpleDevice() {
  }

  public SimpleDevice(String id) {
    super();
    this.setId(id);
  }

  public SimpleDevice(String id, String name) {
    this.setId(id);
    this.setName(name);
  }

  public SimpleDevice(String id, String name, String type) {
    this.setId(id);
    this.setName(name);
    this.setType(type);
  }

}
