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
  void test_SingleThreadPipeline() {
    log.info("test...");
    SingleThreadPipeline pipeline = SingleThreadPipeline.create();
    pipeline.addLast("test2223", new UnboundHandlerAdapter<Object>() {
      @Override
      protected void processPrev0(HandlerContext ctx, Object msg) {
        log.info("{}, {}.processPrev0, msg ==>: {}", EventLoop.getThreadName(), Utils.getInstanceName(this), msg);
        ctx.firePrev(msg);
      }

      @Override
      protected void processNext0(HandlerContext ctx, Object msg) {
        log.info("{}, {}.processNext0, msg ==>: {}", EventLoop.getThreadName(), Utils.getInstanceName(this), msg);
        ctx.fireNext(msg);
      }
    });

    for (int i = 0; i < 100; i++) {
      log.info("current ==>: {}", EventLoop.getThreadName());
      pipeline.firePrev("prev ==>: hello world");
      pipeline.fireNext("next ==>: hello world");
      EventLoop.sleepSecond(1);
    }

  }
}