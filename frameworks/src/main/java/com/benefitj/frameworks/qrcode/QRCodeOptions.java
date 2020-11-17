package com.benefitj.frameworks.qrcode;

import com.google.zxing.BarcodeFormat;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 二维码参数
 *
 * @author DINGXIUAN
 */
public class QRCodeOptions {

  public static QRCodeOptions create(String content) {
    return new QRCodeOptions(content);
  }

  public static QRCodeOptions create(String content, int size) {
    QRCodeOptions qrCode = new QRCodeOptions(content);
    qrCode.width = size;
    qrCode.height = size;
    return qrCode;
  }

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
  private int color = Color.BLACK.getRGB();
  /**
   * 背景色
   */
  private int bgColor = Color.WHITE.getRGB();

  /**
   * 二维码的文本
   */
  private String content;
  /**
   * 保存图片的格式，默认是PNG
   */
  private String formatName = FORMAT_PNG;
  /**
   * 二维码的宽度, 默认是300
   */
  private int width = 300;
  /**
   * 二维码的高度, 默认是300
   */
  private int height = 300;
  /**
   * 编码, 默认 UTF-8
   */
  private String charset = "UTF-8";
  /**
   * 是否压缩
   */
  private boolean compress = true;
  /**
   * 图标的输入流
   */
  private InputStream iconInput;
  /**
   * 图标的宽度
   */
  private int iconWidth = 80;
  /**
   * 图标高度
   */
  private int iconHeight = 80;
  /**
   * 格式: 默认是二维码
   */
  private BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
  /**
   *
   */
  private QRCodeHintType hintType = QRCodeHintType.create();

  public QRCodeOptions() {
  }

  public QRCodeOptions(String content) {
    this.setContent(content);
  }

  public QRCodeOptions(String content, InputStream iconInput) {
    this(content);
    this.iconInput = iconInput;
  }

  public QRCodeOptions(String content, File iconFile) {
    this(content);
    try {
      this.iconInput = new FileInputStream(iconFile);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getBgColor() {
    return bgColor;
  }

  public void setBgColor(int bgColor) {
    this.bgColor = bgColor;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("二维码内容不能为空!");
    }
    this.content = content;
  }

  public String getFormatName() {
    return formatName;
  }

  public void setFormatName(String formatName) {
    this.formatName = formatName;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public boolean isCompress() {
    return compress;
  }

  public void setCompress(boolean compress) {
    this.compress = compress;
  }

  public InputStream getIconInput() {
    return iconInput;
  }

  public void setIconInput(InputStream iconInput) {
    this.iconInput = iconInput;
  }

  public int getIconWidth() {
    return iconWidth;
  }

  public void setIconWidth(int iconWidth) {
    this.iconWidth = iconWidth;
  }

  public int getIconHeight() {
    return iconHeight;
  }

  public void setIconHeight(int iconHeight) {
    this.iconHeight = iconHeight;
  }

  public BarcodeFormat getBarcodeFormat() {
    return barcodeFormat;
  }

  public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
    this.barcodeFormat = barcodeFormat;
  }

  public QRCodeHintType getHintType() {
    return hintType;
  }

  public void setHintType(QRCodeHintType hintType) {
    this.hintType = hintType;
  }

  /**
   * {@code QRCodeOptions} builder static inner class.
   */
  public static final class Builder {
    private final QRCodeOptions options;

    public Builder() {
      this.options = new QRCodeOptions();
    }

    public Builder setContent(String content) {
      options.setContent(content);
      return this;
    }

    public Builder setColor(int color) {
      options.setColor(color);
      return this;
    }

    public Builder setBgColor(int bgColor) {
      options.bgColor = bgColor;
      return this;
    }

    public Builder setFormatName(String formatName) {
      options.setFormatName(formatName);
      return this;
    }

    public Builder setWidth(int width) {
      options.setWidth(width);
      return this;
    }

    public Builder setHeight(int height) {
      options.setHeight(height);
      return this;
    }

    public Builder setCharset(String charset) {
      options.setCharset(charset);
      return this;
    }

    public Builder setCompress(boolean compress) {
      options.setCompress(compress);
      return this;
    }

    public Builder setIconInput(InputStream iconInput) {
      options.setIconInput(iconInput);
      return this;
    }

    public Builder setIconWidth(int iconWidth) {
      options.setIconWidth(iconWidth);
      return this;
    }

    public Builder setIconHeight(int iconHeight) {
      options.setIconHeight(iconHeight);
      return this;
    }

    public Builder setBarcodeFormat(BarcodeFormat barcodeFormat) {
      options.setBarcodeFormat(barcodeFormat);
      return this;
    }

    public Builder setHintType(QRCodeHintType hintType) {
      options.setHintType(hintType);
      return this;
    }

    /**
     * Returns a {@code QRCodeOptions} built from the parameters previously set.
     *
     * @return a {@code QRCodeOptions} built with parameters of this {@code QRCodeOptions.Builder}
     */
    public QRCodeOptions build() {
      return options;
    }


  }
}
