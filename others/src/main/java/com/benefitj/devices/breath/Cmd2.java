package com.benefitj.devices.breath;

public enum Cmd2 {

  exit("7B0600640000C38F7D0D0A", "", "退出当前模式", Type.MODE),
  exhale_assess("7B060064006F83A37D0D0A", "7B0300120008EF937D0D0A", "呼气肌力评估", Type.EXHALE_ASSESS_TIMES),
  inhale_assess("7B0600640070C26B7D0D0A", "7B0300120008EF937D0D0A", "吸气肌力评估", Type.INHALE_ASSESS_TIMES),
  exhale_train("7B0600640079026D7D0D0A", "7B03003E00076E5E7D0D0A", "呼气肌力训练", Type.EXHALE_TRAIN_TIMES),
  inhale_train("7B060064007A426C7D0D0A", "7B03004500071E477D0D0A", "吸气肌力训练", Type.INHALE_TRAIN_TIMES),
  lip_girdle_train("7B060064007B83AC7D0D0A", "7B03004C00060F857D0D0A", "缩唇呼吸训练", Type.LIP_GIRDLE_TRAIN_TIMES),
  ;


  public final String write;
  public final String read;
  public final String descriptor;
  public final Type type;

  Cmd2(String write, String read, String descriptor, Type type) {
    this.write = write;
    this.read = read;
    this.descriptor = descriptor;
    this.type = type;
  }

  public String getWrite() {
    return write;
  }

  public String getRead() {
    return read;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public Type getType() {
    return type;
  }
}
