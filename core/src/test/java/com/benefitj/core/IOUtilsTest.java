package com.benefitj.core;

import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicInteger;

public class IOUtilsTest extends BaseTest {

  @Test
  public void testReadFile() {
    File file = ClasspathUtils.getFile("abb_application-dev.properties");
    IOUtils.readFileLines(file).forEach(System.err::println);
  }

  @Test
  public void testProgress() {
    File dir = new File("D:\\develop\\tools");
    File[] files = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".zip"));
    final AtomicInteger index = new AtomicInteger(0);
    IOUtils.process(files
        , (source, buf, len) -> {
          // 处理读取的数据
        }
        , (source, totalLength, totalProgress, progress) -> {
          if (index.incrementAndGet() % 20 == 0 || totalLength == totalProgress) {
            double currentProgress = totalProgress * 100.0 / totalLength;
            log.info("{}, {}, {} ==>: {}%"
                , totalLength
                , totalProgress
                , source.getName()
                , Utils.fmt(currentProgress, "0.00")
            );
          }
        });
  }

  @Test
  public void testMd5() {
    try {
      String message = "0762ce56ac504e90b653d00ddad52fe5";//IdUtils.uuid();
      // 2 将消息变成byte数组
      byte[] input = message.getBytes();
      // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
      MessageDigest md = MessageDigest.getInstance("MD5");
      // 3 计算后获得字节数组,这就是那128位了
      byte[] buff = md.digest(input);
      // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
      System.err.println(message + ", " + HexUtils.bytesToLong(buff) + ", " + HexUtils.bytesToHex(buff));
      System.err.println("length: " + buff.length);
      byte[] copy = BufCopy.get().copy(buff, 4, 8);
      System.err.println("2.1 ===>: " + HexUtils.bytesToHex(copy));
      System.err.println("2.2 ===>: " + HexUtils.bytesToLong(copy));

      BigInteger num = new BigInteger(buff);
      System.err.println("num: " + num.toString());

      System.err.println("\n--------------------------------\n");
      byte[] md5hex = md.digest("abc".getBytes());
      System.err.println(HexUtils.bytesToHex(md5hex));
      System.err.println(new BigInteger(HexUtils.bytesToHex(md5hex), 16));
      System.err.println("\n--------------------------------\n");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}