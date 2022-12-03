package com.benefitj.extension.comment;

public enum Quote {

  SINGLE('\'', "单引号"),
  DOUBLE('\"', "双引号"),
  BACK('`', "反单引号"),
  ;

  final char symbol;
  final String name;

  Quote(char symbol, String name) {
    this.symbol = symbol;
    this.name = name;
  }

  public char getSymbol() {
    return symbol;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return String.format("%s(%s, %s)", name(), symbol, name);
  }

  /**
   * 获取类型
   *
   * @param ch
   * @return
   */
  public static Quote get(char ch) {
    for (Quote v : values()) {
      if (v.symbol == ch) {
        return v;
      }
    }
    return null;
  }

}
