package com.benefitj.frameworks.cache;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.frameworks.cglib.CGLibProxy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import javax.annotation.CheckForNull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * LoadingCache
 *
 * @param <K> 键
 * @param <V> 值
 */
public interface ILoadingCache<K, V> extends LoadingCache<K, V> {

  Handler NONE = (method, args, proxy) -> null;

  static <K, V> ILoadingCache<K, V> wrap(CacheBuilder<K, V> builder,
                                         Map<K, V> loaderMap,
                                         BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrap(builder.build(newMapLoader(fun)), loaderMap);
  }

  static <K, V> ILoadingCache<K, V> wrap(CacheBuilder<K, V> builder,
                                         BiFunction<MapCacheLoader<K, V>, K, V> fun) {
    return wrap(builder.build(newMapLoader(fun)), new ConcurrentHashMap<>(20));
  }

  static <K, V> ILoadingCache<K, V> wrap(LoadingCache<K, V> cache, Map<K, V> loaderMap) {
    final Map<Method, Method> methods = new ConcurrentHashMap<>(30);
    final Map<Class<?>, Handler> handlers = new ConcurrentHashMap<>(30);
    return ProxyUtils.newProxy(ILoadingCache.class, (proxy, method, args) -> {
      try {
        if ("getLoaderMap".equals(method.getName()) && method.getParameterTypes().length == 0) {
          return loaderMap;
        }
        Method raw = methods.get(method);
        if (raw == null) {
          raw = methods.computeIfAbsent(method, m -> ReflectUtils.getMethod(cache.getClass(), t -> {
            if (!t.getName().equals(method.getName())) return false;
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < t.getParameterTypes().length; i++) {
              if (t.getParameterTypes()[i] != parameterTypes[i]) {
                return false;
              }
            }
            return true;
          }));
        }
        return ReflectUtils.invoke(cache, raw, args);
      } catch (Throwable e) {
        if (method.isAnnotationPresent(TryHandler.class)) {
          TryHandler annotation = method.getAnnotation(TryHandler.class);
          if (e.getClass().isAssignableFrom(annotation.exception())) {
            Handler handler = handlers.get(annotation.handler());
            if (handler == null) {
              handler = handlers.computeIfAbsent(annotation.handler(), k -> CatchUtils.tryThrow(() -> ReflectUtils.newInstance(annotation.handler()), ee -> NONE));
            }
            final Handler h = handler;
            return CatchUtils.ignore(() -> h.process(method, args, null));
          }
          return null;
        } else {
          throw new IllegalStateException(CatchUtils.findRoot(e));
        }
      }
    });
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
    final Map<Class<?>, Handler> handlers = new ConcurrentHashMap<>(30);
    return CGLibProxy.newProxy(null
        , new Class[]{LoadingCache.class, ILoadingCache.class}
        , (obj, method, args, proxy) -> {
          try {
            if ("getLoaderMap".equals(method.getName()) && method.getParameterTypes().length == 0) {
              return loaderMap;
            }
            return proxy.invoke(cache, args);
          } catch (Exception e) {
            if (method.isAnnotationPresent(TryHandler.class)) {
              TryHandler annotation = method.getAnnotation(TryHandler.class);
              if (e.getClass().isAssignableFrom(annotation.exception())) {
                Handler handler = handlers.get(annotation.handler());
                if (handler == null) {
                  handlers.put(annotation.handler(), handler = CatchUtils.tryThrow(() -> ReflectUtils.newInstance(annotation.handler()), ee -> NONE));
                }
                final Handler _h = handler;
                return CatchUtils.ignore(() -> _h.process(method, args, proxy));
              }
            }
          }
          return null;
        }
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

  Map<K, V> getLoaderMap();

  @TryHandler(exception = Exception.class)
  @Override
  V get(K key);

  @TryHandler(exception = Exception.class)
  @Override
  V getUnchecked(K key);

  @TryHandler(exception = Exception.class)
  @Override
  ImmutableMap<K, V> getAll(Iterable<? extends K> keys);

  @TryHandler(exception = Exception.class)
  @Deprecated
  @Override
  V apply(K key);

  @TryHandler(exception = Exception.class)
  @Override
  void refresh(K key);

  @TryHandler(exception = Exception.class)
  @Override
  ConcurrentMap<K, V> asMap();

  @TryHandler(exception = Exception.class)
  @CheckForNull
  @Override
  V getIfPresent(Object key);

  @TryHandler(exception = Exception.class)
  @Override
  V get(K key, Callable<? extends V> loader);

  @TryHandler(exception = Exception.class)
  @Override
  ImmutableMap<K, V> getAllPresent(Iterable<?> keys);

  @TryHandler(exception = Exception.class)
  @Override
  void put(K key, V value);

  @TryHandler(exception = Exception.class)
  @Override
  void putAll(Map<? extends K, ? extends V> m);

  @TryHandler(exception = Exception.class)
  @Override
  void invalidate(Object key);

  @TryHandler(exception = Exception.class)
  @Override
  void invalidateAll(Iterable<?> keys);

  @TryHandler(exception = Exception.class)
  @Override
  void invalidateAll();

  @TryHandler(exception = Exception.class)
  @Override
  long size();

  @TryHandler(exception = Exception.class)
  @Override
  CacheStats stats();

  @TryHandler(exception = Exception.class)
  @Override
  void cleanUp();
}
