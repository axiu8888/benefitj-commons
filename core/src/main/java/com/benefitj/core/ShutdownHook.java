package com.benefitj.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 销毁进程时的回调钩子
 */
public class ShutdownHook extends Thread {
  /**
   * @return 创建
   */
  public static ShutdownHook create() {
    return new ShutdownHook();
  }

  static final SingletonSupplier<ShutdownHook> singleton = SingletonSupplier.of(() -> new ShutdownHook("globalShutdownHook"));

  public static ShutdownHook get() {
    return singleton.get();
  }

  static {
    // 注册销毁时的回调
    Runtime.getRuntime().addShutdownHook(get());
  }

  /**
   * 注册销毁时的回调钩子
   *
   * @param hook 回调
   * @return 是否注册
   */
  public static boolean register(Runnable hook) {
    return get().addShutdownHook(hook);
  }

  /**
   * 取消注册销毁时的回调钩子
   *
   * @param hook 回调
   * @return 是否取消注册
   */
  public static boolean unregister(Runnable hook) {
    return get().removeShutdownHook(hook);
  }

  private final List<Runnable> hooks = new CopyOnWriteArrayList<>();

  private ShutdownHook() {
  }

  private ShutdownHook(String name) {
    super(name);
  }

  public List<Runnable> getHooks() {
    return hooks;
  }

  /**
   * 添加回调钩子
   *
   * @param hook 钩子
   * @return 返回是否添加，如果已存在，返回false
   */
  public boolean addShutdownHook(Runnable hook) {
    final List<Runnable> hooks = getHooks();
    return !hooks.contains(hook) && hooks.add(hook);
  }

  /**
   * 移除回调钩子
   *
   * @param hook 钩子
   * @return 是否移除
   */
  public boolean removeShutdownHook(Runnable hook) {
    return getHooks().remove(hook);
  }

  @Override
  public final void run() {
    final List<Runnable> hooks = getHooks();
    for (Runnable hook : hooks) {
      try {
        hook.run();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    hooks.clear();
  }
}
