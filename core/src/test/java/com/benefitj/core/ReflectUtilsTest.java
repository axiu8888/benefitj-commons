package com.benefitj.core;

import com.benefitj.core.reflection.FieldDescriptor;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReflectUtilsTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void testNewInstance() {

    ReflectUtils.newInstance(TestA.class);
    ReflectUtils.newInstance(TestA.class, "厉害", new Date());
    ReflectUtils.newInstance(TestA.class, null, new Date());
    try {
      ReflectUtils.newInstance(TestA.class, 10, new Date());
    } catch (Exception e) {
      System.err.println("抛异常了：" + e.getMessage());
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
    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("point"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("value"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("items"));
//    field = ReflectUtils.getField(abb.getClass(), f -> f.getName().equals("items2"));
    FieldDescriptor fd = FieldDescriptor.of(field);
    System.err.println("getActualType: " + fd.getActualType(abb));
    System.err.println("getActualType: " + fd.getActualType(null));
    System.err.println("getGenericTypeName: " + fd.getTypeName());
//    System.err.println("isAssignableFrom: " + fieldType.isAssignableFrom(int.class));


    System.err.println("1 " + (TestAbb.class.isAssignableFrom(TestAbbSon.class)));
    System.err.println("2 " + (TestAbbSon.class.isAssignableFrom(TestAbb.class)));


  }

  public static void printType(Field f) {
    printType(f.getName(), f.getGenericType());
  }

  public static void printType(String name, Type genericType) {
    System.err.println("\n---------------- " + name + " ----------------");

    System.err.println(name + " ==>: " + genericType);

    if (genericType instanceof ParameterizedType) {
      System.err.println(name + " ==>: ParameterizedType");
      ParameterizedType pt = (ParameterizedType) genericType;
      System.err.println(name + ".getTypeName ==>: " + (pt.getTypeName()));
      System.err.println(name + ".getActualTypeArguments ==>: " + Arrays.toString(pt.getActualTypeArguments()));
      System.err.println(name + ".getOwnerType ==>: " + (pt.getOwnerType()));
      System.err.println(name + ".getRawType ==>: " + (pt.getRawType()));
    }

    if (genericType instanceof TypeVariable) {
      System.err.println(name + " ==>: TypeVariable");
      TypeVariable tv = (TypeVariable) genericType;
      System.err.println(name + ".getName ==>: " + (tv.getName()));
      System.err.println(name + ".getTypeName ==>: " + (tv.getTypeName()));
      System.err.println(name + ".getBounds ==>: " + Arrays.toString(tv.getBounds()));
      System.err.println(name + ".getAnnotatedBounds ==>: " + Arrays.toString(tv.getAnnotatedBounds()));
      System.err.println(name + ".getGenericDeclaration ==>: " + (tv.getGenericDeclaration()));
    }

    if (genericType instanceof WildcardType) {
      System.err.println(name + " ==>: WildcardType");
      WildcardType wt = (WildcardType) genericType;
      System.err.println(name + ".getTypeName ==>: " + (wt.getTypeName()));
      System.err.println(name + ".getUpperBounds ==>: " + Arrays.toString(wt.getUpperBounds()));
      System.err.println(name + ".getLowerBounds ==>: " + Arrays.toString(wt.getLowerBounds()));
    }

    System.err.println("---------------- " + name + " ----------------\n");
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
