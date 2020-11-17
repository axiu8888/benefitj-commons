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
   * @param buff  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(short[] src, short[] buff, int ratio) {
    int index = 0;
    for (short v : src) {
      for (int i = 0; i < ratio; i++) {
        buff[index++] = v;
      }
    }
  }

  /**
   * 减值
   *
   * @param dest  目标数据
   * @param buff  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void decelerate(short[] dest, short[] buff, int ratio) {
    for (int i = 0, j = 0; i < buff.length; i += ratio, j++) {
      dest[j] = buff[i];
    }
  }

}
