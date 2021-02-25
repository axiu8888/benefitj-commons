package com.benefitj.javastruct.convert;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.JavaStructManager;
import com.benefitj.javastruct.entity.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FieldConverterTest {
  public static void main(String[] args) throws Exception {
    FieldConverterTest test = new FieldConverterTest();

    test.setUp();


    test.testConvert();

    test.tearDown();

  }

  private JavaStructManager structManager;

  @Before
  public void setUp() throws Exception {
    this.structManager = new JavaStructManager();

    this.structManager.init();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testConvert() {
    Person person = new Person();
    person.setName("理查德克莱德曼");
    person.setAge((byte) 30);
    person.setV5(new short[] {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -22, -1117, -1774, 333, 3066, 7229, 10039, 14294, 16503, 12029, 8564, 3493, 56, -4939, -4200, -1554, -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 349, 93, 49, 251, 463, 436, 457, 641, 769, 784, 890, 1001, 1210, 1248, 1333, 1493, 1697, 1792, 1876, 2012, 2286, 2618, 2875, 3180, 3548, 3794, 3964, 3895, 3787, 3609, 3454, 3351, 3348, 2933, 2037, 1453, 823, 554, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8, -197, -1, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 444, 734, 1064, 1275, 1427, 1571, 1599, 1647, 1638, 1437, 1343, 1173, 998, 470, 358, 0
    });

    person.setTime(System.currentTimeMillis());

    String str = new String(HexUtils.hexToBytes("E79086E69FA5E5BEB7E5858BE88EB1E5BEB7E69BBC"), StandardCharsets.UTF_8);
    System.err.println(str);


    byte[] data = structManager.convert(person);
    String hex = HexUtils.bytesToHex(data);
    System.err.println(hex);

  }
}