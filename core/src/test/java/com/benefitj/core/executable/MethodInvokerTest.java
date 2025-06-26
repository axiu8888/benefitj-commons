package com.benefitj.core.executable;

import com.benefitj.core.BaseTest;
import com.benefitj.core.HexUtils;
import com.benefitj.core.ReflectUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class MethodInvokerTest extends BaseTest {

  @Test
  public void testInvoke() {
//    Object[] args = new Object[] {"/device/110101000", new byte[]{0x01, 0x02, 0x03}};
    Object[] args = new Object[]{new byte[]{0x01, 0x02, 0x03}, "/device/110101000", "hello world"};

    Method method = ReflectUtils.findFirstMethod(TestAbc.class, "onMessage");
    SimpleMethodInvoker invoker = new SimpleMethodInvoker(new TestAbc(), method);
    invoker.invoke(args);
  }

  public static class TestAbc {

    public void onMessage(String topic, byte[] payload, String msg) {
      System.err.println(
          "topic: " + topic
              + ", payload: " + HexUtils.bytesToHex(payload)
              + ", msg: " + msg
      );
    }

  }
}