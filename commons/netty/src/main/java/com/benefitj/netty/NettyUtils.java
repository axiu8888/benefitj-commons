package com.benefitj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

/**
 * Netty工具
 */
public class NettyUtils {

  private static final ByteBufCopy COPY = new ByteBufCopy();

  /**
   * 拷贝数据
   *
   * @param msg   消息
   * @param len   长度
   * @param reset 是否重置
   * @return 返回拷贝的数据
   */
  public static byte[] copy(ByteBuf msg, int len, boolean reset) {
    return COPY.copy(msg, len, false, reset);
  }

  /**
   * 拷贝数据
   *
   * @param msg   消息
   * @param len   长度
   * @param reset 是否重置
   * @return 返回拷贝的数据
   */
  public static byte[] copy(DatagramPacket msg, int len, boolean reset) {
    return copy(msg.content(), len, reset);
  }

  /**
   * 拷贝数据
   *
   * @param msg 消息
   * @return 返回拷贝的数据
   */
  public static byte[] copyAndReset(ByteBuf msg) {
    return copyAndReset(msg, msg.readableBytes());
  }

  /**
   * 拷贝数据
   *
   * @param msg 消息
   * @param len 长度
   * @return 返回拷贝的数据
   */
  public static byte[] copyAndReset(ByteBuf msg, int len) {
    return copy(msg, len, true);
  }

  /**
   * 拷贝数据
   *
   * @param msg 消息
   * @return 返回拷贝的数据
   */
  public static byte[] copyAndReset(DatagramPacket msg) {
    return copyAndReset(msg.content());
  }

  /**
   * 拷贝数据
   *
   * @param msg 消息
   * @param len 长度
   * @return 返回拷贝的数据
   */
  public static byte[] copyAndReset(DatagramPacket msg, int len) {
    return copy(msg.content(), len, true);
  }

  /**
   * 判断两个数组是否部分相等
   *
   * @param src  源数据
   * @param dest 目标数据
   * @return 返回是否匹配
   */
  public static boolean match(byte[] src, byte[] dest) {
    return match(src, 0, dest, 0);
  }

  /**
   * 判断两个数组是否部分相等
   *
   * @param src     源数据
   * @param dest    目标数据
   * @param destPos 目标数据的开始位置
   * @return 返回是否匹配
   */
  public static boolean match(byte[] src, byte[] dest, int destPos) {
    return match(src, 0, dest, destPos);
  }

  /**
   * 判断两个数组是否部分相等
   *
   * @param src     源数据
   * @param dest    目标数据
   * @param destPos 目标数据的开始位置
   * @param len     比较的长度
   * @return 返回是否匹配
   */
  public static boolean match(byte[] src, byte[] dest, int destPos, int len) {
    return match(src, 0, dest, destPos, len);
  }

  /**
   * 判断两个数组是否部分相等
   *
   * @param array1    数组1
   * @param array1Pos 数组1的开始位置
   * @param array2    数组2
   * @param array2Pos 数组2的开始位置
   * @return 返回是否匹配
   */
  public static boolean match(byte[] array1, int array1Pos, byte[] array2, int array2Pos) {
    int len = Math.min(array1.length - array1Pos, array2.length - array2Pos);
    return match(array1, array1Pos, array2, array2Pos, len);
  }

  /**
   * 判断两个数组是否部分相等
   *
   * @param array1    数组1
   * @param array1Pos 数组1的开始位置
   * @param array2    数组2
   * @param array2Pos 数组2的开始位置
   * @param len       长度
   * @return 返回是否匹配
   */
  public static boolean match(byte[] array1, int array1Pos, byte[] array2, int array2Pos, int len) {
    for (int i = 0; i < len; i++) {
      if (array1[array1Pos + i] != array2[array2Pos + i]) {
        return false;
      }
    }
    return true;
  }

}
