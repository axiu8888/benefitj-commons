package com.benefitj.jdbc.sql;

public class IllegalSQLException extends RuntimeException {

  public IllegalSQLException() {
  }

  public IllegalSQLException(String message) {
    super(message);
  }

  public IllegalSQLException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalSQLException(Throwable cause) {
    super(cause);
  }

  public IllegalSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
