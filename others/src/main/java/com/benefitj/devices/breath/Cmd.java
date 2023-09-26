package com.benefitj.devices.breath;

/**
 * 指令格式
 */
public enum Cmd {
  compile_time(Type.COMPILE_TIME, "编译时间", 0, 0, 0, 2, false),
  software_version(Type.SOFTWARE_VERSION, "编译版本", 0, 0, 0, 2, false),
  pressure(Type.REALTIME_PRESSURE, "实时压力", 0, 0, 6, 4, false),
  status(Type.INSTRUMENT_STATUS, "仪器状态", 0, 0, 8, 2, false),
  exit(Type.MODE, "退出当前模式", 100, 0, 0, 0, false),
  exhale_assess(Type.EXHALE_ASSESS_TIMES, "呼气肌力评估模式", 100, 111, 10, 7, true),
  inhale_assess(Type.INHALE_ASSESS_TIMES, "吸气肌力评估模式", 100, 112, 18, 7, true),
  exhale_train(Type.EXHALE_TRAIN_TIMES, "呼气肌力训练", 100, 121, 62, 7, true),
  inhale_train(Type.INHALE_TRAIN_TIMES, "吸气肌力训练", 100, 122, 69, 7, true),
  lip_girdle_train(Type.LIP_GIRDLE_TRAIN_TIMES, "吸气肌力评估模式", 100, 123, 76, 12, true),
  resistance(Type.MODE, "阻力", 151, 0, 151, 0, false),
  gears(Type.MODE, "缩唇呼吸档位", 152, 0, 152, 0, false),

  ;


  public final Type type; // 指令类型
  public final String description; // 描述
  public final int writeAddress; // 写入地址
  public final int writePayload; // 写入载荷(指令类型)
  public final int readAddress; // 读取地址
  public final int readPayload; // 读取载荷(长度)
  public final boolean autoRead; // 是否需要发送读取反馈

  /**
   * 指令格式
   *
   * @param type         指令类型
   * @param description  描述
   * @param writeAddress 写入地址
   * @param writePayload 写入载荷
   * @param readAddress  读取地址
   * @param readPayload  读取长度
   * @param autoRead     是否需要发送读取反馈
   */
  Cmd(Type type, String description, int writeAddress, int writePayload, int readAddress, int readPayload, boolean autoRead) {
    this.type = type;
    this.description = description;
    this.writeAddress = writeAddress;
    this.writePayload = writePayload;
    this.readAddress = readAddress;
    this.readPayload = readPayload;
    this.autoRead = autoRead;
  }

  public Type getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public int getWriteAddress() {
    return writeAddress;
  }

  public int getWritePayload() {
    return writePayload;
  }

  public int getReadAddress() {
    return readAddress;
  }

  public int getReadPayload() {
    return readPayload;
  }

  public boolean isAutoRead() {
    return autoRead;
  }

  public static Cmd ofRead(int address, int payload) {
    for (Cmd v : values()) {
      if (v.readAddress == address) {
      //if (v.readAddress == address && v.readPayload == payload) {
        return v;
      }
    }
    return null;
  }

  public static Cmd ofWrite(int address, int payload) {
    for (Cmd v : values()) {
      if (v.writeAddress == address) {
      //if (v.writeAddress == address && v.writePayload == payload) {
        return v;
      }
    }
    return null;
  }
}

