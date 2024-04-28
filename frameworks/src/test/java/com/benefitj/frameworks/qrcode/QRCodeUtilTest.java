package com.benefitj.frameworks.qrcode;


import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.frameworks.BaseTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

class QRCodeUtilTest extends BaseTest {

  /**
   * 测试生成二维码
   */
  @Test
  void testCreateQRCode() {
    File icon = new File("D:/tmp/QRCode/test.png");
    QRCodeOptions options = QRCodeOptions.builder()
        .content("Hello World.")
        .formatName(QRCodeOptions.FORMAT_PNG)// 设置二维码格式
        .width(500)// 设置二维码的宽高，如果不相等，二维码会变小
        .height(500) // 设置二维码的宽高，如果不相等，二维码会变小
        .color(Color.BLACK.getRGB())// 设置二维码的颜色
        .bgColor(Color.WHITE.getRGB())// 设置二维码的背景色
        .iconInput(IOUtils.newFIS(icon)) // 嵌入图标
        .iconHeight(150)
        .iconWidth(150)
        .build();
    QRCodeUtils.encode(options, new File("D:/tmp/QRCode/" + DateFmtter.fmtNow("yyyyMMddHHmmss") + ".png"));
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
