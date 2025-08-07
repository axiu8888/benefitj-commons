package com.benefitj.core;

import com.benefitj.core.local.InitialCallback;
import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 上下文对象
 */
public class LocalContext<T> {

  final LocalCache<T> cache;

  public LocalContext() {
    this.cache = LocalCacheFactory.newCache();
  }

  public LocalContext(InitialCallback<T> callback) {
    this.cache = LocalCacheFactory.newCache(callback);
  }

  public void setInitialCallback(InitialCallback<T> callback) {
    cache.setInitialCallback(callback);
  }

  public T get() {
    return cache.get();
  }

  public T get(T defaultValue) {
    return cache.get(defaultValue);
  }

  public T getAndRemove() {
    return cache.getAndRemove();
  }

  public void set(T value) {
    cache.set(value);
  }

  public void remove() {
    cache.remove();
  }

  public void execute(Consumer<LocalContext<T>> consumer) {
    execute(null, consumer);
  }

  public void execute(Consumer<LocalContext<T>> before, Consumer<LocalContext<T>> consumer) {
    execute(before, consumer, null, (ctx, err) -> err.printStackTrace());
  }

  public void execute(Consumer<LocalContext<T>> before,
                      Consumer<LocalContext<T>> consumer,
                      Consumer<LocalContext<T>> after,
                      BiConsumer<LocalContext<T>, Throwable> error) {
    try {
      if (before != null) before.accept(this);
      consumer.accept(this);
      if (after != null) after.accept(this);
    } catch (Throwable e) {
      error.accept(this, e);
    } finally {
      remove();
    }
  }

}
