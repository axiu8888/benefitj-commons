package com.benefitj.frameworks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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