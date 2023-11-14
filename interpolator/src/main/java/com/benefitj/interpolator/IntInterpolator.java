package com.benefitj.interpolator;

/**
 * 整数插值器
 */
public class IntInterpolator extends ArrayInterpolator<int[], int[], int[]> {

  public IntInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public IntInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
  }

  @Override
  protected int[] createBuffer(int length) {
    return new int[length];
  }

  @Override
  protected int[] createDest(int destFrequency) {
    return new int[destFrequency];
  }

  /**
   * 加值
   *
   * @param src   原数据
   * @param buf  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(int[] src, int[] buf, int ratio) {
    int index = 0;
    for (int v : src) {
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
  public void decelerate(int[] dest, int[] buf, int ratio) {
    for (int i = 0, j = 0; i < buf.length; i += ratio, j++) {
      dest[j] = buf[i];
    }
  }

}
