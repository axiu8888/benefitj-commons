package com.benefitj.core.executable;

import junit.framework.TestCase;
import org.junit.Test;

public class InstantiatorTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testCreate() {
    Instantiator instantiator = Instantiator.INSTANCE;

    TestAbc abc = instantiator.create(TestAbc.class, "abc");
    System.err.println(abc.name);

  }


  public void tearDown() throws Exception {
  }

  public static class TestAbc {

    private String name;

    public TestAbc() {
    }

    public TestAbc(String name) {
      this.name = name;
    }
  }
}