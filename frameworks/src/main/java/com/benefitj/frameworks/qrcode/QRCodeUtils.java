package com.benefitj.frameworks.qrcode;

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

  /**
   * 创建QRCode实例
   *
   * @param content 二维码的文本内容
   * @return 返回创建的QRCode
   */
  public static QRCodeOptions newOptions(String content) {
    return QRCodeOptions.create(content);
  }

  /**
   * 创建QRCode实例
   *
   * @param content 二维码的文本内容
   * @param size    二维码的大小
   * @return 返回创建的QRCode
   */
  public static QRCodeOptions newOptions(String content, int size) {
    return QRCodeOptions.create(content, size);
  }

  /**
   * 生成二维码
   */
  public static byte[] encodeQuietly(String content) {
    try {
      return encode(content);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   */
  public static byte[] encodeQuietly(QRCodeOptions options) {
    try {
      return encode(options);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   */
  public static void encodeQuietly(String content, File destFile) {
    try {
      encode(content, destFile);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   *
   * @param options  可选项参数
   * @param destFile 输出的目标文件
   */
  public static void encodeQuietly(QRCodeOptions options, File destFile) {
    try {
      encode(options, destFile);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   *
   * @param content 二维码内容
   * @param out     输出流
   */
  public static void encodeQuietly(String content, OutputStream out) {
    try {
      encode(content, out);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   */
  public static void encodeQuietly(QRCodeOptions options, OutputStream out) {
    try {
      encode(options, out);
    } catch (WriterException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 生成二维码
   *
   * @param content 文本内容
   * @return 返回生成的二维码字节数组
   */
  public static byte[] encode(String content) throws WriterException, IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    encode(content, output);
    return output.toByteArray();
  }

  /**
   * 生成二维码
   *
   * @param options 二维码配置
   * @return 返回生成的二维码字节数组
   */
  public static byte[] encode(QRCodeOptions options) throws WriterException, IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    encode(options, output);
    return output.toByteArray();
  }

  /**
   * 创建二维码到指定文件
   *
   * @param content  二维码内容
   * @param destFile 目标文件
   */
  public static void encode(String content, File destFile) throws IOException, WriterException {
    QRCodeOptions qrCode = newOptions(content);
    encode(qrCode, destFile);
  }

  /**
   * 创建二维码到指定文件
   *
   * @param options  二维码配置
   * @param destFile 目标文件
   */
  public static void encode(QRCodeOptions options, File destFile) throws IOException, WriterException {
    BufferedImage image = bufferedImage(options);
    // 嵌入图标
    insetIcon(options, image);
    if (!destFile.exists()) {
      destFile.getParentFile().mkdirs();
      destFile.createNewFile();
    }
    ImageIO.write(image, options.getFormatName(), destFile);
  }

  /**
   * 创建二维码
   *
   * @param content 二维码内容
   * @param os      输出流
   */
  public static void encode(String content, OutputStream os) throws WriterException, IOException {
    QRCodeOptions qrCode = newOptions(content);
    encode(qrCode, os);
  }

  /**
   * 创建二维码
   *
   * @param options 二维码配置
   * @param os      输出流
   */
  public static void encode(QRCodeOptions options, OutputStream os) throws WriterException, IOException {
    BufferedImage image = bufferedImage(options);
    // 嵌入图标
    insetIcon(options, image);
    ImageIO.write(image, options.getFormatName(), os);
  }

  /**
   * 解码
   *
   * @param input 文件
   * @return 返回解码后的文本
   * @throws IOException       IO错误
   * @throws NotFoundException 文件找不到
   */
  public static String decode(File input) throws IOException, NotFoundException {
    BufferedImage image = ImageIO.read(input);
    return decodeResult(image).getText();
  }

  /**
   * 解码
   *
   * @param input 二维码的输入流
   * @return 返回解码后的文本
   * @throws IOException       IO异常
   * @throws NotFoundException 文件找不到
   */
  public static String decode(InputStream input) throws IOException, NotFoundException {
    BufferedImage image = ImageIO.read(input);
    return decodeResult(image).getText();
  }

  /**
   * 解码
   *
   * @param input 二维码的输入流
   * @return 返回解码后的文本
   */
  public static String decodeQuietly(InputStream input) {
    try {
      return decode(input);
    } catch (NotFoundException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 解码
   *
   * @param input 二维码文件
   * @return 返回解码后的文本
   */
  public static String decodeQuietly(File input) {
    try {
      return decode(input);
    } catch (NotFoundException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 解码
   *
   * @param image 图片
   * @return 结果
   * @throws NotFoundException 文件找不到
   */
  public static Result decodeResult(BufferedImage image) throws NotFoundException {
    return decodeResult(image, DEFAULT_DECODE_HINTS);
  }

  /**
   * 解码
   *
   * @param image 图片
   * @param hints 解码提示
   * @return 返回解码的结果
   * @throws NotFoundException 文件找不到
   */
  public static Result decodeResult(BufferedImage image, Map<DecodeHintType, Object> hints) throws NotFoundException {
    BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
    return new MultiFormatReader().decode(bitmap, hints);
  }

  /**
   * @param options
   * @return
   * @throws WriterException
   */
  public static BitMatrix bitMatrix(QRCodeOptions options) throws WriterException {
    MultiFormatWriter formatWriter = new MultiFormatWriter();
    BarcodeFormat format = options.getBarcodeFormat();
    Map<EncodeHintType, Object> hints = options.getHintType().getHints();
    return formatWriter.encode(options.getContent(), format, options.getWidth(), options.getHeight(), hints);
  }

  /**
   * 创建BufferedImage
   */
  public static BufferedImage bufferedImage(QRCodeOptions options, BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int color;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        color = matrix.get(x, y) ? options.getColor() : options.getBgColor();
        image.setRGB(x, y, color);
      }
    }
    return image;
  }

  /**
   * 创建BufferedImage
   */
  public static BufferedImage bufferedImage(QRCodeOptions options) throws WriterException {
    // 获取图片的点图矩阵
    BitMatrix matrix = bitMatrix(options);
    return bufferedImage(options, matrix);
  }

  /**
   * 嵌入图标
   *
   * @param options 二维码配置
   * @param source  原图
   * @throws IOException IO异常
   */
  public static void insetIcon(QRCodeOptions options, BufferedImage source) throws IOException {
    if (options.getIconInput() == null) {
      return;
    }

    Image iconImage = ImageIO.read(options.getIconInput());
    int width = iconImage.getWidth(null);
    int height = iconImage.getHeight(null);

    // 压缩icon
    if (options.isCompress()) {
      width = Math.min(width, options.getIconWidth());
      height = Math.min(height, options.getIconHeight());

      Image tempImage = iconImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      Graphics g = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).getGraphics();
      // 绘制缩小后的图
      g.drawImage(tempImage, 0, 0, null);
      g.dispose();
      iconImage = tempImage;
    }

    // 插入图标
    Graphics2D graph = source.createGraphics();
    int x = (options.getWidth() - width) / 2;
    int y = (options.getHeight() - height) / 2;
    graph.drawImage(iconImage, x, y, width, height, null);
    Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
    graph.setStroke(new BasicStroke(3.0f));
    graph.draw(shape);
    graph.dispose();
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
