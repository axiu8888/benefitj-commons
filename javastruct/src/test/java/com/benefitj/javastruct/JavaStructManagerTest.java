package com.benefitj.javastruct;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.entity.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class JavaStructManagerTest {
  public static void main(String[] args) throws Exception {
    JavaStructManagerTest test = new JavaStructManagerTest();

    test.setUp();

    test.testConvert();

    test.testResolver();

    test.tearDown();

  }

  private JavaStructManager structManager = JavaStructManager.getInstance();

  private byte[] data;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * 测试转换成字节
   */
  @Test
  public void testConvert() {
    Person person = new Person();
    person.setName("蔡狗");
    person.setAge(30);
    person.setV5(new short[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -22, -1117, -1774, 333, 3066, 7229, 10039, 14294, 16503, 12029, 8564, 3493, 56, -4939, -4200, -1554, -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 349, 93, 49, 251, 463, 436, 457, 641, 769, 784, 890, 1001, 1210, 1248, 1333, 1493, 1697, 1792, 1876, 2012, 2286, 2618, 2875, 3180, 3548, 3794, 3964, 3895, 3787, 3609, 3454, 3351, 3348, 2933, 2037, 1453, 823, 554, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8, -197, -1, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 444, 734, 1064, 1275, 1427, 1571, 1599, 1647, 1638, 1437, 1343, 1173, 998, 470, 358, 0
    });
    person.setTime(System.currentTimeMillis());
    person.setCreateTime(new Date());

    this.data = structManager.convert(person);
    String hex = HexUtils.bytesToHex(data);
    System.err.println(hex);
  }

  /**
   * 测试解析器
   */
  @Test
  public void testResolver() {
    Person person = structManager.parse(Person.class, data);

    System.err.println(JSON.toJSONString(person));

  }

}