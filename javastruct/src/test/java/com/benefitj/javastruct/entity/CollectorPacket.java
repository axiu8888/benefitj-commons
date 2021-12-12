package com.benefitj.javastruct.entity;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.JavaStructManager;
import com.benefitj.javastruct.convert.HexStringConverter;

@JavaStructClass
public class CollectorPacket {
  public static void main(String[] args) {
    String data = "";


  }

  /**
   * head， [0, 1]
   */
  @JavaStructField(size = 2, converter = HexStringConverter.class)
  private String head;
  /**
   * [2]
   */
  @JavaStructField(size = 1)
  private int packageType;

  /**
   * [3, 4]
   */
  @JavaStructField(size = 2)
  private int length;

  /**
   * [5]
   */
  @JavaStructField(size = 1)
  private int orderNum;

  /**
   * 设备ID, [6, 7, 8, 9]
   */
  @JavaStructField(size = 4, converter = HexStringConverter.class)
  private String deviceId;

  /**
   * 包序号, [10, 11, 12, 13]
   */
  @JavaStructField(size = 4)
  private int packageSn;

  /**
   * 胸呼吸, [14, ..., 63]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  private int[] respList;

  /**
   * 腹呼吸, [64, ..., 113]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  private int[] abdominalList;

  /**
   * 心电波形, [114, ..., 513]
   */
  @JavaStructField(size = 2, arrayLength = 200)
  private int[] ecgList;

  /**
   * 血氧波形, [514, ..., 563]
   */
  @JavaStructField(size = 1, arrayLength = 50)
  private int[] spo2List;

  /**
   * 心率, [564, 565]
   */
  @JavaStructField(size = 2)
  private int heartRate;
  /**
   * 呼吸率, [566]
   */
  @JavaStructField(size = 1)
  private int respRate;
  /**
   * 脉率, [567, 568]
   */
  @JavaStructField(size = 2)
  private int pulseRate;
  /**
   * 体温, [569, 570]
   */
  @JavaStructField(size = 2)
  private int temperature;
  /**
   * 血氧, [571]
   */
  @JavaStructField(size = 1)
  private int spo2;
  /**
   * 外部电池, [572]
   */
  @JavaStructField(size = 1)
  private int outBattery;
  /**
   * 连接异常, [573]
   */
  @JavaStructField(size = 1)
  private byte connAbnormal;
  /**
   * signal异常, [574]
   */
  @JavaStructField(size = 1)
  private byte signalAbnormal;
  /**
   * 其他异常, [575]
   */
  @JavaStructField(size = 1)
  private byte otherAbnormal;

  /**
   * 体位, [576]
   */
  @JavaStructField(size = 1)
  private int gesture;
  /**
   * wifi信号, [577]
   */
  @JavaStructField(size = 1)
  private int wifiSignal;
  /**
   * 肺康复，是否校准,用于在pad端显示校准过的还是呼吸波形, [578]
   */
  @JavaStructField(size = 1)
  private int calibration;
  /**
   * 实时呼吸比, [579]
   */
  @JavaStructField(size = 1)
  private int eiRatio;
  /**
   * 实时胸腹呼吸共享比, [580]
   */
  @JavaStructField(size = 1)
  private int caRatio;
  /**
   * 潮气量, [581, ..., 630]
   */
  @JavaStructField(size = 2, arrayLength = 25)
  private int[] tidalVolumeList;
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
  private int heartRateAlarm;
  /**
   * 忽略, [632, 633, 634]
   */
  @JavaStructField(size = 3, converter = HexStringConverter.class)
  private String ignore1;
  /**
   * 加速度, [635]
   */
  @JavaStructField(size = 1)
  private int acceleration;
  /**
   * 时间, [636, 637, 638, 639]
   */
  @JavaStructField(size = 4)
  private long time;
  /**
   * 血压时间, [640, 641, 642, 643]
   */
  @JavaStructField(size = 4)
  private long bpTime;
  /**
   * 收缩压, [644, 645]
   */
  @JavaStructField(size = 2)
  private int systolic;
  /**
   * 舒张压, [646, 647]
   */
  @JavaStructField(size = 2)
  private int diastolic;
  /**
   * 忽略, [648, 649, 650]
   */
  @JavaStructField(size = 3, converter = HexStringConverter.class)
  private String ignore2;
  /**
   * spo2电池电量, [651]
   */
  @JavaStructField(size = 1)
  private int spo2Battery;

  public String getHead() {
    return head;
  }

  public void setHead(String head) {
    this.head = head;
  }

  public int getPackageType() {
    return packageType;
  }

  public void setPackageType(int packageType) {
    this.packageType = packageType;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(int orderNum) {
    this.orderNum = orderNum;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public int getPackageSn() {
    return packageSn;
  }

  public void setPackageSn(int packageSn) {
    this.packageSn = packageSn;
  }

  public int[] getRespList() {
    return respList;
  }

  public void setRespList(int[] respList) {
    this.respList = respList;
  }

  public int[] getAbdominalList() {
    return abdominalList;
  }

  public void setAbdominalList(int[] abdominalList) {
    this.abdominalList = abdominalList;
  }

  public int[] getEcgList() {
    return ecgList;
  }

  public void setEcgList(int[] ecgList) {
    this.ecgList = ecgList;
  }

  public int[] getSpo2List() {
    return spo2List;
  }

  public void setSpo2List(int[] spo2List) {
    this.spo2List = spo2List;
  }

  public int getHeartRate() {
    return heartRate;
  }

  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  public int getRespRate() {
    return respRate;
  }

  public void setRespRate(int respRate) {
    this.respRate = respRate;
  }

  public int getPulseRate() {
    return pulseRate;
  }

  public void setPulseRate(int pulseRate) {
    this.pulseRate = pulseRate;
  }

  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public int getSpo2() {
    return spo2;
  }

  public void setSpo2(int spo2) {
    this.spo2 = spo2;
  }

  public int getOutBattery() {
    return outBattery;
  }

  public void setOutBattery(int outBattery) {
    this.outBattery = outBattery;
  }

  public byte getConnAbnormal() {
    return connAbnormal;
  }

  public void setConnAbnormal(byte connAbnormal) {
    this.connAbnormal = connAbnormal;
  }

  public byte getSignalAbnormal() {
    return signalAbnormal;
  }

  public void setSignalAbnormal(byte signalAbnormal) {
    this.signalAbnormal = signalAbnormal;
  }

  public byte getOtherAbnormal() {
    return otherAbnormal;
  }

  public void setOtherAbnormal(byte otherAbnormal) {
    this.otherAbnormal = otherAbnormal;
  }

  public int getGesture() {
    return gesture;
  }

  public void setGesture(int gesture) {
    this.gesture = gesture;
  }

  public int getWifiSignal() {
    return wifiSignal;
  }

  public void setWifiSignal(int wifiSignal) {
    this.wifiSignal = wifiSignal;
  }

  public int getCalibration() {
    return calibration;
  }

  public void setCalibration(int calibration) {
    this.calibration = calibration;
  }

  public int getEiRatio() {
    return eiRatio;
  }

  public void setEiRatio(int eiRatio) {
    this.eiRatio = eiRatio;
  }

  public int getCaRatio() {
    return caRatio;
  }

  public void setCaRatio(int caRatio) {
    this.caRatio = caRatio;
  }

  public int[] getTidalVolumeList() {
    return tidalVolumeList;
  }

  public void setTidalVolumeList(int[] tidalVolumeList) {
    this.tidalVolumeList = tidalVolumeList;
  }

  public int getHeartRateAlarm() {
    return heartRateAlarm;
  }

  public void setHeartRateAlarm(int heartRateAlarm) {
    this.heartRateAlarm = heartRateAlarm;
  }

  public String getIgnore1() {
    return ignore1;
  }

  public void setIgnore1(String ignore1) {
    this.ignore1 = ignore1;
  }

  public int getAcceleration() {
    return acceleration;
  }

  public void setAcceleration(int acceleration) {
    this.acceleration = acceleration;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getBpTime() {
    return bpTime;
  }

  public void setBpTime(long bpTime) {
    this.bpTime = bpTime;
  }

  public int getSystolic() {
    return systolic;
  }

  public void setSystolic(int systolic) {
    this.systolic = systolic;
  }

  public int getDiastolic() {
    return diastolic;
  }

  public void setDiastolic(int diastolic) {
    this.diastolic = diastolic;
  }

  public String getIgnore2() {
    return ignore2;
  }

  public void setIgnore2(String ignore2) {
    this.ignore2 = ignore2;
  }

  public int getSpo2Battery() {
    return spo2Battery;
  }

  public void setSpo2Battery(int spo2Battery) {
    this.spo2Battery = spo2Battery;
  }
}
