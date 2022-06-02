package com.benefitj.core;

import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ProxyUtilsTest extends BaseTest {


  @Test
  public void testNewListProxy() {
    Hello hello = ProxyUtils.newListProxy(Hello.class);
    // 添加代理
    ((List)hello).add((Hello) word -> System.err.println("list.say1: " + word));
    ((List)hello).add((Hello) word -> System.err.println("list.say2: " + word));
    hello.say("呵呵");
  }

  @Test
  public void testNewMapProxy() {
    Hello hello2 = ProxyUtils.newMapProxy(Hello.class);
    // 添加代理
    ((Map)hello2).put("hello1", (Hello) word -> System.err.println("map.say1: " + word));
    ((Map)hello2).put("hello2", (Hello) word -> System.err.println("map.say2: " + word));
    hello2.say("呵呵");
  }


  public interface Hello {

    void say(String word);

  }


}