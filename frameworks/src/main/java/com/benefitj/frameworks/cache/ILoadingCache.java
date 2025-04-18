package com.benefitj.frameworks.cache;

import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.frameworks.cglib.CGLibProxy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * LoadingCache
 *
 * @param <K> 键
 * @param <V> 值
 */
public interface ILoadingCache<K, V> extends LoadingCache<K, V> {

  @MethodReturn(name = "loaderMap")
  Map<K, V> getLoaderMap();

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
    final MethodReturn.Handler handler = new MethodReturn.DefaultHandler(new HashMap<String, Object>() {{
      put("loaderMap", loaderMap);
    }});
    return ProxyUtils.newProxy(ILoadingCache.class
        , (proxy, method, args) -> method.isAnnotationPresent(MethodReturn.class)
            ? handler.process(proxy, method, args, method.getAnnotation(MethodReturn.class))
            : ReflectUtils.invoke(cache, method, args));
  }

  static <K, V> ILoadingCache<K, V> wrapCGLib(CacheBuilder<K, V> builder,
                                              Map<K, V> loaderMap,
                                              BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrapCGLib(builder.build(newMapLoader(fun)), loaderMap);
  }

  static <K, V> ILoadingCache<K, V> wrapCGLib(CacheBuilder<K, V> builder,
                                              BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrapCGLib(builder.build(newMapLoader(fun)), new ConcurrentHashMap<>(20));
  }

  static <K, V> ILoadingCache<K, V> wrapCGLib(LoadingCache<K, V> cache, Map<K, V> loaderMap) {
    final MethodReturn.Handler handler = new MethodReturn.DefaultHandler(new HashMap<String, Object>() {{
      put("loaderMap", loaderMap);
    }});
    return CGLibProxy.newProxy(null
        , new Class[]{LoadingCache.class, ILoadingCache.class}
        , (obj, method, args, proxy) -> method.isAnnotationPresent(MethodReturn.class)
            ? handler.process(proxy, method, args, method.getAnnotation(MethodReturn.class))
            : proxy.invoke(cache, args)
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

}
