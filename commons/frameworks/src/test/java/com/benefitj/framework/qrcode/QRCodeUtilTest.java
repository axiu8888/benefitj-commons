package com.benefitj.framework.qrcode;


import com.benefitj.framework.BaseTest;
import com.benefitj.frameworks.qrcode.QRCodeOptions;
import com.benefitj.frameworks.qrcode.QRCodeUtils;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class QRCodeUtilTest extends BaseTest {

  @Override
  public void setUp() {
    System.out.println("------------- start ----------------");
  }

  /**
   * 测试生成二维码
   */
  @Test
  public void testCreateQRCode() throws IOException {
    File iconFile = new File("D:/12468447.jpg");
    QRCodeOptions options = new QRCodeOptions.Builder()
        .setContent("Hello World.")
        .setFormatName(QRCodeOptions.FORMAT_PNG)// 设置二维码格式
        .setWidth(300)// 设置二维码的宽高，如果不相等，二维码会变小
        .setHeight(300) // 设置二维码的宽高，如果不相等，二维码会变小
        .setColor(Color.BLACK.getRGB())// 设置二维码的颜色
        .setBgColor(Color.WHITE.getRGB())// 设置二维码的背景色色
        .setIconInput(new FileInputStream(iconFile)) // 嵌入图标
        .setIconHeight(100)
        .setIconWidth(100)
        .build();
    QRCodeUtils.encodeQuietly(options, new File("/tmp/QRCode/" + System.currentTimeMillis() + ".png"));
  }


  /**
   * 对二维码解码
   */
  @Test
  public void testQRCodeDecode() {
    String text = QRCodeUtils.decodeQuietly(new File("E:/test/QRCode/hello-world.png"));
    System.out.println(text);
  }


  @Override
  public void tearDown() {
    System.out.println("------------- over ----------------");
  }

}
