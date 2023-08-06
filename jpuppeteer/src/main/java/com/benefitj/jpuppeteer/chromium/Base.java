package com.benefitj.jpuppeteer.chromium;

public interface Base {

  public abstract class IntegerBase extends Number {

    int value;

    public IntegerBase(int value) {
      this.value = value;
    }

    @Override
    public int intValue() {
      return value;
    }

    @Override
    public long longValue() {
      return value;
    }

    @Override
    public float floatValue() {
      return value;
    }

    @Override
    public double doubleValue() {
      return value;
    }
  }

}
