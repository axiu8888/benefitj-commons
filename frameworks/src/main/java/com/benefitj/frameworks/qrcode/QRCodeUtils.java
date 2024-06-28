package com.benefitj.frameworks.qrcode;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码的工具类
 */
public class QRCodeUtils {

  private static final Map<DecodeHintType, Object> DEFAULT_DECODE_HINTS;

  static {
    Map<DecodeHintType, Object> map = new HashMap<>();
    map.put(DecodeHintType.CHARACTER_SET, "UTF-8");
    DEFAULT_DECODE_HINTS = Collections.unmodifiableMap(map);
  }

  public static QRCodeOptions defaultOptions(String content) {
    return defaultOptions(content, 300);
  }

  public static QRCodeOptions defaultOptions(String content, int size) {
    return QRCodeOptions.builder()
        .content(content)
        .width(size)
        .height(size)
        .build();
  }

  /**
   * 生成二维码
   *
   * @param content 文本内容
   * @return 返回生成的二维码字节数组
   */
  public static byte[] encode(String content) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    encode(content, out);
    return out.toByteArray();
  }

  /**
   * 生成二维码
   *
   * @param opts 二维码配置
   * @return 返回生成的二维码字节数组
   */
  public static byte[] encode(QRCodeOptions opts) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    encode(opts, out);
    return out.toByteArray();
  }

  /**
   * 创建二维码到指定文件
   *
   * @param content 二维码内容
   * @param dest    目标文件
   */
  public static void encode(String content, File dest) {
    encode(defaultOptions(content), dest);
  }

  /**
   * 创建二维码到指定文件
   *
   * @param opts  二维码配置
   * @param dest 目标文件
   */
  public static void encode(QRCodeOptions opts, File dest) {
    try {
      BufferedImage image = bufferedImage(opts);
      // 嵌入图标
      insetIcon(opts, image);
      if (!dest.exists())
        IOUtils.mkDirs(dest.getAbsolutePath());
      ImageIO.write(image, opts.getFormatName(), dest);
    } catch (IOException e) {
      throw new QRCodeException(e);
    }
  }

  /**
   * 创建二维码
   *
   * @param content 二维码内容
   * @param out     输出流
   */
  public static void encode(String content, OutputStream out) {
    encode(defaultOptions(content), out);
  }

  /**
   * 创建二维码
   *
   * @param opts 二维码配置
   * @param out 输出流
   */
  public static void encode(QRCodeOptions opts, OutputStream out) {
    try {
      BufferedImage image = bufferedImage(opts);
      // 嵌入图标
      insetIcon(opts, image);
      ImageIO.write(image, opts.getFormatName(), out);
    } catch (IOException e) {
      throw new QRCodeException(e);
    }
  }

  /**
   * 解码
   *
   * @param in 文件
   * @return 返回解码后的文本
   */
  public static String decode(File in) {
    try {
      return decodeResult(ImageIO.read(in)).getText();
    } catch (IOException e) {
      throw new QRCodeException(e);
    }
  }

  /**
   * 解码
   *
   * @param input 二维码的输入流
   * @return 返回解码后的文本
   */
  public static String decode(InputStream input) {
    try {
      BufferedImage image = ImageIO.read(input);
      return decodeResult(image).getText();
    } catch (IOException e) {
      throw new QRCodeException(e);
    }
  }

  /**
   * 解码
   *
   * @param image 图片
   * @return 结果
   */
  static Result decodeResult(BufferedImage image) {
    return decodeResult(image, DEFAULT_DECODE_HINTS);
  }

  /**
   * 解码
   *
   * @param image 图片
   * @param hints 解码提示
   * @return 返回解码的结果
   */
  static Result decodeResult(BufferedImage image, Map<DecodeHintType, Object> hints) {
    try {
      BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
      return new MultiFormatReader().decode(bitmap, hints);
    } catch (NotFoundException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  static BitMatrix bitMatrix(QRCodeOptions opts) {
    try {
      MultiFormatWriter mfw = new MultiFormatWriter();
      BarcodeFormat format = opts.getBarcodeFormat();
      Map<EncodeHintType, Object> hints = opts.getHintType().getHints();
      return mfw.encode(opts.getContent(), format, opts.getWidth(), opts.getHeight(), hints);
    } catch (WriterException e) {
      throw new QRCodeException(e);
    }
  }


  public static BufferedImage bufferedImage(QRCodeOptions opts, BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int color;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        color = matrix.get(x, y) ? opts.getColor() : opts.getBgColor();
        image.setRGB(x, y, color);
      }
    }
    return image;
  }

  public static BufferedImage bufferedImage(QRCodeOptions opts) {
    // 获取图片的点图矩阵
    return bufferedImage(opts, bitMatrix(opts));
  }

  /**
   * 嵌入图标
   *
   * @param opts    二维码配置
   * @param source 原图
   */
  static void insetIcon(QRCodeOptions opts, BufferedImage source) {
    try {
      if (opts.getIconInput() == null) {
        return;
      }

      Image iconImage = ImageIO.read(opts.getIconInput());
      int width = iconImage.getWidth(null);
      int height = iconImage.getHeight(null);

      // 压缩icon
      if (opts.isCompress()) {
        width = Math.min(width, opts.getIconWidth());
        height = Math.min(height, opts.getIconHeight());

        Image tempImage = iconImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Graphics g = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics();
        // 绘制缩小后的图
        g.drawImage(tempImage, 0, 0, null);
        g.dispose();
        iconImage = tempImage;
      }

      // 插入图标
      Graphics2D graph = source.createGraphics();
      int x = (opts.getWidth() - width) / 2;
      int y = (opts.getHeight() - height) / 2;
      graph.drawImage(iconImage, x, y, width, height, null);
      Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
      graph.setStroke(new BasicStroke(3.0f));
      graph.draw(shape);
      graph.dispose();
    } catch (IOException e) {
      throw new QRCodeException(e);
    }
  }

  /**
   * 自定义二维码白边宽度
   *
   * @param matrix 原始位图矩阵
   * @param margin 外边距
   * @return 返回新的位图矩阵
   */
  public static BitMatrix updateBit(BitMatrix matrix, int margin) {
    // 必须大于-1, 且不能超过原始图片宽高的二分之一
    if (margin < 0 || margin > matrix.getHeight() / 2 || margin > matrix.getWidth() / 2) {
      return matrix;
    }
    // 获取二维码图案的属性
    int[] rectangle = matrix.getEnclosingRectangle();
    int newWidth = rectangle[2] + margin * 2;
    int newHeight = rectangle[3] + margin * 2;
    // 按照自定义边框生成新的BitMatrix
    BitMatrix newMatrix = new BitMatrix(newWidth, newHeight);
    newMatrix.clear();
    // 循环，将二维码图案绘制到新的bitMatrix中
    int paddingW = newWidth - margin;
    int paddingH = newHeight - margin;
    for (int i = margin; i < paddingW; i++) {
      for (int j = margin; j < paddingH; j++) {
        if (matrix.get(i - margin + rectangle[0], j - margin + rectangle[1])) {
          newMatrix.set(i, j);
        }
      }
    }
    return newMatrix;
  }

  /**
   * 图片放大缩小
   *
   * @param srcImage 原图
   * @param width    缩放的宽度
   * @param height   缩放的高度
   * @return 返回缩放的新图
   */
  public static BufferedImage zoomInImage(BufferedImage srcImage, int width, int height) {
    BufferedImage newImage = new BufferedImage(width, height, srcImage.getType());
    Graphics g = newImage.getGraphics();
    g.drawImage(srcImage, 0, 0, width, height, null);
    g.dispose();
    return newImage;
  }

}
