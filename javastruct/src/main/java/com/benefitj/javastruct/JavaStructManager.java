package com.benefitj.javastruct;


import com.benefitj.core.ReflectUtils;
import com.benefitj.javastruct.convert.FieldConverter;
import com.benefitj.javastruct.convert.DefaultPrimitiveFieldConverter;
import com.benefitj.javastruct.field.*;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class JavaStructManager {

  /**
   * 字段转换器
   */
  private final Map<Class<?>, FieldConverter> fieldConverters = new ConcurrentHashMap<>();
  /**
   * 缓存的类
   */
  private final Map<Class, StructClass> structClasses = new WeakHashMap<>();

  public void init() {
    this.fieldConverters.put(DefaultPrimitiveFieldConverter.class, new DefaultPrimitiveFieldConverter());
  }

  public Map<Class, StructClass> getStructClasses() {
    return structClasses;
  }

  public Map<Class<?>, FieldConverter> getFieldConverters() {
    return fieldConverters;
  }

  public FieldConverter getFieldConverter(Class<?> converterType) {
    return getFieldConverters().get(converterType);
  }

  public byte[] convert(Object o) {
    StructClass structClass = this.structClasses.computeIfAbsent(o.getClass(), this::parseStructClass);
    return structClass.convert(o);
  }

  protected StructClass parseStructClass(Class<?> type) {
    JavaStructClass jsc = type.getAnnotation(JavaStructClass.class);
    if (jsc == null) {
      throw new IllegalStateException("不支持的结构类[" + type + "]，请使用@ClassStruct注释！");
    }
    StructClass sc = new StructClass(type);
    ReflectUtils.foreachField(type
        , f -> f.isAnnotationPresent(JavaStructField.class)
        , f -> {
          JavaStructField jsf = f.getAnnotation(JavaStructField.class);
          FieldConverter fc = getFieldConverter(jsf.converter());
          if (fc == null) {
            throw new IllegalStateException("无法发现转换器");
          }
          StructField cf = new StructField(f);
          cf.setFieldType(PrimitiveFieldType.getFieldType(f.getType()));
          cf.setStructField(jsf);
          cf.setConverter(fc);
          sc.getFields().add(cf);
        }, f -> false);
    // 结构体大小
    sc.setSize(Math.max(jsc.value(), sc.getFields().stream()
        .mapToInt(StructField::size)
        .sum()));
    return sc;
  }


}
