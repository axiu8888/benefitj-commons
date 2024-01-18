package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 单个导联波形数据
 */
@SuperBuilder
@NoArgsConstructor
@Data
@JavaStructClass
public class LeadWave {

  /**
   * 时间
   */
  @JavaStructField(size = 4)
  long time;
  /**
   * 导联状态和PCB參數
   */
  @JavaStructField(size = 1, arrayLength = 2)
  byte[] state;
  /**
   * 波形数据, 200采样率，short2个字节
   */
  @JavaStructField(size = 2, arrayLength = 200)
  short[] wave;

}
