package com.benefitj.netty.log;

/**
 * Netty日志
 */
public class NettyLogger implements INettyLogger {

  /**
   * 实例
   */
  public static final NettyLogger INSTANCE = new NettyLogger();

  private INettyLogger logger;

  public NettyLogger() {
  }

  public void setLogger(INettyLogger logger) {
    this.logger = logger;
  }

  public INettyLogger getLogger() {
    INettyLogger l = this.logger;
    if (l == null) {
      synchronized (this) {
        if ((l = this.logger) == null) {
          this.logger = (l = new DiscardNettyLogger());
        }
      }
    }
    return l;
  }

  @Override
  public void debug(String msg) {
    getLogger().debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    getLogger().debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    getLogger().debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    getLogger().debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    getLogger().debug(msg, t);
  }

  @Override
  public void info(String msg) {
    getLogger().info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    getLogger().info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    getLogger().info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    getLogger().info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    getLogger().info(msg, t);
  }

  @Override
  public void warn(String msg) {
    getLogger().warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    getLogger().warn(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    getLogger().warn(format, arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    getLogger().warn(format, arg1, arg2);
  }

  @Override
  public void warn(String msg, Throwable t) {
    getLogger().warn(msg, t);
  }

  @Override
  public void error(String msg) {
    getLogger().error(msg);
  }

  @Override
  public void error(String format, Object arg) {
    getLogger().error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    getLogger().error(format, arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    getLogger().error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    getLogger().error(msg, t);
  }
}
