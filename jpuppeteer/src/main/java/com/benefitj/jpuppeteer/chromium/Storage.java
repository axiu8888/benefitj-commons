package com.benefitj.jpuppeteer.chromium;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

@ChromiumApi("Storage")
public interface Storage {

  /**
   * Clears cookies.
   *
   * @param browserContextId Browser.BrowserContextID
   *                         Browser context to use when called on the browser endpoint.
   */
  void clearCookies(String browserContextId);

  /**
   * Clears storage for origin.
   *
   * @param origin       string
   *                     Security origin.
   * @param storageTypes string
   *                     Comma separated list of StorageType to clear.
   */
  void clearDataForOrigin(String origin, String storageTypes);

  /**
   * Clears storage for storage key.
   *
   * @param storageKey   string
   *                     Storage key.
   * @param storageTypes string
   *                     Comma separated list of StorageType to clear.
   */
  void clearDataForStorageKey(String storageKey, String storageTypes);

  /**
   * Returns all browser cookies.
   *
   * @param browserContextId Browser.BrowserContextID
   *                         Browser context to use when called on the browser endpoint.
   * @return {
   * cookies: array[ Network.Cookie ] Array of cookie objects.
   * }
   */
  JSONObject getCookies(String browserContextId);

  /**
   * Returns a storage key given a frame id.
   *
   * @param frameId Page.FrameId
   * @return {
   * storageKey: SerializedStorageKey
   * }
   */
  JSONObject getStorageKeyForFrame(String frameId);

  /**
   * Returns usage and quota in bytes.
   *
   * @param origin string
   *               Security origin.
   * @return {
   * usage: number  Storage usage (bytes).
   * quota: number  Storage quota (bytes).
   * overrideActive: boolean  Whether or not the origin has an active storage quota override
   * usageBreakdown: array[ UsageForType ]  Storage usage per type (bytes).
   * }
   */
  JSONObject getUsageAndQuota(String origin);

  /**
   * Sets given cookies.
   *
   * @param cookies          array[ Network.CookieParam ]
   *                         Cookies to be set.
   * @param browserContextId Browser.BrowserContextID
   *                         Browser context to use when called on the browser endpoint.
   */
  void setCookies(String cookies, String browserContextId);

  /**
   * Registers origin to be notified when an update occurs to its cache storage list.
   *
   * @param origin string
   *               Security origin.
   */
  void trackCacheStorageForOrigin(String origin);

  /**
   * Registers storage key to be notified when an update occurs to its cache storage list.
   *
   * @param storageKey string
   *                   Storage key.
   */
  void trackCacheStorageForStorageKey(String storageKey);

  /**
   * Registers origin to be notified when an update occurs to its IndexedDB.
   *
   * @param origin string
   *               Security origin.
   */
  void trackIndexedDBForOrigin(String origin);

  /**
   * Registers storage key to be notified when an update occurs to its IndexedDB.
   *
   * @param storageKey string
   *                   Storage key.
   */
  void trackIndexedDBForStorageKey(String storageKey);

  /**
   * Unregisters origin from receiving notifications for cache storage.
   *
   * @param origin string
   *               Security origin.
   */
  void untrackCacheStorageForOrigin(String origin);

  /**
   * Unregisters storage key from receiving notifications for cache storage.
   *
   * @param storageKey string
   *                   Storage key.
   */
  void untrackCacheStorageForStorageKey(String storageKey);

  /**
   * Unregisters origin from receiving notifications for IndexedDB.
   *
   * @param origin string
   *               Security origin.
   */
  void untrackIndexedDBForOrigin(String origin);

  /**
   * Unregisters storage key from receiving notifications for IndexedDB.
   *
   * @param storageKey string
   *                   Storage key.
   */
  void untrackIndexedDBForStorageKey(String storageKey);

  /**
   * Clears all entries for a given origin's shared storage.
   *
   * @param ownerOrigin string
   */
  void clearSharedStorageEntries(String ownerOrigin);

  /**
   * Removes all Trust Tokens issued by the provided issuerOrigin. Leaves other stored data, including the issuer's Redemption Records, intact.
   *
   * @param issuerOrigin string
   * @return {
   * didDeleteTokens: boolean  True if any tokens were deleted, false otherwise.
   * }
   */
  JSONObject clearTrustTokens(String issuerOrigin);

  /**
   * Deletes entry for key (if it exists) for a given origin's shared storage.
   *
   * @param ownerOrigin string
   * @param key         string
   */
  void deleteSharedStorageEntry(String ownerOrigin, String key);

  /**
   * Deletes the Storage Bucket with the given storage key and bucket name.
   *
   * @param bucket StorageBucket
   */
  void deleteStorageBucket(StorageBucket bucket);

  /**
   * Gets details for a named interest group.
   *
   * @param ownerOrigin string
   * @param name        string
   * @return {
   * details:  InterestGroupDetails
   * }
   */
  JSONObject getInterestGroupDetails(String ownerOrigin, String name);

  /**
   * Gets the entries in an given origin's shared storage.
   *
   * @param ownerOrigin string
   * @return {
   * entries: array[ SharedStorageEntry ]
   * }
   */
  JSONObject getSharedStorageEntries(String ownerOrigin);

  /**
   * Gets metadata for an origin's shared storage.
   *
   * @param ownerOrigin string
   * @return {
   * metadata: SharedStorageMetadata
   * }
   */
  JSONObject getSharedStorageMetadata(String ownerOrigin);

  /**
   * Returns the number of stored Trust Tokens per issuer for the current browsing context.
   *
   * @return {
   * tokens: array[ TrustTokens ]
   * }
   */
  JSONObject getTrustTokens();

  /**
   * Override quota for the specified origin
   *
   * @param origin    string
   *                  Security origin.
   * @param quotaSize number
   *                  The quota size (in bytes) to override the original quota with. If this is called multiple times, the overridden quota will
   *                  be equal to the quotaSize provided in the final call. If this is called without specifying a quotaSize, the quota will be
   *                  reset to the default value for the specified origin. If this is called multiple times with different origins, the override
   *                  will be maintained for each origin until it is disabled (called without a quotaSize).
   */
  void overrideQuotaForOrigin(String origin, String quotaSize);

  /**
   * Resets the budget for ownerOrigin by clearing all budget withdrawals.
   *
   * @param ownerOrigin string
   */
  void resetSharedStorageBudget(String ownerOrigin);

  /**
   * Deletes state for sites identified as potential bounce trackers, immediately.
   *
   * @return {
   * deletedSites: array[ string ]
   * }
   */
  JSONObject runBounceTrackingMitigations(String origin);

  /**
   * https://wicg.github.io/attribution-reporting-api/
   *
   * @param enabled boolean
   *                If enabled, noise is suppressed and reports are sent immediately.
   */
  void setAttributionReportingLocalTestingMode(Boolean enabled);


  /**
   * Enables/disables issuing of Attribution Reporting events.
   *
   * @param enable boolean
   */
  void setAttributionReportingTracking(Boolean enable);

  /**
   * Enables/Disables issuing of interestGroupAccessed events.
   *
   * @param enable boolean
   */
  void setInterestGroupTracking(Boolean enable);

  /**
   * Sets entry with key and value for a given origin's shared storage.
   *
   * @param ownerOrigin     string
   * @param key             string
   * @param value           string
   * @param ignoreIfPresent boolean
   *                        If ignoreIfPresent is included and true, then only sets the entry if key doesn't already exist.
   */
  void setSharedStorageEntry(String ownerOrigin, String key, String value, Boolean ignoreIfPresent);

  /**
   * Enables/disables issuing of sharedStorageAccessed events.
   *
   * @param enable boolean
   */
  void setSharedStorageTracking(Boolean enable);

  /**
   * Set tracking for a storage key's buckets.
   *
   * @param storageKey string
   * @param enable     boolean
   */
  void setStorageBucketTracking(String storageKey, Boolean enable);

  @Event("Storage")
  public interface Events {

    /**
     * A cache's contents have been modified.
     *
     * @param origin     string
     *                   Origin to update.
     * @param storageKey string
     *                   Storage key to update.
     * @param bucketId   string
     *                   Storage bucket to update.
     * @param cacheName  string
     *                   Name of cache in origin.
     */
    @Event("cacheStorageContentUpdated")
    void cacheStorageContentUpdated(String origin, String storageKey, String bucketId, String cacheName);

    /**
     * A cache has been added/deleted.
     *
     * @param origin     string
     *                   Origin to update.
     * @param storageKey string
     *                   Storage key to update.
     * @param bucketId   string
     *                   Storage bucket to update.
     */
    @Event("cacheStorageListUpdated")
    void cacheStorageListUpdated(String origin, String storageKey, String bucketId);

    /**
     * The origin's IndexedDB object store has been modified.
     *
     * @param origin          string
     *                        Origin to update.
     * @param storageKey      string
     *                        Storage key to update.
     * @param bucketId        string
     *                        Storage bucket to update.
     * @param databaseName    string
     *                        Database to update.
     * @param objectStoreName string
     *                        ObjectStore to update.
     */
    @Event("indexedDBContentUpdated")
    void indexedDBContentUpdated(String origin, String storageKey, String bucketId, String databaseName, String objectStoreName);

    /**
     * The origin's IndexedDB database list has been modified.
     *
     * @param origin     string
     *                   Origin to update.
     * @param storageKey string
     *                   Storage key to update.
     * @param bucketId   string
     *                   Storage bucket to update.
     */
    @Event("indexedDBListUpdated")
    void indexedDBListUpdated(String origin, String storageKey, String bucketId);

    /**
     * One of the interest groups was accessed by the associated page.
     *
     * @param accessTime  Network.TimeSinceEpoch
     * @param type        InterestGroupAccessType
     * @param ownerOrigin string
     * @param name        string
     */
    @Event("interestGroupAccessed")
    void interestGroupAccessed(Long accessTime, InterestGroupAccessType type, String ownerOrigin, String name);

    /**
     * Shared storage was accessed by the associated page. The following parameters are included in all events.
     *
     * @param accessTime  Network.TimeSinceEpoch
     *                    Time of the access.
     * @param type        SharedStorageAccessType
     *                    Enum value indicating the Shared Storage API method invoked.
     * @param mainFrameId Page.FrameId
     *                    DevTools Frame Token for the primary frame tree's root.
     * @param ownerOrigin string
     *                    Serialized origin for the context that invoked the Shared Storage API.
     * @param params      SharedStorageAccessParams
     *                    The sub-parameters warapped by params are all optional and their presence/absence depends on type.
     */
    @Event("sharedStorageAccessed")
    void sharedStorageAccessed(Long accessTime, SharedStorageAccessType type, String mainFrameId, String ownerOrigin, SharedStorageAccessParams params);

    /**
     * @param bucketInfo StorageBucketInfo
     */
    @Event("storageBucketCreatedOrUpdated")
    void storageBucketCreatedOrUpdated(StorageBucketInfo bucketInfo);

    /**
     * @param bucketId string
     */
    @Event("storageBucketDeleted")
    void storageBucketDeleted(String bucketId);

    /**
     * TODO(crbug.com/1458532): Add other Attribution Reporting events, e.g. trigger registration.
     *
     * @param registration AttributionReportingSourceRegistration
     * @param result       AttributionReportingSourceRegistrationResult
     */
    @Event("attributionReportingSourceRegistered")
    void attributionReportingSourceRegistered(AttributionReportingSourceRegistration registration, AttributionReportingSourceRegistrationResult result);


  }

  /**
   * Enum of interest group access types.
   * Allowed Values: join, leave, update, loaded, bid, win
   */
  public enum InterestGroupAccessType {
    join, leave, update, loaded, bid, win
  }

  /**
   * Ad advertising element inside an interest group.
   */
  @Data
  public class InterestGroupAd {
    String renderUrl;
    String metadata;
  }

  /**
   * The full details of an interest group.
   */
  @Data
  public class InterestGroupDetails {
    /**
     *
     */
    String ownerOrigin;
    /**
     *
     */
    String name;
    /**
     * Network.TimeSinceEpoch
     */
    Long expirationTime;
    /**
     *
     */
    String joiningOrigin;
    /**
     *
     */
    String biddingUrl;
    /**
     *
     */
    String biddingWasmHelperUrl;
    /**
     *
     */
    String updateUrl;
    /**
     *
     */
    String trustedBiddingSignalsUrl;
    /**
     * array[ string ]
     */
    List<String> trustedBiddingSignalsKeys;
    /**
     *
     */
    String userBiddingSignals;
    /**
     * array[ InterestGroupAd ]
     */
    List<InterestGroupAd> ads;
    /**
     * array[ InterestGroupAd ]
     */
    List<InterestGroupAd> adComponents;
  }

  /**
   * Bundles the parameters for shared storage access events whose presence/absence can vary according to SharedStorageAccessType.
   */
  @Data
  public class SharedStorageAccessParams {
    /**
     * Spec of the module script URL. Present only for SharedStorageAccessType.documentAddModule.
     */
    String scriptSourceUrl;

    /**
     * Name of the registered operation to be run. Present only for SharedStorageAccessType.documentRun and SharedStorageAccessType.documentSelectURL.
     */
    String operationName;

    /**
     * The operation's serialized data in bytes (converted to a string). Present only for SharedStorageAccessType.documentRun and SharedStorageAccessType.documentSelectURL.
     */
    String serializedData;

    /**
     * array[ SharedStorageUrlWithMetadata ]
     * Array of candidate URLs' specs, along with any associated metadata. Present only for SharedStorageAccessType.documentSelectURL.
     */
    List<SharedStorageUrlWithMetadata> urlsWithMetadata;
    /**
     * Key for a specific entry in an origin's shared storage. Present only for SharedStorageAccessType.documentSet, SharedStorageAccessType.documentAppend, SharedStorageAccessType.documentDelete, SharedStorageAccessType.workletSet, SharedStorageAccessType.workletAppend, SharedStorageAccessType.workletDelete, and SharedStorageAccessType.workletGet.
     */
    String key;
    /**
     * Value for a specific entry in an origin's shared storage. Present only for SharedStorageAccessType.documentSet, SharedStorageAccessType.documentAppend, SharedStorageAccessType.workletSet, and SharedStorageAccessType.workletAppend.
     */
    String value;
    /**
     * Whether or not to set an entry for a key if that key is already present. Present only for SharedStorageAccessType.documentSet and SharedStorageAccessType.workletSet.
     */
    boolean ignoreIfPresent;
  }

  /**
   * Enum of shared storage access types.
   * Allowed Values: documentAddModule, documentSelectURL, documentRun, documentSet, documentAppend, documentDelete, documentClear,
   * workletSet, workletAppend, workletDelete, workletClear, workletGet, workletKeys, workletEntries, workletLength, workletRemainingBudget
   */
  public enum SharedStorageAccessType {
    documentAddModule, documentSelectURL, documentRun, documentSet, documentAppend, documentDelete, documentClear, workletSet,
    workletAppend, workletDelete, workletClear, workletGet, workletKeys, workletEntries, workletLength, workletRemainingBudget
  }

  /**
   * Struct for a single key-value pair in an origin's shared storage.
   */
  @Data
  public class SharedStorageEntry {
    String key;
    String value;
  }

  /**
   * Details for an origin's shared storage.
   */
  @Data
  public class SharedStorageMetadata {
    /**
     * Network.TimeSinceEpoch
     */
    Long creationTime;
    Integer length;
    Number remainingBudget;
  }

  /**
   * Pair of reporting metadata details for a candidate URL for selectURL().
   */
  @Data
  public class SharedStorageReportingMetadata {
    String eventType;
    String reportingUrl;
  }

  /**
   * Bundles a candidate URL with its reporting metadata.
   */
  @Data
  public class SharedStorageUrlWithMetadata {
    /**
     * Spec of candidate URL.
     */
    String url;
    /**
     * array[ SharedStorageReportingMetadata ]
     * Any associated reporting metadata.
     */
    List<SharedStorageReportingMetadata> reportingMetadata;
  }

  /**
   *
   */
  @Data
  public class StorageBucket {
    String storageKey;
    /**
     * If not specified, it is the default bucket of the storageKey.
     */
    String name;
  }

  /**
   *
   */
  @Data
  public class StorageBucketInfo {
    /**
     *
     */
    StorageBucket bucket;
    String id;
    /**
     * Network.TimeSinceEpoch
     */
    Long expiration;
    /**
     * Storage quota (bytes).
     */
    Number quota;
    boolean persistent;
    StorageBucketsDurability durability;
  }

  /**
   * Allowed Values: relaxed, strict
   */
  public enum StorageBucketsDurability {
    relaxed, strict
  }

  /**
   * Enum of possible storage types.
   * Allowed Values: appcache, cookies, file_systems, indexeddb, local_storage, shader_cache, websql, service_workers,
   * cache_storage, interest_groups, shared_storage, storage_buckets, all, other
   */
  public enum StorageType {
    appcache, cookies, file_systems, indexeddb, local_storage, shader_cache, websql, service_workers,
    cache_storage, interest_groups, shared_storage, storage_buckets, all, other
  }

  /**
   * Usage for a storage type.
   */
  @Data
  public class UsageForType {
    /**
     * Name of storage type.
     */
    StorageType storageType;
    /**
     * Storage usage (bytes).
     */
    Number usage;
  }

  /**
   *
   */
  @Data
  public class AttributionReportingAggregationKeysEntry {
    String key;
    String value;
  }

  /**
   *
   */
  @Data
  public class AttributionReportingFilterDataEntry {
    String key;
    /**
     * array[ string ]
     */
    List<String> values;
  }

  /**
   *
   */
  @Data
  public class AttributionReportingSourceRegistration {
    /**
     * Network.TimeSinceEpoch
     */
    Long time;
    /**
     * duration in seconds
     */
    Integer expiry;
    /**
     * duration in seconds
     */
    Integer eventReportWindow;
    /**
     * duration in seconds
     */
    Integer aggregatableReportWindow;
    /**
     *
     */
    AttributionReportingSourceType type;
    /**
     *
     */
    String sourceOrigin;
    /**
     *
     */
    String reportingOrigin;
    /**
     * array[ string ]
     */
    List<String> destinationSites;
    String eventId;
    String priority;
    /**
     * array[ AttributionReportingFilterDataEntry ]
     */
    List<AttributionReportingFilterDataEntry> filterData;
    /**
     * array[ AttributionReportingAggregationKeysEntry ]
     */
    List<AttributionReportingAggregationKeysEntry> aggregationKeys;
    String debugKey;
  }

  /**
   * Allowed Values: success, internalError, insufficientSourceCapacity, insufficientUniqueDestinationCapacity, excessiveReportingOrigins,
   * prohibitedByBrowserPolicy, successNoised, destinationReportingLimitReached, destinationGlobalLimitReached,
   * destinationBothLimitsReached, reportingOriginsPerSiteLimitReached, exceedsMaxChannelCapacity
   */
  public enum AttributionReportingSourceRegistrationResult {
    success, internalError, insufficientSourceCapacity, insufficientUniqueDestinationCapacity, excessiveReportingOrigins,
    prohibitedByBrowserPolicy, successNoised, destinationReportingLimitReached, destinationGlobalLimitReached,
    destinationBothLimitsReached, reportingOriginsPerSiteLimitReached, exceedsMaxChannelCapacity
  }

  /**
   * Allowed Values: navigation, event
   */
  public enum AttributionReportingSourceType {
    navigation, event
  }

  /**
   * Pair of issuer origin and number of available (signed, but not used) Trust Tokens from that issuer.
   */
  @Data
  public class TrustTokens {
    String issuerOrigin;
    Integer count;
  }

}
