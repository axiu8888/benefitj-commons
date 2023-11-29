package com.benefitj.interpolator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface ArrayOperator<T> {

  /**
   * 获取值
   *
   * @param array 数组
   * @param index 下表
   */
  T get(Object array, int index);

  /**
   * 设置值
   *
   * @param array 数组
   * @param index 下表
   * @param value 值
   */
  void set(Object array, int index, T value);

  /**
   * 创建数组
   *
   * @param len 长度
   * @return 返回创建的数组
   */
  Object newArray(int len);

  /**
   * 加
   */
  T add(int index, T v1, T v2);

  /**
   * 减
   */
  T subtract(int index, T v1, T v2);

  /**
   * 乘
   */
  T multiply(int index, T v1, T v2);

  /**
   * 除
   */
  T divide(int index, T v1, T v2);


  Map<Class<?>, ArrayOperator> OPERATORS = Collections.unmodifiableMap(new HashMap(){{
    Stream.of(
        new ArrayOperatorImpl<byte[]>(){},
        new ArrayOperatorImpl<Byte[]>(){},
        new ArrayOperatorImpl<short[]>(){},
        new ArrayOperatorImpl<Short[]>(){},
        new ArrayOperatorImpl<int[]>(){},
        new ArrayOperatorImpl<Integer[]>(){},
        new ArrayOperatorImpl<long[]>(){},
        new ArrayOperatorImpl<Long[]>(){},
        new ArrayOperatorImpl<float[]>(){},
        new ArrayOperatorImpl<Float[]>(){},
        new ArrayOperatorImpl<double[]>(){},
        new ArrayOperatorImpl<Double[]>(){},
        new ArrayOperatorImpl<char[]>(){},
        new ArrayOperatorImpl<Character[]>(){},
        new ArrayOperatorImpl<boolean[]>(){},
        new ArrayOperatorImpl<Boolean[]>(){}
    ).forEach(op -> put(op.type, op));
  }});

  static <T> ArrayOperator<T> getOperator(Class<T> cls) {
    return OPERATORS.get(cls);
  }

}
