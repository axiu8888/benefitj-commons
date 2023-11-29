package com.benefitj.interpolator;

public enum PrimitiveOperator {

  Byte(byte.class),
  Int(int.class),
  Long(long.class),
  Float(float.class),
  Double(double.class),
  Char(char.class),
  ;

  final Class<?> type;

  PrimitiveOperator(Class<?> type) {
    this.type = type;
  }

  public byte add(byte v1, byte v2) {
    return (byte) (v1 + v2);
  }

  public short add(short v1, short v2) {
    return (short) (v1 + v2);
  }

  public int add(int v1, int v2) {
    return v1 + v2;
  }

  public long add(long v1, long v2) {
    return v1 + v2;
  }

  public float add(float v1, float v2) {
    return v1 + v2;
  }

  public double add(double v1, double v2) {
    return v1 + v2;
  }

  public Number add(Number v1, Number v2) {
    if (type == byte.class) {
      return add(v1.byteValue(), v2.byteValue());
    } else if (type == short.class) {
      return add(v1.shortValue(), v2.shortValue());
    } else if (type == int.class) {
      return add(v1.intValue(), v2.intValue());
    } else if (type == long.class) {
      return add(v1.longValue(), v2.longValue());
    } else if (type == float.class) {
      return add(v1.floatValue(), v2.floatValue());
    } else if (type == double.class) {
      return add(v1.doubleValue(), v2.doubleValue());
    } else if (type == char.class) {
      return add(v1.byteValue(), v2.byteValue());
    } else {
      return 0;
    }
  }

  public byte subtract(byte v1, byte v2) {
    return (byte) (v1 - v2);
  }

  public short subtract(short v1, short v2) {
    return (short) (v1 - v2);
  }

  public int subtract(int v1, int v2) {
    return v1 - v2;
  }

  public long subtract(long v1, long v2) {
    return v1 - v2;
  }

  public float subtract(float v1, float v2) {
    return v1 - v2;
  }

  public double subtract(double v1, double v2) {
    return v1 - v2;
  }

  public Number subtract(Number v1, Number v2) {
    if (type == byte.class) {
      return subtract(v1.byteValue(), v2.byteValue());
    } else if (type == short.class) {
      return subtract(v1.shortValue(), v2.shortValue());
    } else if (type == int.class) {
      return subtract(v1.intValue(), v2.intValue());
    } else if (type == long.class) {
      return subtract(v1.longValue(), v2.longValue());
    } else if (type == float.class) {
      return subtract(v1.floatValue(), v2.floatValue());
    } else if (type == double.class) {
      return subtract(v1.doubleValue(), v2.doubleValue());
    } else if (type == char.class) {
      return subtract(v1.byteValue(), v2.byteValue());
    } else {
      return 0;
    }
  }

  public byte multiply(byte v1, byte v2) {
    return (byte) (v1 * v2);
  }

  public short multiply(short v1, short v2) {
    return (short) (v1 * v2);
  }

  public int multiply(int v1, int v2) {
    return v1 * v2;
  }

  public long multiply(long v1, long v2) {
    return v1 * v2;
  }

  public float multiply(float v1, float v2) {
    return v1 * v2;
  }

  public double multiply(double v1, double v2) {
    return v1 * v2;
  }

  public Number multiply(Number v1, Number v2) {
    if (type == byte.class) {
      return multiply(v1.byteValue(), v2.byteValue());
    } else if (type == short.class) {
      return multiply(v1.shortValue(), v2.shortValue());
    } else if (type == int.class) {
      return multiply(v1.intValue(), v2.intValue());
    } else if (type == long.class) {
      return multiply(v1.longValue(), v2.longValue());
    } else if (type == float.class) {
      return multiply(v1.floatValue(), v2.floatValue());
    } else if (type == double.class) {
      return multiply(v1.doubleValue(), v2.doubleValue());
    } else if (type == char.class) {
      return multiply(v1.byteValue(), v2.byteValue());
    } else {
      return 0;
    }
  }

  public byte divide(byte v1, byte v2) {
    return (byte) (v1 / v2);
  }

  public short divide(short v1, short v2) {
    return (short) (v1 / v2);
  }

  public int divide(int v1, int v2) {
    return v1 / v2;
  }

  public long divide(long v1, long v2) {
    return v1 / v2;
  }

  public float divide(float v1, float v2) {
    return v1 / v2;
  }

  public double divide(double v1, double v2) {
    return v1 / v2;
  }

  public Number divide(Number v1, Number v2) {
    if (type == byte.class) {
      return divide(v1.byteValue(), v2.byteValue());
    } else if (type == short.class) {
      return divide(v1.shortValue(), v2.shortValue());
    } else if (type == int.class) {
      return divide(v1.intValue(), v2.intValue());
    } else if (type == long.class) {
      return divide(v1.longValue(), v2.longValue());
    } else if (type == float.class) {
      return divide(v1.floatValue(), v2.floatValue());
    } else if (type == double.class) {
      return divide(v1.doubleValue(), v2.doubleValue());
    } else if (type == char.class) {
      return divide(v1.byteValue(), v2.byteValue());
    } else {
      return 0;
    }
  }

  public static boolean support(Class<?> type) {
    for (PrimitiveOperator v : values()) {
      if (v.type == type) {
        return true;
      }
    }
    return false;
  }

  public static PrimitiveOperator of(Class<?> type) {
    for (PrimitiveOperator v : values()) {
      if (v.type == type) {
        return v;
      }
    }
    throw new IllegalArgumentException("不支持的类型: " + type);
  }

}
