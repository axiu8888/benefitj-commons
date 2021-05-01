package com.benefitj.javastruct;

import java.util.*;

/**
 * 简单数据类型
 */
public enum PrimitiveType {

  /**
   * 布尔类型
   */
  BOOLEAN(1, boolean.class, Boolean.class),
  /**
   * 布尔类型数组
   */
  BOOLEAN_ARRAY(1, boolean[].class, Boolean[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof boolean[] ? ((boolean[]) array).length : ((Boolean[]) array).length;
    }
  },
  /**
   * 字节
   */
  BYTE(1, byte.class, Byte.class),
  /**
   * 字节数组
   */
  BYTE_ARRAY(1, byte[].class, Byte[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof byte[] ? ((byte[]) array).length : ((Byte[]) array).length;
    }
  },
  /**
   * 短整型
   */
  SHORT(2, short.class, Short.class),
  /**
   * 短整型数组
   */
  SHORT_ARRAY(2, short[].class, Short[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof short[] ? ((short[]) array).length : ((Short[]) array).length;
    }
  },
  /**
   * 整数
   */
  INTEGER(4, int.class, Integer.class),
  /**
   * 整数数组
   */
  INTEGER_ARRAY(4, int[].class, Integer[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof int[] ? ((int[]) array).length : ((Integer[]) array).length;
    }
  },
  /**
   * 长整型
   */
  LONG(8, long.class, Long.class),
  /**
   * 长整型数组
   */
  LONG_ARRAY(8, long[].class, Long[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof long[] ? ((long[]) array).length : ((Long[]) array).length;
    }
  },
  /**
   * 单精度浮点数
   */
  FLOAT(4, float.class, Float.class),
  /**
   * 单精度浮点数数组
   */
  FLOAT_ARRAY(4, float[].class, Float[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof float[] ? ((float[]) array).length : ((Float[]) array).length;
    }
  },
  /**
   * 双精度浮点数
   */
  DOUBLE(8, double.class, Double.class),
  /**
   * 双精度浮点数数组
   */
  DOUBLE_ARRAY(8, double[].class, Double[].class) {
    /**
     * 获取数组长度
     */
    @Override
    public int arrayLength(Object array) {
      return array instanceof double[] ? ((double[]) array).length : ((Double[]) array).length;
    }
  },
  /**
   * 字符串
   */
  STRING(0, String.class),
  ;

  private final int size;
  private final List<Class<?>> types;
  private final boolean isArray;

  PrimitiveType(int size, Class<?>... types) {
    this.size = size;
    this.types = Collections.unmodifiableList(Arrays.asList(types));

    boolean arrayFlag = false;
    for (Class<?> type : types) {
      arrayFlag |= type.isArray();
    }
    this.isArray = arrayFlag;
  }

  public int getSize() {
    return size;
  }

  public List<Class<?>> getTypes() {
    return types;
  }

  public boolean isArray() {
    return isArray;
  }

  public int arrayLength(Object array) {
    return 0;
  }

  private static final Map<Class, PrimitiveType> fieldTypes;

  static {
    Map<Class<?>, PrimitiveType> typeMap = new HashMap<>();
    for (PrimitiveType value : values()) {
      value.types.forEach(type -> typeMap.put(type, value));
    }
    fieldTypes = Collections.unmodifiableMap(typeMap);
  }

  /**
   * 获取字段类型
   *
   * @param type 类型
   * @return 返回枚举类型
   */
  public static PrimitiveType valueOf(Class<?> type) {
    return fieldTypes.get(type);
  }

}
