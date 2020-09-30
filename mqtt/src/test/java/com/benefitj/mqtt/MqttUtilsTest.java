package com.benefitj.mqtt;


import com.benefitj.core.HexUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.message.impl.ConnectMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MqttUtilsTest {

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * 测试剩余长度的编解码
   */
  @Test
  public void remainingLengthCodec() {
//    int length = 128; // 128 (0x80, 0x01)
//    int length = 16383; // 16 383 (0xFF, 0x7F)
//    int length = 2097152; // 2 097 152 (0x80, 0x80, 0x80, 0x01)
    int length = 268435455; // 268 435 455 (0xFF, 0xFF, 0xFF, 0x7F)

    byte[] remainLength = MqttUtils.remainingLengthEncode(length);
    System.err.println("remainingLengthEncode: " + HexUtils.bytesToHex(remainLength));
    System.err.println("remainingLengthDecode: " + MqttUtils.remainingLengthDecode(remainLength));
  }


  /**
   * 测试连接报文编码
   */
  @Test
  public void testConnectEncode() {
    ConnectMessage connect = new ConnectMessage();
    // 设置 client ID
    connect.setClientId(IdUtils.nextId("mqtt", null, 12));
    // 协议名: MQTT/MQTT3.1.1
    connect.setProtocolName("MQTT");
    // 协议等级
    connect.setProtocolLevel(4);
    // 遗嘱标志
    connect.setWillFlag(true);
    // 遗嘱保留
    connect.setWillRetain(true);
    // 遗嘱 QoS
    connect.setWillQoS(0);
    // 遗嘱主题
    connect.setWillTopic("test");
    // 遗嘱消息
    connect.setWillMessage("呵呵, game over!");
    // 用户名
    connect.setUsername("admin");
    // 密码
    connect.setPassword("123456".getBytes());
    // 保持30秒
    connect.setKeepAlive(30);

    byte[] encode = MqttUtils.connectEncode(connect);
    System.err.println("encode: " + HexUtils.bytesToHex(encode));

  }

}