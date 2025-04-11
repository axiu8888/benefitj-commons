package com.benefitj.frameworks.ehcache;

import com.benefitj.core.ShutdownHook;
import com.benefitj.core.SingletonSupplier;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 缓存代理
 *
 * @param <K>
 * @param <V>
 */
public interface IEncache<K, V> extends Cache<K, V> {

  /**
   * 名称
   */
  @MethodReturn
  String getName();

  /**
   * 键类型
   */
  @MethodReturn
  Class<K> getKeyType();

  /**
   * 值类型
   */
  @MethodReturn
  Class<V> getValueType();

  /**
   * 缓存
   */
  @MethodReturn
  Cache<K, V> getCache();

  /**
   * 创建缓存代理
   *
   * @param cache     缓存
   * @param name      缓存名
   * @param keyType   键类型
   * @param valueType 值类型
   * @return 返回代理
   */
  public static <K, V> IEncache<K, V> newProxy(Cache<K, V> cache, String name, Class<K> keyType, Class<V> valueType) {
    MethodReturn.Handler handler = new MethodReturn.DefaultHandler(new HashMap<String, Object>() {{
      put("cache", cache);
      put("name", name);
      put("keyType", keyType);
      put("valueType", valueType);
    }});
    return (IEncache<K, V>) Proxy.newProxyInstance(IEncache.class.getClassLoader(), new Class[]{IEncache.class, Cache.class}, (proxy, method, args) -> {
      if (method.isAnnotationPresent(MethodReturn.class)) {
        return handler.process(proxy, method, args, method.getAnnotation(MethodReturn.class));
      }
      return method.invoke(cache, args);
    });
  }

  /**
   * 缓存配置
   */
  static <K, V> CacheConfigurationBuilder cacheConfigurationBuilder(Class<K> keyType, Class<V> valueType) {
    return CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType
            , ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(10, MemoryUnit.MB)
                .offheap(20, MemoryUnit.MB)
                .disk(200, MemoryUnit.MB, true)
                .build()
        )
        .withKeySerializer(new JsonValueSerializer<>(keyType))
        .withValueSerializer(new JsonValueSerializer<>(valueType))
        .withExpiry(Expirations.timeToLiveExpiration(Duration.of(6, TimeUnit.HOURS)));
  }

  /**
   * 缓存配置工厂
   */
  interface CacheConfigurationBuilderFactory {
    /**
     * 创建缓存配置
     *
     * @param keyType   键类型
     * @param valueType 值类型
     * @return 返回配置
     */
    <K, V> CacheConfigurationBuilder<K, V> build(Class<K> keyType, Class<V> valueType);

  }

  class Factory {

    static final SingletonSupplier<Factory> singleton
        = SingletonSupplier.of(() -> new Factory(new File("./cache")));

    public static Factory get() {
      return singleton.get();
    }

    final CacheManager cacheManager;
    final Map<String, IEncache> cacheMap = new ConcurrentHashMap<>();

    /**
     * 配置工厂
     */
    private CacheConfigurationBuilderFactory cacheConfigurationBuilderFactory;

    public Factory(File cacheDir) {
      this(cacheDir, IEncache::cacheConfigurationBuilder);
    }

    public Factory(File cacheDir, CacheConfigurationBuilderFactory cacheConfigurationBuilderFactory) {
      this(CacheManagerBuilder.newCacheManagerBuilder()
              .with(CacheManagerBuilder.persistence(cacheDir))
              .build(true)
          , cacheConfigurationBuilderFactory);

      // 关闭
      ShutdownHook.register(cacheManager::close);
    }

    public Factory(CacheManager cacheManager, CacheConfigurationBuilderFactory cacheConfigurationBuilderFactory) {
      this.cacheManager = cacheManager;
      this.cacheConfigurationBuilderFactory = cacheConfigurationBuilderFactory;
    }

    public CacheManager getCacheManager() {
      return cacheManager;
    }

    public CacheConfigurationBuilderFactory getCacheConfigurationBuilderFactory() {
      return cacheConfigurationBuilderFactory;
    }

    public void setCacheConfigurationBuilderFactory(CacheConfigurationBuilderFactory cacheConfigurationBuilderFactory) {
      this.cacheConfigurationBuilderFactory = cacheConfigurationBuilderFactory;
    }

    public <K, V> Cache<K, V> getCache(String name, Class<K> keyType, Class<V> valueType) {
      return getCacheManager().getCache(name, keyType, valueType);
    }

    public <K, V> IEncache<K, V> createIfAbsent(String name, Class<K> keyType, Class<V> valueType) {
      return createIfAbsent(name, keyType, valueType, cacheConfigurationBuilderFactory);
    }

    public <K, V> IEncache<K, V> createIfAbsent(String name, Class<K> keyType, Class<V> valueType, CacheConfigurationBuilderFactory cacheConfigurationBuilderFactory) {
      IEncache ICache = cacheMap.get(name);
      if (ICache != null) return ICache;
      return cacheMap.computeIfAbsent(name, k -> {
        Cache<K, V> cache = getCache(name, keyType, valueType);
        if (cache == null) {
          cache = getCacheManager().createCache(name, cacheConfigurationBuilderFactory.build(keyType, valueType));
        }
        return newProxy(cache, name, keyType, valueType);
      });
    }

  }

}


