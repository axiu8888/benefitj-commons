package com.benefitj.frameworks.qrcode;

import com.google.zxing.BarcodeFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 二维码参数
 *
 * @author DINGXIUAN
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class QRCodeOption {

  /**
   * 图片格式: PNG
   */
  public static final String FORMAT_PNG = "PNG";
  /**
   * 图片格式: JPG
   */
  public static final String FORMAT_JPG = "JPG";

  /**
   * 二维码颜色
   */
  @Builder.Default
  private int color = Color.BLACK.getRGB();
  /**
   * 背景色
   */
  @Builder.Default
  private int bgColor = Color.WHITE.getRGB();

  /**
   * 二维码的文本
   */
  private String content;
  /**
   * 保存图片的格式，默认是PNG
   */
  @Builder.Default
  private String formatName = FORMAT_PNG;
  /**
   * 二维码的宽度, 默认是300
   */
  @Builder.Default
  private int width = 300;
  /**
   * 二维码的高度, 默认是300
   */
  @Builder.Default
  private int height = 300;
  /**
   * 编码, 默认 UTF-8
   */
  @Builder.Default
  private Charset charset = StandardCharsets.UTF_8;
  /**
   * 是否压缩
   */
  @Builder.Default
  private boolean compress = true;
  /**
   * 图标的输入流
   */
  private InputStream iconInput;
  /**
   * 图标的宽度
   */
  @Builder.Default
  private int iconWidth = 80;
  /**
   * 图标高度
   */
  @Builder.Default
  private int iconHeight = 80;
  /**
   * 格式: 默认是二维码
   */
  @Builder.Default
  private BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
  /**
   *
   */
  @Builder.Default
  private HintType hintType = HintType.create();

}
