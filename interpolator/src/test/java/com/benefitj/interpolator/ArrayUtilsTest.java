package com.benefitj.interpolator;

import com.benefitj.core.StackLogger;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;

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

  public void tearDown() throws Exception {
  }
}