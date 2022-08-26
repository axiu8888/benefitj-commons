package com.benefitj.core;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.junit.Test;

public class HexTest extends BaseTest {

  @Test
  public void testParse() {
    String hex = "4D3C2D530A150201023B29000009104F";
    byte[] data = HexUtils.hexToBytes(hex);

    int start = 5;
    System.err.println("模式： " + (data[start + 1] & 0xFF)); // 模式（被动1）	模式（主动2）	模式（智能被动3）	模式（智能主动4）
    System.err.println("方向： " + (data[start + 2] & 0xFF));
    System.err.println("倒计时min： " + (data[start + 3] & 0xFF));
    System.err.println("倒计时sec： " + (data[start + 4] & 0xFF));
    System.err.println("里程低字节： " + (data[start + 5] & 0xFF));
    System.err.println("里程高字节： " + (data[start + 6] & 0xFF));
    System.err.println("里程： " + HexUtils.bytesToShort(data[start + 6], data[start + 5]));
    System.err.println("实时速度： " + (data[start + 7] & 0xFF));

    System.err.println("版本: " + new String(HexUtils.hexToBytes("20312E3030")));

  }

  @Test
  public void testParse2() {
    //System.err.println("packetSn: " + BinaryHelper.LITTLE_ENDIAN.bytesToInt(new byte[]{(byte) 0xF0, 0x00, 0x00, 0x00}));
    String hex = "000000000000000000000000000000000100010090F700D0FF9002100100020002000300030004000400050006000600D0F8051000B0FF2007000800090009000A000B000C000D000E000F0008F8FF30050000A0100012001300140015001600180019001B001C0058F800A0FF1005201D001F002100220024002500270029002B002C0080F8FF10052000602E00300032003400360038003A003C003E00410058F8FFB0FF3005F04300450047004A004C004F00510054005600590058F805000090FF105B005E0060006300660069006B006E007100740070F9FF10050000A077007A007D0080008300860089008D006300650030F800F0FFC0FAB067006A006C006E0070007300750078007A007D0020F9FFD0FA9000D07F0082008400870089008C008F0091009400970008F8000000F0FA609A009C009F00A200A500A800AB00AE00B100B400A8F8FA6000D0FFF0B700BA00BD00C000C300C600C900CD00D000D30058F8FFF0FA9000E0D600DA00DD00E000E400E700EA00EE00F100F500E0F700B0FFF0FA70F800FC000001030107010A010E0112011501190168F7FFE0FA8000D01D012101250128012C013001340138013C014001E0F7000000F0FAA0440148014C015001540158015C0160016401690168F7FBA000D0FFE06D01710175017A017E01820187018B019001940158F8FFE0FA9000E098019D01A201A60173027902800287028E02950230F8FFA0FF3005009C02A302AA02B102B902C002C702CE02D602DD02F8F8FF4005F0FFA0E402EC02F302FB0202030A03120319032103290308F800B0FF300500310338034003480350035803960214006CFD8CFC30F805000090FF309AFCA0FCA8FCB2FCB8FCBBFCC3FCCCFCD4FCDDFC80F8FF2005100080E3FCECFCF2FCFAFC04FD0FFD20FD38FD4FFD62FD90F7FF90FF1005F073FD83FD93FDA1FDABFDB0FDAEFDAAFDA5FD9FFD08F8FF1005F0FF80";
    byte[] data = HexUtils.hexToBytes(hex);
    System.err.println("data.length: " + data.length);
    char[] chars = hex.toCharArray();
    StringBuilder sb = new StringBuilder(28);
    for (int i = 0; i < chars.length; i++) {
      sb.append(chars[i]).append(((i + 1) % 28 == 0) ? "\n" : "");
    }
    System.err.println(sb.toString());

    Packet packet = new Packet();
    //packet.setPacketSn(BinaryHelper.LITTLE_ENDIAN.bytesToInt(data[0], data[1], data[2], data[3]));
    int start = 0;
    //packet.setEcg(parseWave(data, 0, data.length, start + 0, 10, 28));
    packet.setResp(parseWave(data, 0, data.length, start + 20, 1, 28));
    packet.setX(parseWave(data, 0, data.length, start + 22, 1, 28));
    packet.setY(parseWave(data, 0, data.length, start + 24, 1, 28));
    packet.setZ(parseWave(data, 0, data.length, start + 26, 1, 28));

    System.err.println(JSON.toJSONString(packet));
  }

  public static int[] parseWave(byte[] data, int start, int end, int offset, int size, int step) {
    System.err.println("\nstart------------------------");
    int[] wave = new int[(end - start) / step * size];
    byte b1, b2;
    for (int i = start, j = 0; i < end; i += step) {
      System.err.println("segment: " + HexUtils.bytesToHex(BufCopy.get().copy(data, i, step)));
      for (int k = 0, v; k < size; k++, j++) {
        b1 = data[i + 2 * k + offset];
        b2 = data[i + 1 + 2 * k + offset];
        wave[j] = v = BinaryHelper.LITTLE_ENDIAN.bytesToInt(b1, b2);
        System.err.println(""
            + "i: " + i
            + ",\t p: " + (i + 2 * k + offset)
            + ",\t j: " + j
            + ",\t v: " + v
            + ",\t k: " + k
            + ",\t b: " + HexUtils.bytesToHex(new byte[]{b1, b2})
        );
      }
    }
    System.err.println("end------------------------\n");
    return wave;
  }

  @Data
  static class Packet {
    /**
     * 包序号
     */
    int packetSn;
    /**
     * 心电
     */
    int[] ecg;
    /**
     * 呼吸
     */
    int[] resp;
    /**
     * X轴
     */
    int[] x;
    /**
     * Y轴
     */
    int[] y;
    /**
     * Z轴
     */
    int[] z;

    public int getPacketSn() {
      return packetSn;
    }

    public void setPacketSn(int packetSn) {
      this.packetSn = packetSn;
    }

    public int[] getEcg() {
      return ecg;
    }

    public void setEcg(int[] ecg) {
      this.ecg = ecg;
    }

    public int[] getResp() {
      return resp;
    }

    public void setResp(int[] resp) {
      this.resp = resp;
    }

    public int[] getX() {
      return x;
    }

    public void setX(int[] x) {
      this.x = x;
    }

    public int[] getY() {
      return y;
    }

    public void setY(int[] y) {
      this.y = y;
    }

    public int[] getZ() {
      return z;
    }

    public void setZ(int[] z) {
      this.z = z;
    }
  }

}