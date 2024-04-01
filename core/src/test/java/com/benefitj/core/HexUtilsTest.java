package com.benefitj.core;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;

public class HexUtilsTest extends BaseTest {


  @Test
  public void testBytesTo() {
    // 测试字节数组转换为数值
    // 大端
    byte[] beBytes = HexUtils.intToBytes(1234, ByteOrder.BIG_ENDIAN);
    byte[] leBytes = HexUtils.intToBytes(1234, ByteOrder.LITTLE_ENDIAN);
    log.info("节数组转换为二进制，大端{}, 小端{}", Arrays.toString(beBytes), Arrays.toString(leBytes));

    log.info("1234的16进制数据，大端[{}], 小端[{}]", HexUtils.bytesToHex(beBytes), HexUtils.bytesToHex(leBytes));
    log.info("16进制数据转换成short，大端[{}]，小端[{}]", HexUtils.bytesToShort(beBytes, ByteOrder.BIG_ENDIAN), HexUtils.bytesToShort(leBytes, ByteOrder.LITTLE_ENDIAN));
    log.info("16进制数据转换成int，大端[{}]，小端[{}]", HexUtils.bytesToInt(beBytes, ByteOrder.BIG_ENDIAN), HexUtils.bytesToInt(leBytes, ByteOrder.LITTLE_ENDIAN));
    log.info("16进制数据转换成long，大端[{}]，小端[{}]", HexUtils.bytesToLong(beBytes, ByteOrder.BIG_ENDIAN), HexUtils.bytesToLong(leBytes, ByteOrder.LITTLE_ENDIAN));

    // 字节数组转换为二进制字符串
    log.info("字节数组转换为二进制字符串: {}", HexUtils.bytesToBinary(leBytes));
  }

  @Test
  public void testReverse() {
    byte[] bytes = HexUtils.longToBytes(102422230010242223L);
    System.err.println("bytes: " + HexUtils.bytesToHex(bytes));
    System.err.println("bytes.reverse: " + HexUtils.bytesToHex(HexUtils.reverse(bytes)));
  }


  @Test
  public void test_base64() {
    File f = new File("D:/home/znsx/logs/base64.txt");
    String base64 = IOUtils.readAsString(f);
    byte[] decode = Base64.getDecoder().decode(base64);
    IOUtils.write(decode, IOUtils.createFile(f.getParentFile(), "base64.pdf"));
  }

}