package com.benefitj.javastruct;

import com.benefitj.core.ReflectUtils;
import com.benefitj.javastruct.convert.Converter;

import java.lang.reflect.Field;
import java.util.Map;

public class DefaultStructResolver implements StructResolver {
  /**
   * 解析
   *
   * @param manager 管理类
   * @param type    类型
   * @return 返回解析后的结构对象
   */
  @Override
  public StructClass resolve(JavaStructManager manager, Class<?> type) {
    JavaStructClass jsc = type.getAnnotation(JavaStructClass.class);
    if (jsc == null) {
      throw new IllegalStateException("不支持的结构类[" + type + "]，请使用@ClassStruct注释！");
    }

    Instantiator instantiator = manager.findInstantiator(jsc.instantiator());
    if (instantiator == null) {
      throw new IllegalStateException(String.format(
          "无法发现结构对象的实例器\"%s.[%s]\"", type, jsc.instantiator()));
    }

    StructClass structClass = new StructClass(type);
    structClass.setInstantiator(instantiator);
    ReflectUtils.foreachField(type
        , f -> f.isAnnotationPresent(JavaStructField.class)
        , f -> structClass.getFields().add(createStructField(manager, f))
        , f -> false
        , false
    );
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
  protected StructField createStructField(JavaStructManager manager, Field f) {
    PrimitiveType pt = PrimitiveType.valueOf(f.getType());
    JavaStructField jsf = f.getAnnotation(JavaStructField.class);

    if (jsf.size() <= 0) {
      throw new IllegalStateException(String.format(
          "请指定[%s.%s]的长度", f.getDeclaringClass().getName(), f.getName()));
    }
    if (pt != null && pt.isArray() && jsf.arrayLength() < 1) {
      throw new IllegalStateException(String.format(
          "请指定数组数组的长度: %s.%s", f.getDeclaringClass().getName(), f.getName()));
    }

    Converter<?> fc = findFieldConverter(manager, f, jsf, pt);
    StructField structField = new StructField(f);
    structField.setPrimitiveType(pt);
    structField.setAnnotation(jsf);
    structField.setConverter(fc);
    String charsetName = jsf.charset().trim();
    structField.setCharset(charsetName.isEmpty() ? manager.getCharset() : charsetName);
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
  protected Converter<?> findFieldConverter(JavaStructManager manager,
                                            Field f,
                                            JavaStructField jsf,
                                            PrimitiveType pt) {
    Converter<?> fc = null;
    if (jsf.converter() != Converter.class) {
      fc = manager.getFieldResolver(jsf.converter());
    } else {
      for (Map.Entry<Class<?>, Converter<?>> entry : manager.getConverters().entrySet()) {
        Converter<?> value = entry.getValue();
        if (value.support(f, jsf, pt)) {
          fc = value;
          break;
        }
      }
    }

    if (fc == null) {
      throw new IllegalStateException("无法发现转换器: " + jsf.converter().getName());
    }

    if (!fc.support(f, jsf, pt)) {
      throw new IllegalArgumentException(String.format(
          "不支持的数据类型: %s.%s [%s]", f.getDeclaringClass().getName(), f.getName(), f.getType().getName()));
    }
    return fc;
  }
}
