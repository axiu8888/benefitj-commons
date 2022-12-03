package com.benefitj.core.file;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PathWatcher implements Cloneable {
  /**
   * 监听服务
   */
  private final WatchService watchService;
  /**
   * 执行状态
   */
  private final AtomicBoolean running = new AtomicBoolean(false);
  /**
   * 监听的路径
   */
  private final List<Path> paths;
  /**
   * 敏感度
   */
  private SensitivityWatchEventModifier sensitivity;
  /**
   * 监听的类型
   */
  private WatchEvent.Kind[] kinds = new WatchEvent.Kind[]{
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE,
      StandardWatchEventKinds.OVERFLOW
  };
  /**
   * 监听
   */
  private OnWatchEventListener watchEventListener;

  public PathWatcher(Path... paths) {
    this(Arrays.asList(paths), SensitivityWatchEventModifier.MEDIUM);
  }

  public PathWatcher(List<Path> paths, SensitivityWatchEventModifier sensitivity) {
    this.paths = Collections.unmodifiableList(paths);
    this.sensitivity = sensitivity;
    this.watchService = CatchUtils.tryThrow(() -> FileSystems.getDefault().newWatchService());
  }

  public PathWatcher start() {
    if (running.compareAndSet(false, true)) {
      execute();
    }
    return this;
  }

  private void execute() {
    CatchUtils.tryThrow(() -> {
      try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
        // 注册文件 创建、修改 事件的监听
        List<Path> paths = getPaths();
        paths.forEach(path -> {
          try {
            path.register(watchService, getKinds(), getSensitivity());
          } catch (IOException e) {
            getWatchEventListener().onError(path, e);
          }
        });
        watching: while (running.get()) {
          //返回排队的 key。如果没有排队的密钥可用，则此方法等待。
          WatchKey key = watchService.take();
          try {
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
              try {
                WatchEvent.Kind<?> kind = watchEvent.kind();
                WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                //检索与事件关联的文件名。文件名被存储为事件的上下文
                getWatchEventListener().onEvent(key, watchEventPath.context(), kind);
              } catch (Exception e) {
                getWatchEventListener().onError(((WatchEvent<Path>) watchEvent).context(), e);
              }
            }
          } finally {
            // 在处理完事件后，需要通过 reset() 将事件重置 ready 状态。
            // 如果此方法返回false，则该 key 不再有效，循环可以退出。
            boolean valid = key.reset();
            if (!valid) {
              break watching;
            }
          }
        }
      }
    }, Throwable::printStackTrace);
  }

  public PathWatcher stop() {
    running.set(false);
    IOUtils.closeQuietly(watchService);
    return this;
  }

  protected WatchService getWatchService() {
    return watchService;
  }

  public List<Path> getPaths() {
    return paths;
  }

  public SensitivityWatchEventModifier getSensitivity() {
    return sensitivity;
  }

  public PathWatcher setSensitivity(SensitivityWatchEventModifier sensitivity) {
    this.sensitivity = sensitivity;
    return this;
  }

  public WatchEvent.Kind[] getKinds() {
    return kinds;
  }

  public PathWatcher setKinds(WatchEvent.Kind[] kinds) {
    this.kinds = kinds;
    return this;
  }

  public OnWatchEventListener getWatchEventListener() {
    return watchEventListener;
  }

  public PathWatcher setWatchEventListener(OnWatchEventListener watchEventListener) {
    this.watchEventListener = watchEventListener;
    return this;
  }

  public interface OnWatchEventListener {

    /**
     * 事件
     *
     * @param key      键
     * @param filename 文件路径
     * @param kind     事件类型
     */
    void onEvent(WatchKey key, Path filename, WatchEvent.Kind<?> kind);

    /**
     * 出现异常
     *
     * @param context 路径
     * @param e       异常
     */
    default void onError(Path context, Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 描述
   */
  public static String ofDesc(WatchEvent.Kind<?> kind) {
    if (kind == StandardWatchEventKinds.OVERFLOW) {
      return "溢出";
    } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
      return "创建";
    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
      return "删除";
    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
      return "修改";
    }
    return "";
  }
}
