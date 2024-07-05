package com.benefitj.core;


import com.alibaba.fastjson2.JSON;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 基础数据类型
 */
public enum PrimitiveType {

  BYTE(byte.class, Byte.class) {
    @Override
    public Object castTo(Number v) {
      return v.shortValue();
    }

    @Override
    public Object castTo(String v) {
      return Byte.parseByte(v);
    }
  },
  SHORT(short.class, Short.class) {
    @Override
    public Object castTo(Number v) {
      return v.shortValue();
    }

    @Override
    public Object castTo(String v) {
      return Short.parseShort(v);
    }
  },
  INTEGER(int.class, Integer.class) {
    @Override
    public Object castTo(Number v) {
      return v.intValue();
    }

    @Override
    public Object castTo(String v) {
      return Integer.parseInt(v);
    }
  },
  LONG(long.class, Long.class) {
    @Override
    public Object castTo(Number v) {
      return v.longValue();
    }

    @Override
    public Object castTo(String v) {
      return Long.parseLong(v);
    }
  },
  FLOAT(float.class, Float.class) {
    @Override
    public Object castTo(Number v) {
      return v.floatValue();
    }

    @Override
    public Object castTo(String v) {
      return Float.parseFloat(v);
    }
  },
  DOUBLE(double.class, Double.class) {
    @Override
    public Object castTo(Number v) {
      return v.doubleValue();
    }

    @Override
    public Object castTo(String v) {
      return Double.parseDouble(v);
    }
  },
  BOOLEAN(boolean.class, Boolean.class) {
    @Override
    public Object castTo(Number v) {
      return v.byteValue() == 0;
    }

    @Override
    public Object castTo(String v) {
      return Boolean.parseBoolean(v);
    }
  },
  CHARACTER(char.class, Character.class) {
    @Override
    public Object castTo(Number v) {
      return (char) v.byteValue();
    }

    @Override
    public Object castTo(String v) {
      return v != null ? v.charAt(0) : null;
    }
  },
  STRING(String.class, String.class) {
    @Override
    public Object castTo(Number v) {
      return String.valueOf(v);
    }

    @Override
    public Object castTo(String v) {
      return v;
    }
  },
  ;

  /**
   * 基本数据类型，不包含 String
   */
  public static final List<PrimitiveType> PRIMITIVE_TYPES = Collections.unmodifiableList(Arrays.asList(
      PrimitiveType.BYTE,
      PrimitiveType.SHORT,
      PrimitiveType.INTEGER,
      PrimitiveType.LONG,
      PrimitiveType.FLOAT,
      PrimitiveType.DOUBLE,
      PrimitiveType.BOOLEAN,
      PrimitiveType.CHARACTER
  ));

  public final Class<?> primitive;
  public final Class<?> box;

  PrimitiveType(Class<?> primitive, Class<?> box) {
    this.primitive = primitive;
    this.box = box;
  }

  public Object castTo(Number v) {
    throw new UnsupportedOperationException("不支持...");
  }

  public Object castTo(String v) {
    throw new UnsupportedOperationException("不支持...");
  }

  public static byte toByte(Number v) {
    return ifNotNull(v).byteValue();
  }

  public static short toShort(Number v) {
    return ifNotNull(v).shortValue();
  }

  public static int toInt(Number v) {
    return ifNotNull(v).intValue();
  }

  public static long toLong(Number v) {
    return ifNotNull(v).longValue();
  }

  public static float toFloat(Number v) {
    return ifNotNull(v).floatValue();
  }

  public static double toDouble(Number v) {
    return ifNotNull(v).doubleValue();
  }

  public static <T> T castTo(Object value, Class<T> type) {
    if (value == null && (type.isPrimitive() || type == String.class))
      throw new IllegalStateException("被转换的类型是" + type + "，value不允许为null");
    for (PrimitiveType pt : values()) {
      if (pt.primitive == type || pt.box == type) {
        if (value instanceof Number) {
          return (T) pt.castTo((Number) value);
        }
        if (value instanceof String) {
          return (T) pt.castTo((String) value);
        }
        return JSON.parseObject(String.valueOf(value), type);
      }
    }
    return null;
  }

  private static <T> T ifNotNull(T v) {
    if (v == null) throw new NullPointerException("The value is null !");
    return v;
  }
}
