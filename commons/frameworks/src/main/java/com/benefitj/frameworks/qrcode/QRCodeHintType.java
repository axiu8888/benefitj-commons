package com.benefitj.frameworks.qrcode;

import com.google.zxing.EncodeHintType;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码需要的类型: 如编码格式、形状、辨识度、外边距等
 */
public class QRCodeHintType {

  private static volatile Map<EncodeHintType, Object> DEFAULT_HINTS;

  static {
    HashMap<EncodeHintType, Object> map = new HashMap<>();
    // 编码格式
    map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    // 形状: 正方形、长方形、无
    map.put(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_NONE);
    // 辨识度
    map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    // 外边距
    map.put(EncodeHintType.MARGIN, 1);

    DEFAULT_HINTS = Collections.unmodifiableMap(map);
  }

  /**
   * 创建QRCodeHintType对象
   */
  public static QRCodeHintType create() {
    return new QRCodeHintType();
  }

  /**
   *
   */
  private final Map<EncodeHintType, Object> hints = new HashMap<>();

  public QRCodeHintType() {
    hints.putAll(DEFAULT_HINTS);
  }

  /**
   * 获取编码
   */
  public String getCharset() {
    return get(EncodeHintType.CHARACTER_SET);
  }

  /**
   * 设置编码
   */
  public void setCharset(String charset) {
    set(EncodeHintType.CHARACTER_SET, charset);
  }

  /**
   * 获取形状
   */
  public SymbolShapeHint getShape() {
    return get(EncodeHintType.DATA_MATRIX_SHAPE);
  }

  /**
   * 设置形状
   */
  public void setShape(SymbolShapeHint shape) {
    set(EncodeHintType.DATA_MATRIX_SHAPE, shape);
  }

  /**
   * 获取外边距
   */
  public Integer getMargin(Integer margin) {
    return get(EncodeHintType.MARGIN);
  }

  /**
   * 设置外边距
   */
  public void setMargin(Integer margin) {
    set(EncodeHintType.MARGIN, margin);
  }

  /**
   * 获取EncodeHintType集合
   */
  public Map<EncodeHintType, Object> getHints() {
    return hints;
  }

  /**
   * 设置EncodeHintType值
   *
   * @param type  EncodeHintType类型
   * @param value 值
   */
  public final void set(EncodeHintType type, Object value) {
    if (type != null && value != null) {
      getHints().put(type, value);
    }
  }

  @SuppressWarnings("unchecked")
  public final <T> T get(EncodeHintType type) {
    return (T) getHints().get(type);
  }

}
