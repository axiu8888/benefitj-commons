package com.benefitj.interpolator;

/**
 * 短整数插值器
 */
public class LongInterpolator extends ArrayInterpolator<long[], long[], long[]> {

  public LongInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public LongInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
  }

  @Override
  protected long[] createBuffer(int length) {
    return new long[length];
  }

  @Override
  protected long[] createDest(int destFrequency) {
    return new long[destFrequency];
  }

  /**
   * 加值
   *
   * @param src   原数据
   * @param buff  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(long[] src, long[] buff, int ratio) {
    int index = 0;
    for (long v : src) {
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
  public void decelerate(long[] dest, long[] buff, int ratio) {
    for (int i = 0, j = 0; i < buff.length; i += ratio, j++) {
      dest[j] = buff[i];
    }
  }

}
