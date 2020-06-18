package com.benefitj.interpolator;

/**
 * 采样率的插值器
 */
public abstract class Interpolator<S, D> {

  /**
   * 原始采样率
   */
  private int srcSize;
  /**
   * 目标采样率
   */
  private int destSize;
  /**
   * 插值的长度
   */
  private int length;
  /**
   * 原始采样比： total.length / dest.length
   */
  private int srcRatio;
  /**
   * 目标采样比： total.length / src.length
   */
  private int destRatio;

  /**
   * 临时存储元数据
   */
  private final ThreadLocal<S> srcLocal = new ThreadLocal<>();
  /**
   * 临时存储目标数据
   */
  private final ThreadLocal<D> destLocal = new ThreadLocal<>();

  public Interpolator(int srcSize, int destSize) {
    this.srcSize = srcSize;
    this.destSize = destSize;

    int length = (this.length = lcm(srcSize, destSize));
    this.setLength(length);
    this.setSrcRatio(length / srcSize);
    this.setDestRatio(length / destSize);
  }

  /**
   * 处理
   *
   * @param src 原数据
   */
  public D process(S src) {
    return process(src, getLength(), getSrcRatio(), getDestRatio());
  }

  /**
   * 处理
   *
   * @param src       原数据
   * @param length    长度
   * @param srcRatio  原始采样比： total.length / dest.length
   * @param destRatio 目标采样比： total.length / src.length
   */
  public abstract D process(S src, int length, int srcRatio, int destRatio);

  public ThreadLocal<S> getSrcLocal() {
    return srcLocal;
  }

  public S getLocalSrc() {
    return srcLocal.get();
  }

  public void setLocalSrc(S src) {
    if (src != null) {
      getSrcLocal().set(src);
    } else {
      getSrcLocal().remove();
    }
  }

  public ThreadLocal<D> getDestLocal() {
    return destLocal;
  }

  public D getLocalDest() {
    return getDestLocal().get();
  }

  public void setLocalDest(D dest) {
    if (dest != null) {
      getDestLocal().set(dest);
    } else {
      getDestLocal().remove();
    }
  }

  public int getSrcSize() {
    return srcSize;
  }

  public void setSrcSize(int srcSize) {
    this.srcSize = srcSize;
  }

  public int getDestSize() {
    return destSize;
  }

  public void setDestSize(int destSize) {
    this.destSize = destSize;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getSrcRatio() {
    return srcRatio;
  }

  public void setSrcRatio(int srcRatio) {
    this.srcRatio = srcRatio;
  }

  public int getDestRatio() {
    return destRatio;
  }

  public void setDestRatio(int destRatio) {
    this.destRatio = destRatio;
  }

  /**
   * 求两个数的最大公约数
   *
   * @param num1 值-1
   * @param num2 值-2
   * @return 返回计算的最大公约数
   */
  public static int gcd(int num1, int num2) {
    int tmp = 0;
    int n1 = Math.max(num1, num2);
    int n2 = Math.min(num1, num2);
    while (n1 % n2 != 0) {
      tmp = n1 % n2;
      n1 = n2;
      n2 = tmp;
    }
    return tmp;
  }

  /**
   * 求两个数的最小公倍数
   *
   * @param num1 值-1
   * @param num2 值-2
   * @return 返回计算的最小公倍数
   */
  public static int lcm(int num1, int num2) {
    int tmp;
    int n1 = Math.max(num1, num2);
    int n2 = Math.min(num1, num2);
    while (n1 % n2 != 0) {
      tmp = n1 % n2;
      n1 = n2;
      n2 = tmp;
    }
    return (num1 * num2) / n2;
  }

}
