package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.convert.HexStringConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@JavaStructClass
public class CollectorPacket {

  /**
   * head， [0, 1]
   */
  @JavaStructField(size = 2, converter = HexStringConverter.class)
  String head;
  /**
   * [2]
   */
  @JavaStructField(size = 1)
  int packageType;

  /**
   * [3, 4]
   */
  @JavaStructField(size = 2)
  int length;

  /**
   * [5]
   */
  @JavaStructField(size = 1)
  int orderNum;

  /**
   * 设备ID, [6, 7, 8, 9]
   */
  @JavaStructField(size = 4, converter = HexStringConverter.class)
  String deviceId;

  /**
   * 包序号, [10, 11, 12, 13]
   */
  @JavaStructField(size = 4)
  int packageSn;

  /**
   * 胸呼吸, [14, ..., 63]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  int[] respList;

  /**
   * 腹呼吸, [64, ..., 113]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  int[] abdominalList;

  /**
   * 心电波形, [114, ..., 513]
   */
  @JavaStructField(size = 2, arrayLength = 200)
  int[] ecgList;

  /**
   * 血氧波形, [514, ..., 563]
   */
  @JavaStructField(size = 1, arrayLength = 50)
  int[] spo2List;

  /**
   * 心率, [564, 565]
   */
  @JavaStructField(size = 2)
  int heartRate;
  /**
   * 呼吸率, [566]
   */
  @JavaStructField(size = 1)
  int respRate;
  /**
   * 脉率, [567, 568]
   */
  @JavaStructField(size = 2)
  int pulseRate;
  /**
   * 体温, [569, 570]
   */
  @JavaStructField(size = 2)
  int temperature;
  /**
   * 血氧, [571]
   */
  @JavaStructField(size = 1)
  int spo2;
  /**
   * 外部电池, [572]
   */
  @JavaStructField(size = 1)
  int outBattery;
  /**
   * 连接异常, [573]
   */
  @JavaStructField(size = 1)
  byte connAbnormal;
  /**
   * signal异常, [574]
   */
  @JavaStructField(size = 1)
  byte signalAbnormal;
  /**
   * 其他异常, [575]
   */
  @JavaStructField(size = 1)
  byte otherAbnormal;

  /**
   * 体位, [576]
   */
  @JavaStructField(size = 1)
  int gesture;
  /**
   * wifi信号, [577]
   */
  @JavaStructField(size = 1)
  int wifiSignal;
  /**
   * 肺康复，是否校准,用于在pad端显示校准过的还是呼吸波形, [578]
   */
  @JavaStructField(size = 1)
  int calibration;
  /**
   * 实时呼吸比, [579]
   */
  @JavaStructField(size = 1)
  int eiRatio;
  /**
   * 实时胸腹呼吸共享比, [580]
   */
  @JavaStructField(size = 1)
  int caRatio;
  /**
   * 潮气量, [581, ..., 630]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  int[] tidalVolumeList;
  /**
   * 心率告警, [631]
   * <p>
   * NORMAL_SINUS_RHYTHM = prepare_1, // 窦性心律
   * SINUS_TACHYCARDIA = prepare_2, // 窦性心动过速
   * SINUS_BRADYCARDIA = 3, // 窦性心动过缓
   * SUPRAVENTRICULAR_PREMATURE_CONTRACTION = 4, // 室上性期前收缩
   * PAC_BIGEMINY = 5, // 室上性期前收缩二联律
   * PAC_TRIGEMINY = 6, // 室上性期前收缩三联律
   * PAIR_PAC = 7, // 成对室上性期前收缩
   * SHORT_TUN = 8, // 短阵室上性心动过速
   * ATRIAL_FIBRILLATION = 9, // 心房颤动
   * ATRIAL_FLUTTER = 10, // 心房扑动
   * PREMATURE_VENTRICULAR_CONTRACTION = 11, // 室性期前收缩
   * PVC_BIGEMINY = 12, // 室性期前收缩二联律
   * PVC_TRIGEMINY = 13, // 室性期前收缩三联律
   * PAIR_PVC = 14, // 成对室性期前收缩
   * VENTRICULAR_TACHYCARDIA = 15, // 室性心动过速
   * VENTRICULAR_FIBRILLATION = 16, // 室颤
   * LONG_RR_INTERVAL = 17, // 长RR间期
   * BEAT_STOP = 18, // 停搏
   */
  @JavaStructField(size = 1)
  int heartRateAlarm;
  /**
   * 忽略, [632, 633, 634]
   */
  @JavaStructField(size = 3, converter = HexStringConverter.class)
  String ignore1;
  /**
   * 加速度, [635]
   */
  @JavaStructField(size = 1)
  int acceleration;
  /**
   * 时间, [636, 637, 638, 639]
   */
  @JavaStructField(size = 4)
  long time;
  /**
   * 血压时间, [640, 641, 642, 643]
   */
  @JavaStructField(size = 4)
  long bpTime;
  /**
   * 收缩压, [644, 645]
   */
  @JavaStructField(size = 2)
  int systolic;
  /**
   * 舒张压, [646, 647]
   */
  @JavaStructField(size = 2)
  int diastolic;
  /**
   * 忽略, [648, 649, 650]
   */
  @JavaStructField(size = 3, converter = HexStringConverter.class)
  String ignore2;
  /**
   * spo2电池电量, [651]
   */
  @JavaStructField(size = 1)
  int spo2Battery;

}
