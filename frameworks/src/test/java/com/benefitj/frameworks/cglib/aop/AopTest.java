package com.benefitj.frameworks.cglib.aop;

import org.junit.Test;

import java.util.Arrays;

public class AopTest {

  @Test
  public void testAround() {
    Person person = AopAdviceProxy.newProxy(new Person("小明"), new PointAdvice() {
      @Override
      public void onBefore(AopPointJoint joint) {
        System.err.println("onBefore: " + Arrays.toString(joint.getArgs()));
      }

      @Override
      public void onAfter(AopPointJoint joint) {
        System.err.println("onAfter: " + joint.getReturnValue());
      }

      @Override
      public void onAfterReturning(AopPointJoint joint) {
        System.err.println("onAfterReturning: " + joint.getReturnValue());
      }

      @Override
      public void onError(AopPointJoint joint, Throwable ex) {
        ex.printStackTrace();
      }
    });
    person.say("你好!");
    person.say2("你好222!");

  }

  @ProxyAround
  public static class Person {

    private String name;

    public Person() {
    }

    public Person(String name) {
      this.name = name;
    }

    public String say(String word) {
      System.err.println(name + " say: " + word);
      return "呵呵!";
    }

    /**
     * 不支持
     */
    String say2(String word) {
      System.err.println(name + " say: " + word);
      return "呵呵!";
    }

  }

}
