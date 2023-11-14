package com.benefitj.interpolator;

/**
 * 短整数插值器
 */
public class ShortInterpolator extends ArrayInterpolator<short[], short[], short[]> {

  public ShortInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public ShortInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
  }

  @Override
  protected short[] createBuffer(int length) {
    return new short[length];
  }

  @Override
  protected short[] createDest(int destFrequency) {
    return new short[destFrequency];
  }

  /**
   * 加值
   *
   * @param src   原数据
   * @param buf  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(short[] src, short[] buf, int ratio) {
    int index = 0;
    for (short v : src) {
      for (int i = 0; i < ratio; i++) {
        buf[index++] = v;
      }
    }
  }

  /**
   * 减值
   *
   * @param dest  目标数据
   * @param buf  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void decelerate(short[] dest, short[] buf, int ratio) {
    for (int i = 0, j = 0; i < buf.length; i += ratio, j++) {
      dest[j] = buf[i];
    }
  }

}
