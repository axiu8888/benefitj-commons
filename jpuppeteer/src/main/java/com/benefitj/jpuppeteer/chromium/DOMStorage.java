package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Query and modify DOM storage. EXPERIMENTAL
 */
@ChromiumApi("DOMStorage")
public interface DOMStorage {

  /**
   *
   */
  void clear();

  /**
   * Disables storage tracking, prevents storage events from being sent to the client.
   */
  void disable();

  /**
   * Enables storage tracking, storage events will now be delivered to the client.
   */
  void enable();

  /**
   * @param storageId StorageId
   * @return {
   * entries: array[ Item ]
   * }
   */
  JSONObject getDOMStorageItems(String storageId);

  /**
   * @param storageId StorageId
   * @param key       string
   */
  void removeDOMStorageItem(String storageId, String key);

  /**
   * @param storageId StorageId
   * @param key       string
   * @param value     string
   */
  void setDOMStorageItem(String storageId, String key, String value);

  /**
   * 事件
   */
  @ChromiumApi("DOMStorage")
  public interface Events {

    /**
     * @param storageId StorageId
     * @param key       string
     * @param newValue  string
     */
    @Event("domStorageItemAdded")
    void domStorageItemAdded(String storageId, String key, String newValue);

    /**
     * @param storageId StorageId
     * @param key       string
     */
    @Event("domStorageItemRemoved")
    void domStorageItemRemoved(String storageId, String key);

    /**
     * @param storageId StorageId
     */
    @Event("domStorageItemsCleared")
    void domStorageItemsCleared(String storageId);

    /**
     * @param storageId StorageId
     * @param key       string
     * @param oldValue  string
     * @param newValue  string
     */
    @Event("domStorageItemUpdated")
    void domStorageItemUpdated(String storageId, String key, String oldValue, String newValue);

  }

  /**
   * DOM Storage item.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class Item extends JSONArray {
  }

  /**
   * DOM Storage identifier.
   */
  @EqualsAndHashCode
  @Data
  public class StorageId {
    /**
     * Security origin for the storage.
     */
    String securityOrigin;
    /**
     * Represents a key by which DOM Storage keys its CachedStorageAreas
     */
    String storageKey;
    /**
     * Whether the storage is local storage (not session storage).
     */
    boolean isLocalStorage;
  }

}
