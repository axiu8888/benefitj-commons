package com.benefitj.javastruct;

import com.benefitj.core.ReflectUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 结构类信息
 */
public class StructClass {

  /**
   * 类型
   */
  private final Class<?> type;
  /**
   * 字段
   */
  private final List<StructField> fields = new LinkedList<>();
  /**
   * 实例化器
   */
  private Instantiator instantiator;
  /**
   * 结构体长度
   */
  private int size;

  public StructClass(Class<?> type) {
    this.type = type;
  }

  public Class<?> getType() {
    return type;
  }

  public List<StructField> getFields() {
    return fields;
  }

  public Instantiator getInstantiator() {
    return instantiator;
  }

  public void setInstantiator(Instantiator instantiator) {
    this.instantiator = instantiator;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  /**
   * 转换对象
   *
   * @param o 对象
   * @return 返回转换后的字节数组
   */
  public byte[] toBytes(Object o) {
    byte[] data = new byte[getSize()];
    int index = 0;
    for (StructField field : getFields()) {
      Object value = ReflectUtils.getFieldValue(field.getField(), o);
      byte[] bytes = field.getConverter().convert(field, value);
      System.arraycopy(bytes, 0, data, index, bytes.length);
      index += field.size();
    }
    return data;
  }

  /**
   * 解析结构体数据
   *
   * @param data  数据
   * @param start 开始的位置
   * @param <T>   对象类型
   * @return 返回解析的对象
   */
  public <T> T parseObject(byte[] data, int start) {
    /*if (data.length - start < getSize()) {
      throw new IllegalArgumentException(
          "数据长度不够，要求长度" + getSize() + "，实际长度" + (data.length - start));
    }*/

    // 创建对象
    Object o = getInstantiator().create(getType());
    int index = start, startAt;
    for (StructField sf : getFields()) {
      if ((sf.size() + index) > (data.length - start)) {
        // 多余数据不做处理
        break;
      }
      startAt = sf.getAnnotation().startAt();
      startAt = startAt > -1 ? start + startAt : index;
      Object value = sf.getConverter().parse(sf, data, startAt);
      if (value != null) {
        ReflectUtils.setFieldValue(sf.getField(), o, value);
      }
      index += sf.size();
    }
    return (T) o;
  }

}
