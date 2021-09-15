package com.benefitj.event;

public class OriginalEventWrapper implements EventWrapper {

  @Override
  public Object wrap(Object original) {
    return original;
  }
}