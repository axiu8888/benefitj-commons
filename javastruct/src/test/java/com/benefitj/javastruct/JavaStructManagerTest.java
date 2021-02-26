package com.benefitj.javastruct;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.entity.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class JavaStructManagerTest {

  private JavaStructManager manager = JavaStructManager.getInstance();

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

    byte[] data = manager.convert(person);
    String hex = HexUtils.bytesToHex(data);
    System.err.println(hex);
  }

  /**
   * 测试解析器
   */
  @Test
  public void testResolver() {
    String hex = "B2CCB9B7000000000000000000000000000000001E0000000000000000000000000000000000000000000000000000000000" +
        "000000FFEAFBA3F912014D0BFA1C3D273737D640772EFD21740DA50038ECB5EF98F9EEFFF70000000000000000000000000000000000000" +
        "0000000000000000000015D005D003100FB01CF01B401C9028103010310037A03E904BA04E0053505D506A10700075407DC08EE0A3A0B3B" +
        "0C6C0DDC0ED20F7C0F370ECB0E190D7E0D170D140B7507F505AD0337022A000A00000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000FF" +
        "F8FF3BFFFF0000FFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
        "000000300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
        "00000000000001BC02DE042804FB05930623063F066F0666059D053F049503E601D60166000000000177DE38FD5C6038E394";
    Person person = manager.parse(Person.class, HexUtils.hexToBytes(hex));
    System.err.println(JSON.toJSONString(person));
  }

}