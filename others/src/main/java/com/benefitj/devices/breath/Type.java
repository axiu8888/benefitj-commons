package com.benefitj.devices.breath;

public enum Type {
  COMPILE_TIME(0, 4, long.class, "程序编译时间", "", true),
  SOFTWARE_VERSION(2, 4, String.class, "程序版本", "", true),
  // 预留(4~5)
  //IGNORE1(4, 0, String.class, "预留(4~5)", "", false),
  REALTIME_PRESSURE(6, 4, float.class, "实时压力", "", true),
  INSTRUMENT_STATUS(8, 2, int.class, "仪器状态", "", true),
  // 预留(9)
  //IGNORE2(9, 0, String.class, "预留(9)", "", false),

  /*-------------------------------------*/
  // 呼气肌力评估 10~17
  /**
   * 0 初始化(每次开始)
   * 1 第一次
   * 2 第二次
   * 3 第三次(维持 3 秒)
   * 4 的时候为结果
   */
  EXHALE_ASSESS_TIMES(10, 2, int.class, "评估次数", "呼气肌力评估", true),
  EXHALE_ASSESS_TIME(11, 2, int.class, "呼吸时间(秒)", "呼气肌力评估", true),
  EXHALE_ASSESS_MAX_PRESSURE(12, 4, float.class, "最大压力", "呼气肌力评估", true),
  EXHALE_ASSESS_REALTIME_PRESSURE(14, 4, float.class, "实时压力", "呼气肌力评估", true),
  /**
   * 第一次评估、第二次评估、第三次(维持 3秒)
   * 5的时候为结果
   * 7 的时候为最终结果
   */
  EXHALE_ASSESS_STATUS(16, 2, int.class, "评估状态", "呼气肌力评估", true),

  // 预留(17)

  /*-------------------------------------*/
  // 吸气肌力评估 18~24
  /**
   * 0 初始化(每次开始)
   * 1 第一次
   * 2 第二次
   * 3 第三次(维持 3 秒)
   * 4 的时候为结果
   */
  INHALE_ASSESS_TIMES(18, 2, int.class, "评估次数", "吸气肌力评估", true),
  INHALE_ASSESS_TIME(19, 2, int.class, "呼气时间(秒)", "吸气肌力评估", true),
  INHALE_ASSESS_MAX_PRESSURE(20, 4, float.class, "最大压力", "吸气肌力评估", true),
  INHALE_ASSESS_REALTIME_PRESSURE(22, 4, float.class, "实时压力", "吸气肌力评估", true),
  /**
   * 第一次评估、第二次评估、第三次(维持 3秒)
   * 5的时候为结果
   * 7 的时候为最终结果
   */
  INHALE_ASSESS_STATUS(24, 2, int.class, "仪器状态", "吸气肌力评估", true),


  // 预留(25~61)

  /*-------------------------------------*/
  // 呼气肌力训练 62~68
  EXHALE_TRAIN_TIMES(62, 2, int.class, "训练次数", "呼气肌力训练", true),
  EXHALE_TRAIN_DURATION(63, 2, int.class, "训练时长(秒)", "呼气肌力训练", true),
  EXHALE_TRAIN_RESISTANCE(64, 2, int.class, "训练阻抗", "呼气肌力训练", true),
  EXHALE_TRAIN_REALTIME_PRESSURE(65, 4, float.class, "实时压力", "呼气肌力训练", true),
  /**
   * 0 训练准备
   * 1 仪器就绪
   * 2 正在训练
   * 3 训练结束
   */
  EXHALE_TRAIN_STEP(67, 2, int.class, "当前步骤", "呼气肌力训练", true),
  EXHALE_TRAIN_TIME(68, 2, int.class, "单次呼气时间(秒)", "呼气肌力训练", true),


  /*-------------------------------------*/
  // 吸气肌力训练 69~75
  INHALE_TRAIN_TIMES(69, 2, int.class, "训练次数", "吸气肌力训练", true),
  INHALE_TRAIN_DURATION(70, 2, int.class, "训练时长(秒)", "吸气肌力训练", true),
  INHALE_TRAIN_RESISTANCE(71, 2, int.class, "训练阻抗", "吸气肌力训练", true),
  INHALE_TRAIN_REALTIME_PRESSURE(72, 4, float.class, "实时压力", "吸气肌力训练", true),
  /**
   * 0 训练准备
   * 1 仪器就绪
   * 2 正在训练
   * 3 训练结束
   */
  INHALE_TRAIN_STEP(74, 2, int.class, "当前步骤", "吸气肌力训练", true),
  INHALE_TRAIN_TIME(75, 2, int.class, "单次吸气时间(秒)", "吸气肌力训练", true),


  /*-------------------------------------*/
  // 缩唇训练 76~80
  LIP_GIRDLE_TRAIN_TIMES(76, 2, int.class, "训练次数", "缩唇训练", true),
  LIP_GIRDLE_TRAIN_DURATION(77, 2, int.class, "训练时长(秒)", "缩唇训练", true),
  LIP_GIRDLE_TRAIN_GEARS(78, 2, int.class, "训练档位", "缩唇训练", true),
  LIP_GIRDLE_TRAIN_REALTIME_PRESSURE(79, 4, float.class, "实时压力", "缩唇训练", true),
  /**
   * 0 训练准备
   * 1 仪器就绪
   * 2 正在训练
   * 3 训练结束
   */
  LIP_GIRDLE_TRAIN_STEP(80, 2, int.class, "当前步骤", "缩唇训练", true),


  /*-------------------------------------*/

  /**
   * 0 退出任何评估训练模式
   * 111 进入呼气力估
   * 112 进入吸气肌力估
   * 121 入呼气力
   * 122 进入吸气肌力训练
   * 123 进入缩唇呼吸训练
   */
  MODE(100, 2, int.class, "模式选择", "", true),

  // 预留 (101~149)

  EXHALE_W(150, 1, int.class, "呼气肌力训练阻抗", "", false),
  INHALE_W(151, 1, int.class, "呼气肌力训练阻抗", "", false),
  LIP_GIRDLE_W(152, 1, int.class, "缩唇训练训练档位", "", false),

  ;

  private final int address;
  private final int len;
  private final Class<?> klass;
  private final String descriptor;
  private final String item;
  private final boolean notify;

  Type(int address, int len, Class<?> klass, String descriptor, String item, boolean notify) {
    this.address = address;
    this.len = len;
    this.klass = klass;
    this.descriptor = descriptor;
    this.item = item;
    this.notify = notify;
  }

  public int getAddress() {
    return address;
  }

  public int getLen() {
    return len;
  }

  public Class<?> getKlass() {
    return klass;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public String getItem() {
    return item;
  }

  public boolean isNotify() {
    return notify;
  }

  public static Type of(int type) {
    for (Type v : values()) {
      if (v.address == type) {
        return v;
      }
    }
    return null;
  }

}
