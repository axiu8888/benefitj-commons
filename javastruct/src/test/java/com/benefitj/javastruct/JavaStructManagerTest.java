package com.benefitj.javastruct;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.BinaryHelper;
import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.HexUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.javastruct.entity.CollectorPacket;
import com.benefitj.javastruct.entity.LeadWave;
import com.benefitj.javastruct.entity.Person;
import lombok.Data;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

public class JavaStructManagerTest {


  private JavaStructManager manager = JavaStructManager.get();
  /**
   * 二进制工具
   */
  private BinaryHelper binary = BinaryHelper.BIG_ENDIAN;

  @Before
  public void setUp() throws Exception {
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    for (int i = 0; i < 100; i++) {
      testBytes();
//      testResolver();
    }
  }

  /**
   * 测试转换成字节
   */
  @Test
  public void testBytes() {
    Person person = new Person();
    person.setName("蔡狗");
    person.setAge(30);
    person.setV5(new short[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -22, -1117, -1774, 333, 3066, 7229, 10039, 14294, 16503, 12029, 8564, 3493, 56, -4939, -4200, -1554, -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 349, 93, 49, 251, 463, 436, 457, 641, 769, 784, 890, 1001, 1210, 1248, 1333, 1493, 1697, 1792, 1876, 2012, 2286, 2618, 2875, 3180, 3548, 3794, 3964, 3895, 3787, 3609, 3454, 3351, 3348, 2933, 2037, 1453, 823, 554, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8, -197, -1, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 444, 734, 1064, 1275, 1427, 1571, 1599, 1647, 1638, 1437, 1343, 1173, 998, 470, 358, 0
    });
    person.setTime(System.currentTimeMillis());
    person.setCreateTime(new Date());
    person.setHex(binary.bytesToHex(binary.longToBytes(1024 * 1234 * 123456789L)));
    person.setHello("世界,你好!");

    long start = System.nanoTime();
    byte[] data = manager.toBytes(person);
    System.err.println("testConvert时间: " + (System.nanoTime() - start));
    System.err.println(binary.bytesToHex(data));
  }

  /**
   * 测试解析器
   */
  @Test
  public void testParse() {
    // 缺少字段时
//    String hex = "E894A1E78B97000000001E00008DE20A3CE80000000000000000000000000000000000000000000000000000000000000000000000000000000000FFEAFBA3F912014D0BFA1C3D273737D640772EFD21740DA50038ECB5EF98F9EEFFF700000000000000000000000000000000000000000000000000000000015D005D003100FB01CF01B401C9028103010310037A03E904BA04E0053505D506A10700075407DC08EE0A3A0B3B0C6C0DDC0ED20F7C0F370ECB0E190D7E0D170D140B7507F505AD0337022A000A000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000FFF8FF3BFFFF0000FFFFFFFF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001BC02DE042804FB05930623063F066F0666059D053F049503E601D60166000000000177ED500631603CC084";
    // 测试完整的数据
    String hex = "E894A1E78B97000000001E00008DE20A3CE80000000000000000000000000000000000000000000000000000000000000000000000000000000000FFEAFBA3F912014D0BFA1C3D273737D640772EFD21740DA50038ECB5EF98F9EEFFF700000000000000000000000000000000000000000000000000000000015D005D003100FB01CF01B401C9028103010310037A03E904BA04E0053505D506A10700075407DC08EE0A3A0B3B0C6C0DDC0ED20F7C0F370ECB0E190D7E0D170D140B7507F505AD0337022A000A000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000FFF8FF3BFFFF0000FFFFFFFF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001BC02DE042804FB05930623063F066F0666059D053F049503E601D6016600000000017925F7A2EE608CCAA8E4B896E7958C2CE4BDA0E5A5BD2100000000000000";
    long start = System.nanoTime();
    Person person = manager.parseObject(binary.hexToBytes(hex), Person.class);
    System.err.println("testResolver时间: " + (System.nanoTime() - start));
    System.err.println(JSON.toJSONString(person));
  }

  @Test
  public void testLeadWave() {
    String hex = "603dcd820000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000fff8ff3bffff0000ffffffff0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001bc02de042804fb05930623063f066f0666059d053f049503e601d6016600000000000000000000000000000000000000000000000000000000000000000000ffeafba3f912014d0bfa1c3d273737d640772efd21740da50038ecb5ef98f9eefff700000000000000000000000000000000000000000000000000000000015d005d003100fb01cf01b401c9028103010310037a03e904ba04e0053505d506a10700075407dc08ee0a3a0b3b0c6c0ddc0ed20f7c0f370ecb0e190d7e0d170d140b7507f505ad0337022a000a000000000000000000000000000000000000000000000000000000000000";
    long start = System.nanoTime();
    LeadWave lw = manager.parseObject(binary.hexToBytes(hex), LeadWave.class);
    System.err.println("testResolver时间: " + (System.nanoTime() - start));
    System.err.println(JSON.toJSONString(lw));
  }

  @Test
  public void testCollector() {
    File file = ClasspathUtils.getFile("collector.hex");
    byte[] data = IOUtils.readAsBytes(file);
    CollectorPacket packet = manager.parseObject(HexUtils.hexToBytes(new String(data)), CollectorPacket.class);
    System.err.println(JSON.toJSONString(packet));
  }

  @Test
  public void test222() {
    File bin = new File("D:\\tmp\\cache\\znsx\\ecg_3fd8897833d9461eb3c39865d15c0312_From20240716081757To20240716083450_12.DAT");
    IOUtils.read(bin, (buf, lineNumber) -> {
      Holter12 holter12 = manager.parseObject(buf, Holter12.class);
      System.err.println(JSON.toJSONString(holter12));
    });
  }

  @Data
  @JavaStructClass
  public static class Holter12 {
    /**
     * 包序号
     */
    @JavaStructField(startAt = 0, size = 4)
    int sn;
    // 忽略
    @JavaStructField(startAt = 4, size = 4)
    int skip1;
    /**
     * 时间
     */
    @JavaStructField(startAt = 8, size = 4)
    int time;

    @JavaStructField(size = 2, arrayLength = 200)
    int[] I;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] II;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V1;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V2;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V3;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V4;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V5;
    @JavaStructField(size = 2, arrayLength = 200)
    int[] V6;

  }


}