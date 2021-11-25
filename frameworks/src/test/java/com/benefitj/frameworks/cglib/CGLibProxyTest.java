package com.benefitj.frameworks.cglib;

import com.benefitj.core.ReflectUtils;
import com.benefitj.frameworks.cglib.CGLibProxy;
import com.benefitj.frameworks.cglib.EnhancerBuilder;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CGLibProxyTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateProxy() {
    Coder coderProxy = CGLibProxy.createProxy(Coder.class, (obj, method, args, proxy) -> {
      String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
      System.err.println(
          "\n.............. [" + key + "] .............."
              + "\n" + obj.getClass().getSimpleName() + "." + obj.hashCode()
              + "\n" + proxy.getClass().getSimpleName() + "." + proxy.hashCode()
              + "\n.............. [" + key + "] ..............\n"
      );
      return proxy.invokeSuper(obj, args);
    });
    coderProxy.say();
  }

  @Test
  public void testCreateProxy2() {
    Coder source = new Coder();
    Coder coderProxy = CGLibProxy.createProxy(Coder.class, (obj, method, args, proxy) -> {
      String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
      System.err.println(
          "\n.............. [" + key + "] .............."
              + "\n" + obj.getClass().getSimpleName() + "." + obj.hashCode()
              + "\n" + proxy.getClass().getSimpleName() + "." + proxy.hashCode()
              + "\n.............. [" + key + "] ..............\n"
      );
      return method.invoke(source, args);
    });
    coderProxy.say();
  }

  @Test
  public void testInterface() {
    final Map<String, Object> source = new HashMap<>();
    final Coder coder = new Coder();
    Object proxyObj = EnhancerBuilder.newBuilder()
        // 接口
        .setInterfaces(new Class<?>[]{Map.class})
        // 父类
        .setSuperclass(Coder.class)
        // 回调函数的匹配方法
        .setCallbackFilter(m -> Map.class.isAssignableFrom(m.getDeclaringClass()) ? 0 : 1)
        // 回调
        .setCallbacks(
            // 回调函数 0
            (MethodInterceptor) (obj, method, args, proxy) -> ReflectUtils.invoke(source, method, args),
            // 回调函数 1
            (MethodInterceptor) (obj, method, args, proxy) -> ReflectUtils.invoke(coder, method, args)
        )
        .create();


    Map<String, Object> proxyMap = (Map<String, Object>) proxyObj;
    proxyMap.put("hehe", "呵呵");
    System.err.println(source);

    ((Coder) proxyObj).say();

  }


  @After
  public void tearDown() throws Exception {
  }


  public static class Coder {

    public String say() {
      System.err.println("hello world: " + getClass().getSimpleName() + "\n");
      return "hello world !";
    }

  }


}