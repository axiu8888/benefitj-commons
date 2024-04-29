package com.benefitj.core.log;

import org.slf4j.Logger;

/**
 * 日志等级
 */
public enum Slf4jLevel {
  TRACE() {
    @Override
    public void print(Logger log, String msg) {
      log.trace(msg);
    }

    @Override
    public void print(Logger log, String format, Object... arguments) {
      log.trace(format, arguments);
    }

    @Override
    public void print(Logger log, String msg, Throwable t) {
      log.trace(msg, t);
    }
  },
  DEBUG() {
    @Override
    public void print(Logger log, String msg) {
      log.debug(msg);
    }

    @Override
    public void print(Logger log, String format, Object... arguments) {
      log.debug(format, arguments);
    }

    @Override
    public void print(Logger log, String msg, Throwable t) {
      log.debug(msg, t);
    }
  },
  INFO() {
    @Override
    public void print(Logger log, String msg) {
      log.info(msg);
    }

    @Override
    public void print(Logger log, String format, Object... arguments) {
      log.info(format, arguments);
    }

    @Override
    public void print(Logger log, String msg, Throwable t) {
      log.info(msg, t);
    }
  },
  WARN() {
    @Override
    public void print(Logger log, String msg) {
      log.warn(msg);
    }

    @Override
    public void print(Logger log, String format, Object... arguments) {
      log.warn(format, arguments);
    }

    @Override
    public void print(Logger log, String msg, Throwable t) {
      log.warn(msg, t);
    }
  },
  ERROR() {
    @Override
    public void print(Logger log, String msg) {
      log.error(msg);
    }

    @Override
    public void print(Logger log, String format, Object... arguments) {
      log.error(format, arguments);
    }

    @Override
    public void print(Logger log, String msg, Throwable t) {
      log.error(msg, t);
    }
  },

  NONE();

  public void print(Logger log, String msg) {
  }

  public void print(Logger log, String format, Object... arguments) {
  }

  public void print(Logger log, String msg, Throwable t) {
  }
}
