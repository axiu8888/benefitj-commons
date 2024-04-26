package com.benefitj.frameworks.cglib;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class CGLibProxyTest {

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testCreateProxy() {
    Coder coderProxy = CGLibProxy.newProxy(Coder.class, (obj, method, args, proxy) -> {
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
  void testInterface() {
    // 创建代理
    Coder coder = CGLibProxy.newProxy(Coder.class
        , new Class[]{Map.class}
        , new Object[]{new HashMap<>()}
    );
    coder.put("hehe", "呵呵");
    coder.say();
    log.info("coder: {}", coder);
    log.info("coder json: {}", JSON.toJSONString(coder));

    coder.setSource("Hello ???");
    log.info("source: {}", coder.getSource());

    coder.setBirthday(TimeUtils.toDate(1982, 10, 2));
    log.info("age: {}", coder.getAge());
  }

  @Test
  void testInterfaceWrapper() {

    MapWrapper wrapper = CGLibProxy.newProxy(null
        , new Class<?>[]{MapWrapper.class}
        , new Object[]{new ConcurrentHashMap<>()}
    );

    wrapper.put("key-1", "1");
    wrapper.put("key-2", "2");
    wrapper.put("key-3", "3");

    System.err.println(wrapper.keySet());
    System.err.println(wrapper.values());
    System.err.println("wrapper: " + wrapper);

  }


  public static abstract class Coder extends SourceRoot<String> implements IPerson, Map<String, Object> {

    private Date birthday;

    public String say() {
      System.err.println("hello world: " + getClass().getSimpleName() + "\n");
      return "hello world !";
    }

    @Override
    public Date getBirthday() {
      return birthday;
    }

    public void setBirthday(Date birthday) {
      this.birthday = birthday;
    }
  }

  public interface IPerson {

    Date getBirthday();

    default int getAge() {
      return TimeUtils.getAge(getBirthday());
    }
  }

  public interface MapWrapper extends Map<String, Object> {
  }

}