package com.benefit.vertx.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jVertxLogger implements VertxLogger {

  public static Slf4jVertxLogger create(String name, Level level) {
    return new Slf4jVertxLogger(LoggerFactory.getLogger(name), level);
  }

  public static final Slf4jVertxLogger NONE = new Slf4jVertxLogger(LoggerFactory.getLogger("vertx"), Level.NONE);

  final Logger log;
  final Level level;

  public Slf4jVertxLogger(Logger log, Level level) {
    this.log = log;
    this.level = level;
  }

  @Override
  public void trace(String msg) {
    level.trace(log, msg);
  }

  @Override
  public void trace(String format, Object... arguments) {
    level.trace(log, format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    level.trace(log, msg, t);
  }

  public enum Level {
    TRACE() {
      @Override
      public void trace(Logger log, String msg) {
        log.trace(msg);
      }

      @Override
      public void trace(Logger log, String format, Object... arguments) {
        log.trace(format, arguments);
      }

      @Override
      public void trace(Logger log, String msg, Throwable t) {
        log.trace(msg, t);
      }
    },
    DEBUG() {
      @Override
      public void trace(Logger log, String msg) {
        log.debug(msg);
      }

      @Override
      public void trace(Logger log, String format, Object... arguments) {
        log.debug(format, arguments);
      }

      @Override
      public void trace(Logger log, String msg, Throwable t) {
        log.debug(msg, t);
      }
    },
    INFO() {
      @Override
      public void trace(Logger log, String msg) {
        log.info(msg);
      }

      @Override
      public void trace(Logger log, String format, Object... arguments) {
        log.info(format, arguments);
      }

      @Override
      public void trace(Logger log, String msg, Throwable t) {
        log.info(msg, t);
      }
    },
    WARN() {
      @Override
      public void trace(Logger log, String msg) {
        log.warn(msg);
      }

      @Override
      public void trace(Logger log, String format, Object... arguments) {
        log.warn(format, arguments);
      }

      @Override
      public void trace(Logger log, String msg, Throwable t) {
        log.warn(msg, t);
      }
    },
    ERROR() {
      @Override
      public void trace(Logger log, String msg) {
        log.error(msg);
      }

      @Override
      public void trace(Logger log, String format, Object... arguments) {
        log.error(format, arguments);
      }

      @Override
      public void trace(Logger log, String msg, Throwable t) {
        log.error(msg, t);
      }
    },

    NONE();

    public void trace(Logger log, String msg) {
    }

    public void trace(Logger log, String format, Object... arguments) {
    }

    public void trace(Logger log, String msg, Throwable t) {
    }
  }
}
