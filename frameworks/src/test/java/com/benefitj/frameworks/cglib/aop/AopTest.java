package com.benefitj.frameworks.cglib.aop;

import com.benefitj.core.ReflectUtils;
import com.benefitj.frameworks.cglib.CGLibProxy;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AopTest {

  @Test
  public void testAround() {

    Person person = checkProxy(new Person("小明"), new AopAdvice(){
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

  }

  public static <T> T checkProxy(T target, AopAdvice aopAdvice) {
    return checkProxy((Class<T>) target.getClass(), target, aopAdvice);
  }

  public static <T> T checkProxy(Class<? extends T> type, T target, AopAdvice advice) {
    List<Method> methods;
    if (type.isAnnotationPresent(AopAround.class)) {
      AopAround around = type.getAnnotation(AopAround.class);
      methods = ReflectUtils.getMethods(type, m
          -> m.isAnnotationPresent(AopAround.class) || (around.modifier() & m.getModifiers()) > 0);
    } else {
      methods = ReflectUtils.getMethods(type, m -> m.isAnnotationPresent(AopAround.class));
    }

    if (methods.isEmpty()) {
      return target;
    }

    final Map<Method, AopPointJoint> methodAdvices = new ConcurrentHashMap<>();
    return CGLibProxy.newProxy(type, (obj, method, args, proxy) -> {
      AopPointJointImpl joint = (AopPointJointImpl) methodAdvices.get(method);
      if (joint == null) {
        joint = (AopPointJointImpl) methodAdvices.computeIfAbsent(method, method1 -> {
          if (methods.contains(method1)) {
            return AopPointJoint.newPointJoint(proxy, target, method1, args);
          }
          return AopPointJoint.newPointJoint(false);
        });
      }

      if (joint.isSupport()) {
        joint.setArgs(args);
        try {
          advice.onBefore(joint);
          Object resultValue = proxy.invoke(target, args);
          joint.setReturnValue(resultValue);
          advice.onAfter(joint);
        } catch (Throwable e) {
          advice.onError(joint, e);
          throw e;
        } finally {
          advice.onAfterReturning(joint);
        }
        return joint.getReturnValue();
      }
      return proxy.invoke(target, args);
    });
  }


  @AopAround
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

  }

}
