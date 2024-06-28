package com.benefitj.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加密
 */
public class CodecUtils {

  /**
   * base64转码，UTF-8
   */
  public static String encodeBase64(String src) {
    return encodeBase64(src, StandardCharsets.UTF_8);
  }

  /**
   * base64转码
   */
  public static String encodeBase64(String src, Charset charset) {
    return Base64.getEncoder().encodeToString(src.getBytes(charset));
  }

  /**
   * base64解码，UTF-8
   */
  public static String decodeBase64(String src) {
    return decodeBase64(src, StandardCharsets.UTF_8);
  }

  /**
   * base64解码
   */
  public static String decodeBase64(String src, Charset charset) {
    return new String(Base64.getDecoder().decode(src.getBytes(charset)));
  }

  /**
   * URL转码，UTF-8
   */
  public static String encodeURL(String src) {
    return encodeURL(src, StandardCharsets.UTF_8);
  }

  /**
   * URL转码
   */
  public static String encodeURL(String src, Charset charset) {
    return CatchUtils.tryThrow(() -> URLEncoder.encode(src, charset));
  }

  /**
   * URL解码，UTF-8
   */
  public static String decodeURL(String src) {
    return decodeURL(src, StandardCharsets.UTF_8);
  }

  /**
   * URL解码
   */
  public static String decodeURL(String src, Charset charset) {
    return CatchUtils.tryThrow(() -> URLDecoder.decode(src, charset));
  }

  /**
   * 获取一个文件的md5值
   */
  public static String md5(File in) {
    try (final FileInputStream fis = new FileInputStream(in)) {
      return md5(fis);
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取一个文件的md5值
   */
  public static String md5(byte[] in) {
    return md5(new ByteArrayInputStream(in));
  }

  /**
   * 获取一个文件的md5值
   */
  public static String md5(InputStream in) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      IOUtils.read(in, 1024 << 4, true, (buf, len) -> md5.update(buf, 0, len));
      return HexUtils.bytesToHex(md5.digest(), true);
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }


  /**
   * 获取验证码byte数组，基于Modbus CRC16的校验算法
   */
  public static byte[] CRC16(byte[] data) {
    return CRC16(data, 0, data.length);
  }

  /**
   * 获取验证码byte数组，基于Modbus CRC16的校验算法
   *
   * @param data  数据
   * @param start 开始的位置
   * @param len   数据长度
   * @return 返回校验和
   */
  public static byte[] CRC16(byte[] data, int start, int len) {
    // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
    int crc = 0xFFFF;
    for (int i = 0; i < len; i++) {
      byte b = data[start + i];
      // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
      crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (b & 0xFF));
      for (int j = 0; j < 8; j++) {
        // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
        if ((crc & 0x0001) > 0) {
          // 如果移出位为 1, CRC寄存器与多项式A001进行异或
          crc = crc >> 1;
          crc = crc ^ 0xA001;
        } else {
          // 如果移出位为 0,再次右移一位
          crc = crc >> 1;
        }
      }
    }
    return HexUtils.shortToBytes((short) crc, ByteOrder.LITTLE_ENDIAN);
  }

}
