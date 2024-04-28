package com.benefitj.frameworks.cache;

public class DiskLruCacheException extends RuntimeException {

  public DiskLruCacheException() {
  }

  public DiskLruCacheException(String message) {
    super(message);
  }

  public DiskLruCacheException(String message, Throwable cause) {
    super(message, cause);
  }

  public DiskLruCacheException(Throwable cause) {
    super(cause);
  }

  public DiskLruCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
