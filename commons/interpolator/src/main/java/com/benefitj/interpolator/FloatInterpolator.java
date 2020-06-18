package com.benefitj.interpolator;

/**
 * 浮点数插值器
 */
public class FloatInterpolator extends ArrayInterpolator<float[], float[], float[]> {

  public FloatInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public FloatInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
  }

  @Override
  protected float[] createBuffer(int length) {
    return new float[length];
  }

  @Override
  protected float[] createDest(int destFrequency) {
    return new float[destFrequency];
  }

  /**
   * 加值
   *
   * @param src   原数据
   * @param buff  临时缓冲区
   * @param ratio 比率
   */
  @Override
  public void accelerate(float[] src, float[] buff, int ratio) {
    int index = 0;
    for (float v : src) {
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
  public void decelerate(float[] dest, float[] buff, int ratio) {
    for (int i = 0, j = 0; i < buff.length; i += ratio, j++) {
      dest[j] = buff[i];
    }
  }

}
