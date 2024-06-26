package com.benefitj.core.executable;

import com.benefitj.core.BaseTest;
import org.junit.jupiter.api.Test;

public class InstantiatorTest extends BaseTest {

  @Test
  public void testCreate() {
    // 创建实例
    TestAbc abc = Instantiator.get().create(TestAbc.class, "abc");
    System.err.println(abc.name);
  }

  @Test
  public void testCreate2() {
    ConstructorInvoker<TestAbc> invoker = new ConstructorInvoker<>(TestAbc.class);
    // 创建实例
    TestAbc abc = invoker.newInstance("呵呵");
    System.err.println(abc.name);

    TestAbc abc2 = invoker.newInstance(11);
    System.err.println(abc2.name);

    TestAbc abc3 = invoker.newInstance("你好", 13);
    System.err.println(abc3.name);
  }

  public static class TestAbc {

    private String name;

    public TestAbc() {
    }

    public TestAbc(String name) {
      this.name = name;
    }

    public TestAbc(Integer age, String name) {
      this.name = "age: " + age + ", " + name;
    }

    public TestAbc(Integer age) {
      this.name = "age: " + age;
    }
  }
}