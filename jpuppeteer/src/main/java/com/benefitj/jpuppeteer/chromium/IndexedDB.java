package com.benefitj.jpuppeteer.chromium;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * IndexedDB Domain
 */
@ChromiumApi("IndexedDB")
public interface IndexedDB {

  /**
   * Clears all entries from an object store.
   *
   * @param securityOrigin  string
   *                        At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey      string
   *                        Storage key.
   * @param storageBucket   Storage.StorageBucket
   *                        Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName    string
   *                        Database name.
   * @param objectStoreName string
   *                        Object store name.
   */
  void clearObjectStore(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket, String databaseName, String objectStoreName);

  /**
   * Deletes a database.
   *
   * @param securityOrigin string
   *                       At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey     string
   *                       Storage key.
   * @param storageBucket  Storage.StorageBucket
   *                       Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName   string
   *                       Database name.
   */
  void deleteDatabase(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket, String databaseName);

  /**
   * Delete a range of entries from an object store
   *
   * @param securityOrigin  string
   *                        At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey      string
   *                        Storage key.
   * @param storageBucket   Storage.StorageBucket
   *                        Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName    string
   * @param objectStoreName string
   * @param keyRange        KeyRange
   *                        Range of entry keys to delete
   */
  void deleteObjectStoreEntries(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket,
                                String databaseName, String objectStoreName, KeyRange keyRange);

  /**
   * Disables events from backend.
   */
  void disable();

  /**
   * Enables events from backend.
   */
  void enable();

  /**
   * Gets metadata of an object store.
   *
   * @param securityOrigin  string
   *                        At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey      string
   *                        Storage key.
   * @param storageBucket   Storage.StorageBucket
   *                        Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName    string
   *                        Database name.
   * @param objectStoreName string
   *                        Object store name.
   * @return {
   * entriesCount: number  the entries count
   * keyGeneratorValue: number  the current value of key generator, to become the next inserted key into the object store. Valid if objectStore.autoIncrement is true.
   * }
   */
  JSONObject getMetadata(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket, String databaseName, String objectStoreName);

  /**
   * Requests data from object store or index.
   *
   * @param securityOrigin  string
   *                        At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey      string
   *                        Storage key.
   * @param storageBucket   Storage.StorageBucket
   *                        Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName    string
   *                        Database name.
   * @param objectStoreName string
   *                        Object store name.
   * @param indexName       string
   *                        Index name, empty string for object store data requests.
   * @param skipCount       integer
   *                        Number of records to skip.
   * @param pageSize        integer
   *                        Number of records to fetch.
   * @param keyRange        KeyRange
   *                        Key range.
   * @return {
   * objectStoreDataEntries: array[ DataEntry ] Array of object store data entries.
   * hasMore: boolean  If true, there are more entries to fetch in the given range.
   * }
   */
  JSONObject requestData(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket, String databaseName,
                         String objectStoreName, String indexName, Integer skipCount, Integer pageSize, KeyRange keyRange);

  /**
   * Requests database with given name in given frame.
   *
   * @param securityOrigin string
   *                       At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey     string
   *                       Storage key.
   * @param storageBucket  Storage.StorageBucket
   *                       Storage bucket. If not specified, it uses the default bucket.
   * @param databaseName   string
   *                       Database name.
   * @return {
   * databaseWithObjectStores: DatabaseWithObjectStores  Database with an array of object stores.
   * }
   */
  JSONObject requestDatabase(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket, String databaseName);

  /**
   * Requests database names for given security origin.
   *
   * @param securityOrigin string
   *                       At least and at most one of securityOrigin, storageKey, or storageBucket must be specified. Security origin.
   * @param storageKey     string
   *                       Storage key.
   * @param storageBucket  Storage.StorageBucket
   *                       Storage bucket. If not specified, it uses the default bucket.
   * @return {
   * databaseNames: array[ string ]  Database names for origin.
   * }
   */
  JSONObject requestDatabaseNames(String securityOrigin, String storageKey, Storage.StorageBucket storageBucket);

  /**
   * Database with an array of object stores.
   */
  @Data
  public class DatabaseWithObjectStores {
    /**
     * Database name.
     */
    String name;
    /**
     * Database version (type is not 'integer', as the standard requires the version number to be 'unsigned long long')
     */
    Long version;
    /**
     * Object stores in this database.
     * array[ ObjectStore ]
     */
    List<ObjectStore> objectStores;
  }

  /**
   * Data entry.
   */
  @Data
  public class DataEntry {
    /**
     * Key object.
     */
    Runtime.RemoteObject key;
    /**
     * Primary key object.
     */
    Runtime.RemoteObject primaryKey;
    /**
     * Value object.
     */
    Runtime.RemoteObject value;
  }

  /**
   * Key.
   */
  @Data
  public class Key {
    /**
     * Key type.
     * Allowed Values: number, string, date, array
     */
    String type;
    /**
     * Number value.
     */
    Number number;
    /**
     * String value.
     */
    String string;
    /**
     * Date value.
     */
    Number date;
    /**
     * Array value.
     * array[ Key ]
     */
    List<Key> array;
  }

  /**
   * Key path.
   */
  @Data
  public class KeyPath {
    /**
     * Key path type.
     * Allowed Values: null, string, array
     */
    String type;
    /**
     * String value.
     */
    String string;
    /**
     * Array value.
     * array[ string ]
     */
    List<String> array;
  }

  /**
   * Key range.
   */
  @Data
  public class KeyRange {
    /**
     * Lower bound.
     */
    Key lower;
    /**
     * Upper bound.
     */
    Key upper;
    /**
     * If true lower bound is open.
     */
    boolean lowerOpen;
    /**
     * If true upper bound is open.
     */
    boolean upperOpen;
  }

  /**
   * Object store.
   */
  @Data
  public class ObjectStore {
    /**
     * Object store name.
     */
    String name;
    /**
     * Object store key path.
     */
    KeyPath keyPath;
    /**
     * If true, object store has auto increment flag set.
     */
    boolean autoIncrement;
    /**
     * Indexes in this object store.
     * array[ ObjectStoreIndex ]
     */
    List<ObjectStoreIndex> indexes;
  }

  /**
   * Object store index.
   */
  @Data
  public class ObjectStoreIndex {
    /**
     * Index name.
     */
    String name;
    /**
     * Index key path.
     */
    KeyPath keyPath;
    /**
     * If true, index is unique.
     */
    boolean unique;
    /**
     * If true, index allows multiple entries for a key.
     */
    boolean multiEntry;
  }

}
