package com.benefitj.jpuppeteer.chromium;


/**
 *
 */
@ChromiumApi("DeviceOrientation")
public interface DeviceOrientation {

  /**
   * Clears the overridden Device Orientation.
   */
  void clearDeviceOrientationOverride();

  /**
   * @param alpha number
   *              Mock alpha
   * @param beta  number
   *              Mock beta
   * @param gamma number
   *              Mock gamma
   */
  void setDeviceOrientationOverride(Number alpha, Number beta, Number gamma);

}
