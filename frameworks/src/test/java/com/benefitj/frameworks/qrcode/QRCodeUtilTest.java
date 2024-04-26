package com.benefitj.frameworks.qrcode;


import com.benefitj.frameworks.BaseTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class QRCodeUtilTest extends BaseTest {

  /**
   * 测试生成二维码
   */
  @Test
  void testCreateQRCode() throws IOException {
    File icon = new File("D:/tmp/d412dd8d521ae3e273df22ec00d107b.jpg");
    QRCodeOption options = QRCodeOption.builder()
        .content("Hello World.")
        .formatName(QRCodeOption.FORMAT_PNG)// 设置二维码格式
        .width(500)// 设置二维码的宽高，如果不相等，二维码会变小
        .height(500) // 设置二维码的宽高，如果不相等，二维码会变小
        .color(Color.BLACK.getRGB())// 设置二维码的颜色
        .bgColor(Color.YELLOW.getRGB())// 设置二维码的背景色
        .iconInput(new FileInputStream(icon)) // 嵌入图标
        .iconHeight(150)
        .iconWidth(150)
        .build();
    QRCodeUtils.encode(options, new File("D:/tmp/QRCode/" + System.currentTimeMillis() + ".png"));
  }

  /**
   * 对二维码解码
   */
  @Test
  void testQRCodeDecode() {
    String text = QRCodeUtils.decode(new File("E:/test/QRCode/hello-world.png"));
    System.out.println(text);
  }

}
