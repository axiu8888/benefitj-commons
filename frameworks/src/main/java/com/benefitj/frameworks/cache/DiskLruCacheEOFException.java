package com.benefitj.frameworks.cache;

public class DiskLruCacheEOFException extends RuntimeException {

  public DiskLruCacheEOFException() {
  }

  public DiskLruCacheEOFException(String message) {
    super(message);
  }

  public DiskLruCacheEOFException(String message, Throwable cause) {
    super(message, cause);
  }

  public DiskLruCacheEOFException(Throwable cause) {
    super(cause);
  }

  public DiskLruCacheEOFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
