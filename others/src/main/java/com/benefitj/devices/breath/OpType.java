package com.benefitj.devices.breath;

public enum OpType {
  /**
   * 0 退出任何评估训练模式
   * 111 进入呼气肌力评估
   * 112 进入吸气肌力评估
   * 121 进入呼气肌力训练
   * 122 进入吸气肌力训练
   * 123 进入缩唇呼吸训练
   */

  EXIT(0, "退出"),
  EXHALE_ASSESS(111, "进入呼气肌力评估"),
  INHALE_ASSESS(112, "进入吸气肌力评估"),
  EXHALE_TRAIN(121, "进入呼气肌力训练"),
  INHALE_TRAIN(122, "进入吸气肌力训练"),
  LIP_GIRDLE_TRAIN(123, "进入缩唇呼吸训练"),
  ;

  private final int flag;
  private final String descriptor;

  OpType(int flag, String descriptor) {
    this.flag = flag;
    this.descriptor = descriptor;
  }

  public int getFlag() {
    return flag;
  }

  public String getDescriptor() {
    return descriptor;
  }
}
