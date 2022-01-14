package com.benefitj.core.lambda;

import com.benefitj.core.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;

@Slf4j
public class ReflectLambdaMeta implements LambdaMeta {

  private static final Field FIELD_CAPTURING_CLASS;

  static {
    Field fieldCapturingClass;
    try {
      Class<SerializedLambda> aClass = SerializedLambda.class;
      fieldCapturingClass = ReflectUtils.setAccessible(aClass.getDeclaredField("capturingClass"));
    } catch (Throwable e) {
      // 解决高版本 jdk 的问题 gitee: https://gitee.com/baomidou/mybatis-plus/issues/I4A7I5
      log.warn(e.getMessage());
      fieldCapturingClass = null;
    }
    FIELD_CAPTURING_CLASS = fieldCapturingClass;
  }

  private final SerializedLambda lambda;

  public ReflectLambdaMeta(SerializedLambda lambda) {
    this.lambda = lambda;
  }

  @Override
  public String getImplMethodName() {
    return lambda.getImplMethodName();
  }

  @Override
  public Class<?> getInstantiatedClass() {
    String instantiatedMethodType = lambda.getInstantiatedMethodType();
    String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(";")).replace("\\", ".");
    try {
      return Class.forName(instantiatedType);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  private ClassLoader getCapturingClassClassLoader() {
    // 如果反射失败，使用默认的 classloader
    if (FIELD_CAPTURING_CLASS == null) {
      return null;
    }
    try {
      return ((Class<?>) FIELD_CAPTURING_CLASS.get(lambda)).getClassLoader();
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

}
