package com.benefitj.core;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
class EventLoopTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void execCron() {
//    final String cronExpress = "*/5 * * * * ?";
    final String cronExpress = "*/1 * * * * ?";
    final Runnable myTask = () -> {
      log.info("execute at: {}", DateFmtter.fmtNowS());
    };


    EventLoop loop = EventLoop.io();

    final CronRun run = new CronRun(cronExpress, loop, myTask) ;
    run.run();

    EventLoop.sleepSecond(Integer.MAX_VALUE);

  }

  public static class CronRun implements Runnable {

    private final String cronExpress;
    private final EventLoop loop;
    private final Runnable task;

    private CronParser parser;
    private Cron cron;
    private ExecutionTime executionTime;

    final AtomicBoolean lock = new AtomicBoolean(false);
    final AtomicReference<ZonedDateTime> now = new AtomicReference<>();

    public CronRun(String cronExpress, EventLoop loop, Runnable task) {
      this.cronExpress = cronExpress;
      this.loop = loop;
      this.task = task;
      this.parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
      this.cron = parser.parse(cronExpress);// "*/5 * * * * ?"
      this.executionTime = ExecutionTime.forCron(cron);
    }

    @Override
    public void run() {
      if (lock.compareAndSet(false, true)) {
        if (now.get() == null) now.set(ZonedDateTime.now());
        executionTime.nextExecution(now.get()).ifPresent(nextAt -> {
          long delay = Duration.between(now.get(), nextAt).toMillis();
          log.info("nextAt: {}, delay, {}", nextAt, delay);
          loop.schedule(() -> {
            //now.set(ZonedDateTime.now());
            try {
              task.run();
            } finally {
              lock.set(false);
              this.run();
            }
          }, delay, TimeUnit.MILLISECONDS);
        });
      }
    }
  }

}