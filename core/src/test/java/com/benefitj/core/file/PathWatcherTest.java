package com.benefitj.core.file;

import com.benefitj.core.BaseTest;
import com.benefitj.core.DateFmtter;
import org.junit.Test;

import java.nio.file.Paths;

public class PathWatcherTest extends BaseTest {

  /**
   * 测试监听
   */
  @Test
  public void testPathWatcher() {
    new PathWatcher(Paths.get("D:/home"))
        .setWatchEventListener((key, context, kind) -> {
          logger.info("文件：" + (context + " " + PathWatcher.ofDesc(kind)) + ", 发生事件：" + kind.name() +", " + DateFmtter.fmtNowS());
          // 无论注册了什么事件，都可能收到一个 OVERFLOW 事件（表名事件被丢失或者丢弃）,可以处理或则忽略
        })
        .start();
  }

}
