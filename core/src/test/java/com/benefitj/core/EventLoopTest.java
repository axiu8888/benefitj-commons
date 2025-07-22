package com.benefitj.core;

import com.benefitj.core.cron.CronExpression;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
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



  @Test
  void testCron2() {
    String cron = "0 15 10 * * ?"; // 每天10:15执行
    try {
      CronExpression cronExpression = new CronExpression(cron);

      // 计算下次执行时间
      Date now = new Date();
      Date nextTime = cronExpression.getNextValidTimeAfter(now);
      log.info("下次执行时间: {}", DateFmtter.fmtS(nextTime));

      EventLoop.io().scheduleCron("0/1 * * * * ?", new Runnable() {
        @Override
        public void run() {
          log.info("{} exec cron: {}", Integer.toHexString(this.hashCode()), DateFmtter.fmtNowS());
        }
      });

    } catch (ParseException e) {
      log.warn("无效的Cron表达式: {}", e.getMessage());
    }

    EventLoop.sleepSecond(10000);

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