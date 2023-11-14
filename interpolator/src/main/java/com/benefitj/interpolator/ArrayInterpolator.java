package com.benefitj.interpolator;

import com.benefitj.core.SingletonSupplier;

/**
 * 数组插值器
 */
public abstract class ArrayInterpolator<S, D, T> extends Interpolator<S, D> {

  private final SingletonSupplier<T> singleton = SingletonSupplier.of(() -> createBuffer(getLength()));

  private boolean cachedDest = false;
  private final ThreadLocal<D> localDestCache = ThreadLocal.withInitial(() -> createDest(getDestSize()));

  public ArrayInterpolator(int srcFrequency, int destFrequency) {
    super(srcFrequency, destFrequency);
  }

  public ArrayInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency);
    this.cachedDest = cachedDest;
  }

  @Override
  public D process(S src, int length, int srcRatio, int destRatio) {
    T buf = getBuffer();
    D dest = getDest(getDestSize());
    setLocalSrc(src);
    setLocalDest(dest);
    accelerate(src, buf, srcRatio);
    decelerate(dest, buf, destRatio);
    setLocalSrc(null);
    setLocalDest(null);
    return dest;
  }

  /**
   * 创建缓冲区
   *
   * @param length 缓冲区长度
   */
  protected abstract T createBuffer(int length);

  public T getBuffer() {
    return singleton.get();
  }

  public boolean isCachedDest() {
    return cachedDest;
  }

  public void setCachedDest(boolean cachedDest) {
    this.cachedDest = cachedDest;
  }

  public D getDest(int destFrequency) {
    return isCachedDest() ? localDestCache.get() : createDest(destFrequency);
  }

  /**
   * 创建目标数组
   *
   * @param destFrequency 目标频率
   * @return 返回创建的目标数组
   */
  protected abstract D createDest(int destFrequency);

  /**
   * 加值
   *
   * @param src   原数据
   * @param buf   临时缓冲区
   * @param ratio 比率
   */
  public abstract void accelerate(S src, T buf, int ratio);

  /**
   * 减值
   *
   * @param dest  目标数据
   * @param buf   临时缓冲区
   * @param ratio 比率
   */
  public abstract void decelerate(D dest, T buf, int ratio);

}
