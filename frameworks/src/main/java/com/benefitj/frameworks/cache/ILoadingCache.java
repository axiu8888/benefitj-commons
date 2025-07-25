package com.benefitj.frameworks.cache;

import com.benefitj.core.EventLoop;
import com.benefitj.core.ReflectUtils;
import com.benefitj.core.annotation.MethodReturn;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

import java.lang.annotation.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * LoadingCache
 *
 * @param <K> 键
 * @param <V> 值
 */
@ILoadingCache.CleanUpScheduler(value = 3, unit = TimeUnit.SECONDS)
public interface ILoadingCache<K, V> extends LoadingCache<K, V> {

  @MethodReturn(name = "loaderMap")
  Map<K, V> getLoaderMap();

  @MethodReturn(name = "cache")
  LoadingCache<K, V> getCache();

  @MethodReturn(name = "attrs")
  Map<String, Object> attrs();

  /**
   * 开启清理调度
   */
  default void startCleanup() {
    Class<? extends ILoadingCache> type = getClass();
    attrs().computeIfAbsent("_cleanUp_", key -> {
      CleanUpScheduler annotation = type.getAnnotation(CleanUpScheduler.class);
      long period = annotation != null ? annotation.value() : 3;
      TimeUnit unit = annotation != null ? annotation.unit() :TimeUnit.SECONDS;
      return EventLoop.asyncIOFixedRate(this::cleanUp, period, period, unit);
    });
  }

  /**
   * 停止清理调度
   */
  default void stopCleanup() {
    EventLoop.cancel((ScheduledFuture) attrs().remove("_cleanUp_"));
  }

  /**
   * 创建写入超时缓存
   *
   * @param writeExpired    写入超时
   * @param removalListener 移除监听器
   * @return 返回缓存
   */
  static <K, V> ILoadingCache<K, V> newWriteCache(Duration writeExpired, RemovalListener<K, V> removalListener) {
    CacheBuilder<K, V> builder = CacheBuilder.<K, V>newBuilder()
        .removalListener(removalListener != null ? removalListener : NOTHING_DONE)
        .expireAfterWrite(writeExpired);
    return wrap(builder, (kvMapCacheLoader, k) -> null);
  }

  /**
   * 创建访问超时缓存
   *
   * @param writeExpired    写入超时
   * @param removalListener 移除监听器
   * @return 返回缓存
   */
  static <K, V> ILoadingCache<K, V> newAccessCache(Duration writeExpired, RemovalListener<K, V> removalListener) {
    CacheBuilder<K, V> builder = CacheBuilder.<K, V>newBuilder()
        .removalListener(removalListener != null ? removalListener : NOTHING_DONE)
        .expireAfterAccess(writeExpired);
    return wrap(builder, (kvMapCacheLoader, k) -> null);
  }

  static <K, V> ILoadingCache<K, V> wrap(CacheBuilder<K, V> builder,
                                         BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrap(builder.build(newMapLoader(fun)), new ConcurrentHashMap<>(20));
  }

  static <K, V> ILoadingCache<K, V> wrap(CacheBuilder<K, V> builder,
                                         Map<K, V> loaderMap,
                                         BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrap(builder.build(newMapLoader(fun)), loaderMap);
  }

  static <K, V> ILoadingCache<K, V> wrap(LoadingCache<K, V> cache, Map<K, V> loaderMap) {
    return MethodReturn.Handler.newProxy(ILoadingCache.class
        , (proxy, method, args) ->
            method.isDefault()
                ? ReflectUtils.invokeDefault(proxy, method, args)
                : ReflectUtils.invoke(cache, method, args)
        , new HashMap<String, Object>() {{
          put("cache", cache);
          put("loaderMap", loaderMap);
          put("attrs", new ConcurrentHashMap<>());
        }}
    );
  }

  static <K, V> MapCacheLoader<K, V> newMapLoader(BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return new MapCacheLoader<>(fun);
  }

  class MapCacheLoader<K, V> extends CacheLoader<K, V> {

    public final Function<K, V> fun = key -> getAbsentFun().apply(MapCacheLoader.this, key);
    public final Map<K, V> map;
    BiFunction<MapCacheLoader<K, V>, K, V> absentFun;

    public MapCacheLoader(BiFunction<MapCacheLoader<K, V>, K, V> fun) {
      this(new ConcurrentHashMap<>(), fun);
    }

    public MapCacheLoader(Map<K, V> map, BiFunction<MapCacheLoader<K, V>, K, V> absentFun) {
      this.map = map;
      this.absentFun = absentFun;
    }

    public Map<K, V> map() {
      return map;
    }

    public BiFunction<MapCacheLoader<K, V>, K, V> getAbsentFun() {
      return absentFun;
    }

    public void setAbsentFun(BiFunction<MapCacheLoader<K, V>, K, V> absentFun) {
      this.absentFun = absentFun;
    }

    @Override
    public V load(K key) throws Exception {
      return map.computeIfAbsent(key, fun);
    }
  }


  RemovalListener NOTHING_DONE = (notification) -> {/*^_^*/};


  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  @Inherited
  public @interface CleanUpScheduler {

    long value() default 3;

    TimeUnit unit() default TimeUnit.SECONDS;

  }

}
