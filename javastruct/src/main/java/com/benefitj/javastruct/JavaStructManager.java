package com.benefitj.javastruct;


import com.benefitj.core.BinaryHelper;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.javastruct.convert.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 结构体管理
 */
public class JavaStructManager {

  static final SingletonSupplier<JavaStructManager> singleton = SingletonSupplier.of(JavaStructManager::new);

  public static JavaStructManager get() {
    return singleton.get();
  }

  /**
   * 字段解析器
   */
  private final Map<Class<?>, Converter<?>> converters = Collections.synchronizedMap(new LinkedHashMap<>());
  /**
   * 实例化器
   */
  private final Map<Class<?>, Instantiator> instantiators = Collections.synchronizedMap(new LinkedHashMap<>());
  /**
   * 缓存的类
   */
  private final Map<Class, StructClass> structClasses = new WeakHashMap<>();
  /**
   * 字符串编码
   */
  private String charset = "UTF-8";
  /**
   * 二进制转换工具
   */
  protected final BinaryHelper binary = BinaryHelper.BIG_ENDIAN;
  /**
   * 类结构解析对象
   */
  private StructResolver structResolver;

  public JavaStructManager() {
    this(true);
  }

  public JavaStructManager(boolean initSetup) {
    if (initSetup) {
      setup();
    }
  }

  /**
   * 初始化
   */
  public void setup() {
    // 初始化解析器
    this.addConverter(new DefaultPrimitiveConverter());
    this.addConverter(new DateTimeConverter());
    this.addConverter(new StringConverter());
    this.addConverter(new LongTimeConverter());
    this.addConverter(new LowerCaseHexStringConverter());
    this.addConverter(new UpperCaseHexStringConverter());

    // 默认的实例化器
    this.addInstantiator(Instantiator.class, new DefaultInstantiator());

    if (this.structResolver == null) {
      this.structResolver = new DefaultStructResolver();
    }
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public StructResolver getStructResolver() {
    return structResolver;
  }

  public void setStructResolver(StructResolver structResolver) {
    this.structResolver = structResolver;
  }

  public Map<Class, StructClass> getStructClasses() {
    return structClasses;
  }

  /**
   * 获取结构体信息
   *
   * @param type   对象类型
   * @param create 如果不存在是否创建
   * @return 返回结构体信息
   */
  public StructClass getStructClass(Class<?> type, boolean create) {
    if (create) {
      return this.getStructClasses().computeIfAbsent(type, this::parseStructClass);
    }
    return this.getStructClasses().get(type);
  }

  /**
   * 字段转换器
   */
  public Map<Class<?>, Converter<?>> getConverters() {
    return converters;
  }

  public Converter<?> addConverter(Converter converter) {
    return addConverter(converter.getClass(), converter);
  }

  public Converter<?> addConverter(Class<? extends Converter> type, Converter converter) {
    return getConverters().put(type, converter);
  }

  /**
   * 获取字段解析器
   *
   * @param resolverType 解析器类型
   * @return 返回对应的解析器
   */
  public Converter getFieldResolver(Class<?> resolverType) {
    return getConverters().get(resolverType);
  }

  /**
   * 获取全部的实例化器
   */
  public Map<Class<?>, Instantiator> getInstantiators() {
    return instantiators;
  }

  public Instantiator addInstantiator(Instantiator instantiator) {
    return addInstantiator(instantiator.getClass(), instantiator);
  }

  public Instantiator addInstantiator(Class<? extends Instantiator> type, Instantiator instantiator) {
    return getInstantiators().put(type, instantiator);
  }

  public Instantiator findInstantiator(Class<? extends Instantiator> instantiatorType) {
    return getInstantiators().get(instantiatorType);
  }

  public BinaryHelper getBinary() {
    return binary;
  }

  /**
   * 转换字节数组
   *
   * @param o 对象
   * @return 返回转换的字节数组
   */
  public byte[] toBytes(Object o) {
    return getStructClass(o.getClass(), true).toBytes(o);
  }

  /**
   * 解析结构体数据
   *
   * @param data 数据
   * @param type 类型
   * @param <T>  对象类型
   * @return 返回解析的对象
   */
  public <T> T parseObject(byte[] data, Class<T> type) {
    return parseObject(data, 0, type);
  }

  /**
   * 解析结构体数据
   *
   * @param type  类型
   * @param start 开始的位置
   * @param data  数据
   * @param <T>   对象类型
   * @return 返回解析的对象
   */
  public <T> T parseObject(byte[] data, int start, Class<T> type) {
    return getStructClass(type, true).parseObject(data, start);
  }

  /**
   * 解析结构体
   *
   * @param type 类型
   * @return 返回解析的结构体信息
   */
  protected StructClass parseStructClass(Class<?> type) {
    return getStructResolver().resolve(this, type);
  }

}
