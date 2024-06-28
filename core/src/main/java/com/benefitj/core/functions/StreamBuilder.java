package com.benefitj.core.functions;

import com.benefitj.core.CatchUtils;

import java.util.function.Consumer;

public class StreamBuilder<T> {

  public static <T> StreamBuilder<T> of(T target) {
    return new StreamBuilder<>(target);
  }

  private final T target;

  public StreamBuilder(T target) {
    this.target = target;
  }

  public StreamBuilder<T> set(Consumer<T> consumer) {
    return set(consumer, true);
  }

  public StreamBuilder<T> set(Consumer<T> consumer, boolean match) {
    if (match) {
      consumer.accept(get());
    }
    return this;
  }

  public StreamBuilder<T> handle(IConsumer<T> consumer) {
    try {
      consumer.accept(get());
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
    return this;
  }

  public T get() {
    return target;
  }

}
