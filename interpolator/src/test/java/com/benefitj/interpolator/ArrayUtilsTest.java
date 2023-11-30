package com.benefitj.interpolator;

import com.benefitj.core.StackLogger;
import com.benefitj.core.TimeUtils;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class ArrayUtilsTest extends TestCase {

  protected final Logger log = StackLogger.getLogger();

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testArrayToList() {
    int[] array = new int[]{1, 2, 3, 4, 5, 6, 7};

    long start = System.currentTimeMillis();
    log.info("array to short list: {}", ArrayUtils.arrayToShort(array));
    log.info("array to integer list: {}", ArrayUtils.arrayToInteger(array));
    log.info("array to long list: {}", ArrayUtils.arrayToLong(array));
    log.info("array to float list: {}", ArrayUtils.arrayToFloat(array));
    log.info("array to double list: {}", ArrayUtils.arrayToDouble(array));
    log.info("elapse: {}", System.currentTimeMillis() - start);

  }

  @Test
  public void test_SimpleInterpolator() {
    SimpleInterpolator<int[]> interpolator = new SimpleInterpolator<int[]>(100, 25){};

    log.info("interpolator.type =>: {}", interpolator.type);

    int[] src = new int[100];
    for (int i = 0; i < src.length; i++) {
      src[i] = i;
    }
    int[] dest = interpolator.process(src);
    log.info("dest: {}", Arrays.toString(dest));
    System.err.println("dest: " + Arrays.toString(dest));

  }

  @Test
  public void test_func() {
    log.info("sum: {}", ArrayUtils.sum(Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000)).longValue());
    log.info("mean: {}", ArrayUtils.mean(Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000)).longValue());
    log.info("mean: {}", ArrayUtils.mean(Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000)).doubleValue()); //
    log.info("max: {}", ArrayUtils.max(Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000)).longValue());
    log.info("min: {}", ArrayUtils.min(Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000)).longValue());


    List<Integer> list = Arrays.asList(1, 3, 5, 7, 9, 1024, 10240, 102400, 1024000, 10240000, 102400000);
    long startAt = TimeUtils.now();
    long max = 0;
    for (int i = 0; i < 1_000_000; i++) {
      max = Math.max(max, ArrayUtils.mean(list).longValue());
    }
    log.info("耗时: {}, {}", TimeUtils.diffNow(startAt), max);

  }

  public void tearDown() throws Exception {
  }
}