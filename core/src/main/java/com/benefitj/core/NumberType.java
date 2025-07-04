package com.benefitj.core;


import java.nio.ByteOrder;

/**
 * 数值类型
 */
public enum NumberType {

  BYTE(1, byte.class, Byte.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toByte(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      buf[start] = v.byteValue();
      return buf;
    }
  },
  SHORT(2, short.class, Short.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toShort(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      System.arraycopy(HexUtils.shortToBytes(v.shortValue(), order), 0, buf, start, 2);
      return buf;
    }
  },
  INTEGER(4, int.class, Integer.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toInt(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      System.arraycopy(HexUtils.intToBytes(v.intValue(), order), 0, buf, start, 4);
      return buf;
    }
  },
  LONG(8, long.class, Long.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toLong(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      System.arraycopy(HexUtils.longToBytes(v.longValue(), order), 0, buf, start, 8);
      return buf;
    }
  },
  FLOAT(4, float.class, Float.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toFloat(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      System.arraycopy(HexUtils.intToBytes(Float.floatToIntBits(v.floatValue()), order), 0, buf, start, 4);
      return buf;
    }
  },
  DOUBLE(8, double.class, Double.class) {
    @Override
    public <T extends Number> T cast(Number v) {
      if (v == null) return null;
      if (isCast(v)) return (T) v;
      return (T) ((Object) toDouble(v));
    }

    @Override
    public byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order) {
      System.arraycopy(HexUtils.longToBytes(Double.doubleToLongBits(v.doubleValue()), order), 0, buf, start, 8);
      return buf;
    }
  },
  ;

  static final ArrayCopy<byte[]> COPY = ArrayCopy.newByteArrayCopy();

  final int byteCount;
  final Class<? extends Number>[] classes;

  NumberType(int byteCount, Class<? extends Number>... classes) {
    this.byteCount = byteCount;
    this.classes = classes;
  }

  public int getByteCount() {
    return byteCount;
  }

  public Class<? extends Number>[] getClasses() {
    return classes;
  }

  public boolean isCast(Number v) {
    return v != null && isCast(v.getClass());
  }

  public boolean isCast(Class<? extends Number> cls) {
    for (Class<? extends Number> clazz : getClasses()) {
      if (clazz == cls) {
        return true;
      }
    }
    return false;
  }

  /**
   * 转换为当前类型的值
   */
  public abstract <T extends Number> T cast(Number v);

  /**
   * 转换为字节数组
   *
   * @param v     值
   * @param order 字节顺序
   * @return 返回转换后的数组
   */
  public byte[] toBytes(Number v, ByteOrder order) {
    return toBytes(v, COPY.getCache(getByteCount()), 0, order);
  }

  /**
   * 转换为字节数组
   *
   * @param v     值
   * @param buf   缓冲区
   * @param start 开始的位置
   * @param order 字节顺序
   * @return 返回转换后的数组
   */
  public abstract byte[] toBytes(Number v, byte[] buf, int start, ByteOrder order);

  public static byte toByte(Number v) {
    return toByte(v, (byte) 0);
  }

  public static byte toByte(Number v, byte defaultValue) {
    if (BYTE.isCast(v)) return (byte) v;
    return (v == null) ? defaultValue : v.byteValue();
  }

  public static short toShort(Number v) {
    return toShort(v, (short) 0);
  }

  public static short toShort(Number v, short defaultValue) {
    if (SHORT.isCast(v)) return (short) v;
    return (v == null) ? defaultValue : v.shortValue();
  }

  public static int toInt(Number v) {
    return toInt(v, 0);
  }

  public static int toInt(Number v, int defaultValue) {
    if (INTEGER.isCast(v)) return (int) v;
    return (v == null) ? defaultValue : v.intValue();
  }

  public static long toLong(Number v) {
    return toLong(v, 0);
  }

  public static long toLong(Number v, long defaultValue) {
    if (LONG.isCast(v)) return (long) v;
    return (v == null) ? defaultValue : v.longValue();
  }

  public static float toFloat(Number v) {
    return toFloat(v, 0.0f);
  }

  public static float toFloat(Number v, float defaultValue) {
    if (FLOAT.isCast(v)) return (float) v;
    return (v == null) ? defaultValue : v.floatValue();
  }

  public static double toDouble(Number v) {
    return toDouble(v, 0.0);
  }

  public static double toDouble(Number v, double defaultValue) {
    if (DOUBLE.isCast(v)) return (double) v;
    return (v == null) ? defaultValue : v.doubleValue();
  }

  public static NumberType of(Number v) {
    for (NumberType type : values()) {
      if (type.isCast(v)) return type;
    }
    return null;
  }

  public static byte[] autoBytes(Number v, ByteOrder order) {
    NumberType type = of(v);
    return type != null ? type.toBytes(v, order) : null;
  }

}
