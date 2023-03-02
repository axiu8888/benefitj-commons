package com.benefitj.core.lambda;

import com.benefitj.core.ReflectUtils;
import com.benefitj.core.functions.IFunction;

import java.io.*;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Lambda 工具
 */
public class LambdaUtils {

  /**
   * 获取Lambda信息
   *
   * @param func 函数
   * @param <T>  参数类型
   * @param <R>  返回值类型
   * @return 返回信息
   */
  public static <T, R> LambdaMeta getLambda(IFunction<T, R> func) {
    return getLambda((Serializable) func);
  }

  /**
   * 获取Lambda信息
   *
   * @param serializable 序列化对象
   * @return 返回信息
   */
  public static LambdaMeta getLambda(Serializable serializable) {
    try {
      // 1. IDEA 调试模式下 lambda 表达式是一个代理
      if (serializable instanceof Proxy) {
        return new ProxyLambdaMeta((Proxy) serializable);
      }
      Method method = serializable.getClass().getDeclaredMethod("writeReplace");
      return new ReflectLambdaMeta(ReflectUtils.invoke(serializable, method));
    } catch (Exception ignore) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
           ObjectOutputStream oos = new ObjectOutputStream(baos)) {
        oos.writeObject(serializable);
        oos.flush();
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
          @Override
          protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            return super.resolveClass(desc);
          }
        }) {
          return new ShadowLambdaMeta((SerializedLambda) ois.readObject());
        }
      } catch (IOException | ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    }
  }

}
