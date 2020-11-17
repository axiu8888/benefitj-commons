package com.benefitj.interpolator;

/**
 * 短整数插值器
 */
public class DoubleInterpolator extends ArrayInterpolator<double[], double[], double[]> {

  public DoubleInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public DoubleInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
  }

  @Override
  protected double[] createBuffer(int length) {
    return new double[length];
  }

  @Override
  protected double[] createDest(int destFrequency) {
    return new double[destFrequency];
  }

  /**
   * 加值
   *
   * @param src   原数据
   * @param buff  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(double[] src, double[] buff, int ratio) {
    int index = 0;
    for (double v : src) {
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
  public void decelerate(double[] dest, double[] buff, int ratio) {
    for (int i = 0, j = 0; i < buff.length; i += ratio, j++) {
      dest[j] = buff[i];
    }
  }

}
