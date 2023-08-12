package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * CacheStorage Domain
 */
@ChromiumApi("CacheStorage")
public interface CacheStorage {

  /**
   * Deletes a cache.
   *
   * @param cacheId CacheId
   *                Id of cache for deletion.
   */
  void deleteCache(String cacheId);

  /**
   * Deletes a cache entry.
   *
   * @param cacheId CacheId
   *                Id of cache where the entry will be deleted.
   * @param request string
   *                URL spec of the request.
   */
  void deleteEntry(String cacheId, String request);

  /**
   * Fetches cache entry.
   *
   * @param cacheId        CacheId
   *                       Id of cache that contains the entry.
   * @param requestURL     string
   *                       URL spec of the request.
   * @param requestHeaders array[ Header ]
   *                       headers of the request.
   * @return {
   * response: CachedResponse   Response read from the cache.
   * }
   */
  JSONObject requestCachedResponse(String cacheId, String requestURL, List<Header> requestHeaders);

  /**
   * Requests cache names.
   *
   * @param securityOrigin string
   *                       At least and at most one of securityOrigin, storageKey, storageBucket must be specified. Security origin.
   * @param storageKey     string
   *                       Storage key.
   * @param storageBucket  Storage.StorageBucket
   *                       Storage bucket. If not specified, it uses the default bucket.
   * @return {
   * caches: array[ Cache ]  Caches for the security origin.
   * }
   */
  JSONObject requestCacheNames(String securityOrigin, String storageKey, String storageBucket);

  /**
   * Requests data from cache.
   *
   * @param cacheId    CacheId
   *                   ID of cache to get entries from.
   * @param skipCount  integer
   *                   Number of records to skip.
   * @param pageSize   integer
   *                   Number of records to fetch.
   * @param pathFilter string
   *                   If present, only return the entries containing this substring in the path
   * @return {
   * cacheDataEntries: array[ DataEntry ]  Array of object store data entries.
   * returnCount: number  Count of returned entries from this storage. If pathFilter is empty, it is the count of all entries from this storage.
   * }
   */
  JSONObject requestEntries(String cacheId, Integer skipCount, Integer pageSize, String pathFilter);

  @Event("CacheStorage")
  public interface Events {

  }

  /**
   * Cache identifier.
   */
  @Data
  public class Cache {
    /**
     * CacheId
     * <p>
     * An opaque unique id of the cache.
     */
    String cacheId;
    /**
     * Security origin of the cache.
     */
    String securityOrigin;
    /**
     * Storage key of the cache.
     */
    String storageKey;
    /**
     * Storage bucket of the cache.
     */
    Storage.StorageBucket storageBucket;
    /**
     * The name of the cache.
     */
    String cacheName;
  }

  /**
   * Cached response
   */
  @Data
  public class CachedResponse {
    /**
     * Entry content, base64-encoded. (Encoded as a base64 string when passed over JSON)
     */
    String body;
  }

  /**
   * type of HTTP response cached
   * Allowed Values: basic, cors, default, error, opaqueResponse, opaqueRedirect
   */
  public enum CachedResponseType {
    basic, cors, __default, error, opaqueResponse, opaqueRedirect
  }

  /**
   *
   */
  @Data
  public class DataEntry {
    /**
     * Request URL.
     */
    String requestURL;
    /**
     * Request method.
     */
    String requestMethod;
    /**
     * array[ Header ]
     * Request headers;
     */
    List<Header> requestHeaders;
    /**
     * Number of seconds since epoch.
     */
    Long responseTime;
    /**
     * HTTP response status code.
     */
    Integer responseStatus;
    /**
     * HTTP response status text.
     */
    String responseStatusText;
    /**
     * HTTP response type
     */
    CachedResponseType responseType;
    /**
     * array[ Header ]
     * Response headers;
     */
    List<Header> responseHeaders;
  }

  /**
   *
   */
  @Data
  public class Header {
    String name;
    String value;
  }

}
