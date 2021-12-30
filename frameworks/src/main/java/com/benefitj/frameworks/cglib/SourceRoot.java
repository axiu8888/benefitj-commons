package com.benefitj.frameworks.cglib;

public class SourceRoot<T> {

  private T source;

  public SourceRoot() {
  }

  public SourceRoot(T source) {
    this.source = source;
  }

  public T getSource() {
    return source;
  }

  public void setSource(T source) {
    this.source = source;
  }
}
