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
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import javax.annotation.CheckForNull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

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
          throw new IllegalStateException(e);
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

    /**
     * Computes or retrieves the value corresponding to {@code key}.
     *
     * @param key the non-null key whose value should be loaded
     * @return the value associated with {@code key}; <b>must not be null</b>
     * @throws Exception            if unable to load the result
     * @throws InterruptedException if this method is interrupted. {@code InterruptedException} is
     *                              treated like any other {@code Exception} in all respects except that, when it is caught,
     *                              the thread's interrupt status is set
     */
    @Override
    public V load(K key) throws Exception {
      return map.computeIfAbsent(key, fun);
    }
  }

  Map<K, V> getLoaderMap();

  /**
   * Returns the value associated with {@code key} in this cache, first loading that value if
   * necessary. No observable state associated with this cache is modified until loading completes.
   *
   * <p>If another call to {@link #get} or {@link #getUnchecked} is currently loading the value for
   * {@code key}, simply waits for that thread to finish and returns its loaded value. Note that
   * multiple threads can concurrently load values for distinct keys.
   *
   * <p>Caches loaded by a {@link CacheLoader} will call {@link CacheLoader#load} to load new values
   * into the cache. Newly loaded values are added to the cache using {@code
   * Cache.asMap().putIfAbsent} after loading has completed; if another value was associated with
   * {@code key} while the new value was loading then a removal notification will be sent for the
   * new value.
   *
   * <p>If the cache loader associated with this cache is known not to throw checked exceptions,
   * then prefer {@link #getUnchecked} over this method.
   *
   * @param key
   * @throws ExecutionException          if a checked exception was thrown while loading the value. ({@code
   *                                     ExecutionException} is thrown <a
   *                                     href="https://github.com/google/guava/wiki/CachesExplained#interruption">even if
   *                                     computation was interrupted by an {@code InterruptedException}</a>.)
   * @throws UncheckedExecutionException if an unchecked exception was thrown while loading the
   *                                     value
   * @throws ExecutionError              if an error was thrown while loading the value
   */
  @TryHandler(exception = Exception.class)
  @Override
  V get(K key);

  /**
   * Returns the value associated with {@code key} in this cache, first loading that value if
   * necessary. No observable state associated with this cache is modified until loading completes.
   * Unlike {@link #get}, this method does not throw a checked exception, and thus should only be
   * used in situations where checked exceptions are not thrown by the cache loader.
   *
   * <p>If another call to {@link #get} or {@link #getUnchecked} is currently loading the value for
   * {@code key}, simply waits for that thread to finish and returns its loaded value. Note that
   * multiple threads can concurrently load values for distinct keys.
   *
   * <p>Caches loaded by a {@link CacheLoader} will call {@link CacheLoader#load} to load new values
   * into the cache. Newly loaded values are added to the cache using {@code
   * Cache.asMap().putIfAbsent} after loading has completed; if another value was associated with
   * {@code key} while the new value was loading then a removal notification will be sent for the
   * new value.
   *
   * <p><b>Warning:</b> this method silently converts checked exceptions to unchecked exceptions,
   * and should not be used with cache loaders which throw checked exceptions. In such cases use
   * {@link #get} instead.
   *
   * @param key
   * @throws UncheckedExecutionException if an exception was thrown while loading the value. (As
   *                                     explained in the last paragraph above, this should be an unchecked exception only.)
   * @throws ExecutionError              if an error was thrown while loading the value
   */
  @TryHandler(exception = Exception.class)
  @Override
  V getUnchecked(K key);

  /**
   * Returns a map of the values associated with {@code keys}, creating or retrieving those values
   * if necessary. The returned map contains entries that were already cached, combined with newly
   * loaded entries; it will never contain null keys or values.
   *
   * <p>Caches loaded by a {@link CacheLoader} will issue a single request to {@link
   * CacheLoader#loadAll} for all keys which are not already present in the cache. All entries
   * returned by {@link CacheLoader#loadAll} will be stored in the cache, over-writing any
   * previously cached values. This method will throw an exception if {@link CacheLoader#loadAll}
   * returns {@code null}, returns a map containing null keys or values, or fails to return an entry
   * for each requested key.
   *
   * <p>Note that duplicate elements in {@code keys}, as determined by {@link Object#equals}, will
   * be ignored.
   *
   * @param keys
   * @throws ExecutionException          if a checked exception was thrown while loading the value. ({@code
   *                                     ExecutionException} is thrown <a
   *                                     href="https://github.com/google/guava/wiki/CachesExplained#interruption">even if
   *                                     computation was interrupted by an {@code InterruptedException}</a>.)
   * @throws UncheckedExecutionException if an unchecked exception was thrown while loading the
   *                                     values
   * @throws ExecutionError              if an error was thrown while loading the values
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  ImmutableMap<K, V> getAll(Iterable<? extends K> keys);

  /**
   * @param key
   * @throws UncheckedExecutionException if an exception was thrown while loading the value. (As
   *                                     described in the documentation for {@link #getUnchecked}, {@code LoadingCache} should be
   *                                     used as a {@code Function} only with cache loaders that throw only unchecked exceptions.)
   * @deprecated Provided to satisfy the {@code Function} interface; use {@link #get} or {@link
   * #getUnchecked} instead.
   */
  @TryHandler(exception = Exception.class)
  @Deprecated
  @Override
  V apply(K key);

  /**
   * Loads a new value for {@code key}, possibly asynchronously. While the new value is loading the
   * previous value (if any) will continue to be returned by {@code get(key)} unless it is evicted.
   * If the new value is loaded successfully it will replace the previous value in the cache; if an
   * exception is thrown while refreshing the previous value will remain, <i>and the exception will
   * be logged (using {@link Logger}) and swallowed</i>.
   *
   * <p>Caches loaded by a {@link CacheLoader} will call {@link CacheLoader#reload} if the cache
   * currently contains a value for {@code key}, and {@link CacheLoader#load} otherwise. Loading is
   * asynchronous only if {@link CacheLoader#reload} was overridden with an asynchronous
   * implementation.
   *
   * <p>Returns without doing anything if another thread is currently loading the value for {@code
   * key}. If the cache loader associated with this cache performs refresh asynchronously then this
   * method may return before refresh completes.
   *
   * @param key
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  void refresh(K key);

  /**
   * {@inheritDoc}
   *
   * <p><b>Note that although the view <i>is</i> modifiable, no method on the returned map will ever
   * cause entries to be automatically loaded.</b>
   */
  @TryHandler(exception = Exception.class)
  @Override
  ConcurrentMap<K, V> asMap();

  /**
   * Returns the value associated with {@code key} in this cache, or {@code null} if there is no
   * cached value for {@code key}.
   *
   * @param key
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @CheckForNull
  @Override
  V getIfPresent(Object key);

  /**
   * Returns the value associated with {@code key} in this cache, obtaining that value from {@code
   * loader} if necessary. The method improves upon the conventional "if cached, return; otherwise
   * create, cache and return" pattern. For further improvements, use {@link LoadingCache} and its
   * {@link LoadingCache#get(Object) get(K)} method instead of this one.
   *
   * <p>Among the improvements that this method and {@code LoadingCache.get(K)} both provide are:
   *
   * <ul>
   *   <li>{@linkplain LoadingCache#get(Object) awaiting the result of a pending load} rather than
   *       starting a redundant one
   *   <li>eliminating the error-prone caching boilerplate
   *   <li>tracking load {@linkplain #stats statistics}
   * </ul>
   *
   * <p>Among the further improvements that {@code LoadingCache} can provide but this method cannot:
   *
   * <ul>
   *   <li>consolidation of the loader logic to {@linkplain CacheBuilder#build(CacheLoader) a single
   *       authoritative location}
   *   <li>{@linkplain LoadingCache#refresh refreshing of entries}, including {@linkplain
   *       CacheBuilder#refreshAfterWrite automated refreshing}
   *   <li>{@linkplain LoadingCache#getAll bulk loading requests}, including {@linkplain
   *       CacheLoader#loadAll bulk loading implementations}
   * </ul>
   *
   * <p><b>Warning:</b> For any given key, every {@code loader} used with it should compute the same
   * value. Otherwise, a call that passes one {@code loader} may return the result of another call
   * with a differently behaving {@code loader}. For example, a call that requests a short timeout
   * for an RPC may wait for a similar call that requests a long timeout, or a call by an
   * unprivileged user may return a resource accessible only to a privileged user making a similar
   * call. To prevent this problem, create a key object that includes all values that affect the
   * result of the query. Or use {@code LoadingCache.get(K)}, which lacks the ability to refer to
   * state other than that in the key.
   *
   * <p><b>Warning:</b> as with {@link CacheLoader#load}, {@code loader} <b>must not</b> return
   * {@code null}; it may either return a non-null value or throw an exception.
   *
   * <p>No observable state associated with this cache is modified until loading completes.
   *
   * @param key
   * @param loader
   * @throws ExecutionException          if a checked exception was thrown while loading the value
   * @throws UncheckedExecutionException if an unchecked exception was thrown while loading the
   *                                     value
   * @throws ExecutionError              if an error was thrown while loading the value
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  V get(K key, Callable<? extends V> loader);

  /**
   * Returns a map of the values associated with {@code keys} in this cache. The returned map will
   * only contain entries which are already present in the cache.
   *
   * @param keys
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  ImmutableMap<K, V> getAllPresent(Iterable<?> keys);

  /**
   * Associates {@code value} with {@code key} in this cache. If the cache previously contained a
   * value associated with {@code key}, the old value is replaced by {@code value}.
   *
   * <p>Prefer {@link #get(Object, Callable)} when using the conventional "if cached, return;
   * otherwise create, cache and return" pattern.
   *
   * @param key
   * @param value
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  void put(K key, V value);

  /**
   * Copies all of the mappings from the specified map to the cache. The effect of this call is
   * equivalent to that of calling {@code put(k, v)} on this map once for each mapping from key
   * {@code k} to value {@code v} in the specified map. The behavior of this operation is undefined
   * if the specified map is modified while the operation is in progress.
   *
   * @param m
   * @since 12.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  void putAll(Map<? extends K, ? extends V> m);

  /**
   * Discards any cached value for key {@code key}.
   *
   * @param key
   */
  @TryHandler(exception = Exception.class)
  @Override
  void invalidate(Object key);

  /**
   * Discards any cached values for keys {@code keys}.
   *
   * @param keys
   * @since 11.0
   */
  @TryHandler(exception = Exception.class)
  @Override
  void invalidateAll(Iterable<?> keys);

  /**
   * Discards all entries in the cache.
   */
  @TryHandler(exception = Exception.class)
  @Override
  void invalidateAll();

  /**
   * Returns the approximate number of entries in this cache.
   */
  @TryHandler(exception = Exception.class)
  @Override
  long size();

  /**
   * Returns a current snapshot of this cache's cumulative statistics, or a set of values if
   * the cache is not recording statistics. All statistics begin at zero and never decrease over the
   * lifetime of the cache.
   *
   * <p><b>Warning:</b> this cache may not be recording statistical data. For example, a cache
   * created using {@link CacheBuilder} only does so if the {@link CacheBuilder#recordStats} method
   * was called. If statistics are not being recorded, a {@code CacheStats} instance with zero for
   * all values is returned.
   */
  @TryHandler(exception = Exception.class)
  @Override
  CacheStats stats();

  /**
   * Performs any pending maintenance operations needed by the cache. Exactly which activities are
   * performed -- if any -- is implementation-dependent.
   */
  @TryHandler(exception = Exception.class)
  @Override
  void cleanUp();
}
