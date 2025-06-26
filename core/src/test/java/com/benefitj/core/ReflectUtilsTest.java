package com.benefitj.core;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.executable.FieldProxy;
import com.benefitj.core.lambda.LambdaMeta;
import com.benefitj.core.lambda.LambdaUtils;
import com.benefitj.core.reflection.FieldDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
public class ReflectUtilsTest extends BaseTest {

  @Test
  public void testNewInstance() {

    ReflectUtils.newInstance(TestA.class);
    ReflectUtils.newInstance(TestA.class, "厉害", new Date());
    ReflectUtils.newInstance(TestA.class, null, new Date());
    try {
      ReflectUtils.newInstance(TestA.class, 10, new Date());
    } catch (Exception e) {
      log.info("抛异常了：" + e.getMessage());
    }

    ReflectUtils.newInstance(TestA.class, new Date(), "厉害2");
  }

  @Test
  public void testFIndTYpe() {

    TestAbb<Integer> abb = new TestAbb<>();
    abb.setPoint(200);

//    printType(ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("point")));
//    printType(ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("value")));
//    printType(ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("items")));
//
//    printType(ReflectUtils.getField(TestAcc.class, f -> f.getName().equals("point")));

//    printType(TestAcc.class.getSimpleName(), TestAcc.class.getGenericSuperclass());


    Field field;
    field = ReflectUtils.findFirstField(abb.getClass(), f -> f.getName().equals("point"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("value"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("items"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("items2"));
    FieldDescriptor fd = FieldDescriptor.of(field);
    log.info("getActualType: {}", fd.getActualType(abb));
    log.info("getActualType: {}", fd.getActualType(null));
    log.info("getGenericTypeName: {}", fd.getTypeName());
//    log.info("isAssignableFrom: " + fieldType.isAssignableFrom(int.class));


    log.info("1 {}", TestAbb.class.isAssignableFrom(TestAbbSon.class));
    log.info("2 {}", TestAbbSon.class.isAssignableFrom(TestAbb.class));


  }

  public static void printType(Field f) {
    printType(f.getName(), f.getGenericType());
  }

  public static void printType(String name, Type genericType) {
    log.info("\n---------------- " + name + " ----------------");

    log.info("{} ==>: {}", name, genericType);

    if (genericType instanceof ParameterizedType) {
      log.info("{} ==>: ParameterizedType", name);
      ParameterizedType pt = (ParameterizedType) genericType;
      log.info("{}.getTypeName ==>: {}", name, pt.getTypeName());
      log.info("{}.getActualTypeArguments ==>: {}", name, Arrays.toString(pt.getActualTypeArguments()));
      log.info("{}.getOwnerType ==>: {}", name, pt.getOwnerType());
      log.info("{}.getRawType ==>: {}", name, pt.getRawType());
    }

    if (genericType instanceof TypeVariable) {
      log.info("{} ==>: TypeVariable", name);
      TypeVariable tv = (TypeVariable) genericType;
      log.info("{}.getName ==>: {}", name, tv.getName());
      log.info("{}.getTypeName ==>: {}", name, tv.getTypeName());
      log.info("{}.getBounds ==>: {}", name, Arrays.toString(tv.getBounds()));
      log.info("{}.getAnnotatedBounds ==>: {}", name, Arrays.toString(tv.getAnnotatedBounds()));
      log.info("{}.getGenericDeclaration ==>: {}", name, tv.getGenericDeclaration());
    }

    if (genericType instanceof WildcardType) {
      log.info("{} ==>: WildcardType", name);
      WildcardType wt = (WildcardType) genericType;
      log.info("{}.getTypeName ==>: {}", name, wt.getTypeName());
      log.info("{}.getUpperBounds ==>: {}", name, Arrays.toString(wt.getUpperBounds()));
      log.info("{}.getLowerBounds ==>: {}", name, Arrays.toString(wt.getLowerBounds()));
    }

    log.info("---------------- {} ----------------\n", name);
  }

  @Test
  public void testSerializable() {
    LambdaMeta lambda = LambdaUtils.getLambda(SysUser::getName);
    log.info(lambda.getImplMethodName());
  }

  @Test
  public void test_$etter() {

    FieldProxy fp = FieldProxy.create(ReflectUtils.findFirstField(SysUser.class, "name"), false);
    SysUser su = new SysUser();
    log.info("su ->: {}", JSON.toJSONString(su));

    fp.set(su, "你好");
    log.info("su ->: {}", JSON.toJSONString(su));
    log.info("name ->: {}", fp.<String>get(su));

    fp.setMethodFirst(true);
    fp.set(su, "你好222");
    log.info("su ->: {}", JSON.toJSONString(su));
    log.info("name ->: {}", fp.<String>get(su));

  }



  public static class SysUser {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
      log.info("setName方法被调用了...");
    }
  }

  static class TestA {
    public TestA() {
      System.err.printf("3. name[%s], birthday[%s]%n", "...", "...");
    }

    public TestA(Date birthday, String name) {
      System.err.printf("2. name[%s], birthday[%s]%n", name, DateFmtter.fmtDate(birthday));
    }

    public TestA(String name, Date birthday) {
      System.err.printf("1. name[%s], birthday[%s]%n", name, DateFmtter.fmtDate(birthday));
    }

  }

  static class TestAbbSon extends TestAbb<Integer> {

  }

  static class TestAbb<T> {

    T point;

    int value;

    List<? extends Number> items;
    List<T> items2;

    public T getPoint() {
      return point;
    }

    public void setPoint(T point) {
      this.point = point;
    }
  }


  static class TestAcc<T extends Number> extends TestAbb<T> {
  }

}
