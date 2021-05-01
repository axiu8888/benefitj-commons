package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单个导联波形数据
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JavaStructClass
public class LeadWave {

  /**
   * 时间
   */
  @JavaStructField(size = 4)
  private long time;
  /**
   * 导联状态和PCB參數
   */
  @JavaStructField(size = 1, arrayLength = 2)
  private byte[] state;
  /**
   * 波形数据, 200采样率，short2个字节
   */
  @JavaStructField(size = 2, arrayLength = 200)
  private short[] wave;

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public byte[] getState() {
    return state;
  }

  public void setState(byte[] state) {
    this.state = state;
  }

  public short[] getWave() {
    return wave;
  }

  public void setWave(short[] wave) {
    this.wave = wave;
  }
}
