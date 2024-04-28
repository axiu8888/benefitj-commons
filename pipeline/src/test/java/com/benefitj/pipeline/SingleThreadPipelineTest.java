package com.benefitj.pipeline;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SingleThreadPipelineTest {

  protected Logger log = LoggerFactory.getLogger(getClass());

  @BeforeEach
  public void setUp() throws Exception {
    log.info("-------------  setUp   ----------------" + DateFmtter.fmtNowS());
  }

  @AfterEach
  public void tearDown() throws Exception {
    log.info("------------- tearDown ----------------" + DateFmtter.fmtNowS());
  }

  @Test
  void test() {
    log.info("test...");
    SingleThreadPipeline pipeline = SingleThreadPipeline.wrap(new DefaultPipeline(), EventLoop.single());
    pipeline.addAfter("test2223", new UnboundHandlerAdapter<Object>() {
      @Override
      protected void processPrev0(HandlerContext ctx, Object msg) {
        log.info("{}, {}.processPrev0, msg ==>: {}", EventLoop.threadName(), Utils.getInstanceName(this), msg);
        ctx.firePrev(msg);
      }

      @Override
      protected void processNext0(HandlerContext ctx, Object msg) {
        log.info("{}, {}.processNext0, msg ==>: {}", EventLoop.threadName(), Utils.getInstanceName(this), msg);
        ctx.fireNext(msg);
      }
    });

    pipeline.fireNext("hello world !");

    log.info("current ==>: {}", EventLoop.threadName());

    EventLoop.sleepSecond(5);

  }
}