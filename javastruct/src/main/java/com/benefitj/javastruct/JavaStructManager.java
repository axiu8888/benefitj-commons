package com.benefitj.javastruct;


import com.benefitj.core.ReflectUtils;
import com.benefitj.javastruct.annotaion.JavaStructClass;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructClass;
import com.benefitj.javastruct.field.StructField;
import com.benefitj.javastruct.resovler.DateTimeFieldResolver;
import com.benefitj.javastruct.resovler.DefaultPrimitiveFieldResolver;
import com.benefitj.javastruct.resovler.FieldResolver;
import com.benefitj.javastruct.resovler.HexStringResolver;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 结构体管理
 */
public class JavaStructManager {

  private static final JavaStructManager INSTANCE = new JavaStructManager();

  static {
    INSTANCE.setup();
    INSTANCE.setCharset(StandardCharsets.UTF_8.name());
  }

  public static JavaStructManager getInstance() {
    return JavaStructManager.INSTANCE;
  }

  /**
   * 字段解析器
   */
  private final Map<Class<?>, FieldResolver<?>> fieldResolvers = Collections.synchronizedMap(new LinkedHashMap<>());
  /**
   * 缓存的类
   */
  private final Map<Class, StructClass> structClasses = new WeakHashMap<>();
  /**
   * 字符串编码
   */
  private String charset = Charset.defaultCharset().name();

  public JavaStructManager() {
  }

  /**
   * 初始化
   */
  public void setup() {
    // 初始化解析器
    this.fieldResolvers.put(DefaultPrimitiveFieldResolver.class, new DefaultPrimitiveFieldResolver());
    this.fieldResolvers.put(DateTimeFieldResolver.class, new DateTimeFieldResolver());
    this.fieldResolvers.put(HexStringResolver.class, new HexStringResolver());
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
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
   * 字段解析器
   */
  public Map<Class<?>, FieldResolver<?>> getFieldResolvers() {
    return fieldResolvers;
  }

  /**
   * 获取字段解析器
   *
   * @param resolverType 解析器类型
   * @return 返回对应的解析器
   */
  public FieldResolver getFieldResolver(Class<?> resolverType) {
    return getFieldResolvers().get(resolverType);
  }

  /**
   * 转换对象
   *
   * @param o 结构体
   * @return 返回转换的字节数组
   */
  public byte[] convert(Object o) {
    return getStructClass(o.getClass(), true).convert(o);
  }

  /**
   * 解析结构体数据
   *
   * @param type 类型
   * @param data 数据
   * @param <T>  对象类型
   * @return 返回解析的对象
   */
  public <T> T parse(Class<T> type, byte[] data) {
    return parse(type, data, 0);
  }

  /**
   * 解析结构体数据
   *
   * @param type  类型
   * @param data  数据
   * @param start 开始的位置
   * @param <T>   对象类型
   * @return 返回解析的对象
   */
  public <T> T parse(Class<T> type, byte[] data, int start) {
    return getStructClass(type, true).parse(data, start);
  }

  /**
   * 解析结构体
   *
   * @param type 类型
   * @return 返回解析的结构体信息
   */
  protected StructClass parseStructClass(Class<?> type) {
    JavaStructClass jsc = type.getAnnotation(JavaStructClass.class);
    if (jsc == null) {
      throw new IllegalStateException("不支持的结构类[" + type + "]，请使用@ClassStruct注释！");
    }
    StructClass structClass = new StructClass(type);
    ReflectUtils.foreachField(type
        , f -> f.isAnnotationPresent(JavaStructField.class)
        , f -> structClass.getFields().add(createStructField(f)), f -> false);
    // 结构体大小
    structClass.setSize(Math.max(jsc.value(), structClass.getFields().stream()
        .mapToInt(StructField::size)
        .sum()));
    return structClass;
  }

  /**
   * 创建字段结构
   *
   * @param f 字段
   * @return 字段结构
   */
  protected StructField createStructField(Field f) {
    PrimitiveType primitiveType = PrimitiveType.getFieldType(f.getType());
    JavaStructField jsf = f.getAnnotation(JavaStructField.class);

    if (primitiveType == PrimitiveType.STRING
        && jsf.size() <= 0) {
      throw new IllegalStateException(String.format(
          "请指定[%s.%s]的长度", f.getDeclaringClass().getName(), f.getName()));
    }

    FieldResolver<?> fr = findFieldResolver(f, jsf, primitiveType);
    StructField structField = new StructField(f);
    structField.setPrimitiveType(primitiveType);
    structField.setStructField(jsf);
    structField.setResolver(fr);
    String charsetName = jsf.charset().trim();
    structField.setCharset(charsetName.isEmpty() ? getCharset() : charsetName);
    return structField;
  }

  /**
   * 查找字段解析器
   *
   * @param f   字段
   * @param jsf 结构注解
   * @param pt  基本数据类型
   * @return 返回解析器
   */
  protected FieldResolver<?> findFieldResolver(Field f, JavaStructField jsf, PrimitiveType pt) {
    FieldResolver<?> fr = null;
    if (jsf.resolver() != FieldResolver.class) {
      fr = getFieldResolver(jsf.resolver());
    } else {
      for (Map.Entry<Class<?>, FieldResolver<?>> entry : getFieldResolvers().entrySet()) {
        FieldResolver<?> value = entry.getValue();
        if (value.support(f, jsf, pt)) {
          fr = value;
          break;
        }
      }
    }

    if (fr == null) {
      throw new IllegalStateException("无法发现解析器: " + jsf.resolver().getName());
    }

    if (!fr.support(f, jsf, pt)) {
      throw new IllegalArgumentException(String.format(
          "不支持的数据类型: %s.%s [%s]", f.getDeclaringClass().getName(), f.getName(), f.getType().getName()));
    }
    return fr;
  }

}
