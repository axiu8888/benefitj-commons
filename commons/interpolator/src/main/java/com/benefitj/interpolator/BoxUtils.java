package com.benefitj.interpolator;

import java.util.ArrayList;
import java.util.List;

public class BoxUtils {

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param klass List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(Object array, Class<T> klass) {
    Class<?> arrayClass = array.getClass();
    if (!arrayClass.isArray()) {
      throw new IllegalStateException("不支持的数据类型: " + array);
    }
    ArrayType arrayType = obtainArrayType(arrayClass);
    ArrayType type = obtainType(klass);
    Object v;
    switch (arrayType) {
      case BYTE:
        v = arrayToList((byte[]) array, type);
        break;
      case SHORT:
        v = arrayToList((short[]) array, type);
        break;
      case INT:
        v = arrayToList((int[]) array, type);
        break;
      case LONG:
        v = arrayToList((long[]) array, type);
        break;
      case FLOAT:
        v = arrayToList((float[]) array, type);
        break;
      case DOUBLE:
        v = arrayToList((double[]) array, type);
        break;
      default:
        throw new IllegalArgumentException("不支持的数组类型");
    }
    return (List<T>) v;
  }

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(byte[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (byte v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }


  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(short[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (short v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  private static <T extends Number> List<T> arrayToList(int[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (int v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(long[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (long v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(float[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (float v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }

  /**
   * 数组转换成 List
   *
   * @param array 数组
   * @param type  List存储的类型
   * @param <T>   类型
   * @return 返回转换后的数组
   */
  public static <T extends Number> List<T> arrayToList(double[] array, ArrayType type) {
    List<Number> list = new ArrayList<>(array.length);
    for (double v : array) {
      putValue(v, type, list);
    }
    return (List<T>) list;
  }

  /**
   * List转换成数组
   *
   * @param list  List
   * @param klass 数组类型，仅支持基本数据类型，不支持包装类型
   * @return 返回转换后的数组
   */
  public static <T> T listToArray(List<? extends Number> list, Class<T> klass) {
    ArrayType type = obtainArrayType(klass);
    Object v;
    switch (type) {
      case BYTE:
        v = listToByteArray(list);
        break;
      case SHORT:
        v = listToShortArray(list);
        break;
      case INT:
        v = listToIntArray(list);
        break;
      case LONG:
        v = listToLongArray(list);
        break;
      case FLOAT:
        v = listToFloatArray(list);
        break;
      case DOUBLE:
      default:
        v = listToDoubleArray(list);
        break;
    }
    return (T) v;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static byte[] listToByteArray(List<? extends Number> list) {
    int index = 0;
    byte[] array = new byte[list.size()];
    for (Number v : list) {
      array[index++] = v.byteValue();
    }
    return array;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static short[] listToShortArray(List<? extends Number> list) {
    int index = 0;
    short[] array = new short[list.size()];
    for (Number v : list) {
      array[index++] = v.shortValue();
    }
    return array;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static int[] listToIntArray(List<? extends Number> list) {
    int index = 0;
    int[] array = new int[list.size()];
    for (Number v : list) {
      array[index++] = v.intValue();
    }
    return array;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static long[] listToLongArray(List<? extends Number> list) {
    int index = 0;
    long[] array = new long[list.size()];
    for (Number v : list) {
      array[index++] = v.longValue();
    }
    return array;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static float[] listToFloatArray(List<? extends Number> list) {
    int index = 0;
    float[] array = new float[list.size()];
    for (Number v : list) {
      array[index++] = v.floatValue();
    }
    return array;
  }

  /**
   * List转换成数组
   *
   * @param list List
   * @return 返回转换后的数组
   */
  public static double[] listToDoubleArray(List<? extends Number> list) {
    int index = 0;
    double[] array = new double[list.size()];
    for (Number v : list) {
      array[index++] = v.doubleValue();
    }
    return array;
  }

  /**
   * 获取数组类型
   *
   * @param klass 类型
   * @return 返回对应的类型
   */
  public static ArrayType obtainArrayType(Class<?> klass) throws IllegalArgumentException {
    ArrayType type = ofArrayType(klass);
    if (type == null) {
      throw new IllegalArgumentException("不支持的类型");
    }
    return type;
  }

  /**
   * 获取数组类型
   *
   * @param klass 类型
   * @return 返回对应的类型
   */
  public static ArrayType obtainType(Class<?> klass) throws IllegalArgumentException {
    if (klass == Byte.class) {
      return ArrayType.BYTE;
    } else if (klass == Short.class) {
      return ArrayType.SHORT;
    } else if (klass == Integer.class) {
      return ArrayType.INT;
    } else if (klass == Long.class) {
      return ArrayType.LONG;
    } else if (klass == Float.class) {
      return ArrayType.FLOAT;
    } else if (klass == Double.class) {
      return ArrayType.DOUBLE;
    } else {
      throw new IllegalArgumentException("不支持的类型");
    }
  }

  /**
   * 获取数组类型
   *
   * @param klass 类型
   * @return 返回对应的类型
   */
  public static ArrayType ofArrayType(Class<?> klass) {
    if (klass == byte[].class) {
      return ArrayType.BYTE;
    } else if (klass == short[].class) {
      return ArrayType.SHORT;
    } else if (klass == int[].class) {
      return ArrayType.INT;
    } else if (klass == long[].class) {
      return ArrayType.LONG;
    } else if (klass == float[].class) {
      return ArrayType.FLOAT;
    } else if (klass == double[].class) {
      return ArrayType.DOUBLE;
    }
    return null;
  }

  private static void putValue(Number v, ArrayType type, List<Number> list) {
    switch (type) {
      case BYTE:
        list.add(ofByteValue(v));
        break;
      case SHORT:
        list.add(ofShortValue(v));
        break;
      case INT:
        list.add(ofIntegerValue(v));
        break;
      case LONG:
        list.add(ofLongValue(v));
        break;
      case FLOAT:
        list.add(ofFloatValue(v));
        break;
      case DOUBLE:
      default:
        list.add(ofDoubleValue(v));
        break;
    }
  }

  public static Byte ofByteValue(Number v) {
    return v instanceof Byte ? (Byte) v : v.byteValue();
  }


  public static Short ofShortValue(Number v) {
    return v instanceof Short ? (Short) v : v.shortValue();
  }

  public static Integer ofIntegerValue(Number v) {
    return v instanceof Integer ? (Integer) v : v.intValue();
  }


  public static Long ofLongValue(Number v) {
    return v instanceof Long ? (Long) v : v.longValue();
  }

  public static Float ofFloatValue(Number v) {
    return v instanceof Float ? (Float) v : v.floatValue();
  }

  public static Double ofDoubleValue(Number v) {
    return v instanceof Double ? (Double) v : v.doubleValue();
  }

  public enum ArrayType {

    /**
     * byte array
     */
    BYTE,
    /**
     * short array
     */
    SHORT,
    /**
     * int array
     */
    INT,
    /**
     * long array
     */
    LONG,
    /**
     * float array
     */
    FLOAT,
    /**
     * double array
     */
    DOUBLE;

  }

}
