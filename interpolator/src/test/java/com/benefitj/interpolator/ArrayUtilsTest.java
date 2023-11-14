package com.benefitj.interpolator;

import com.benefitj.core.StackLogger;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Arrays;

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


  public void tearDown() throws Exception {
  }
}