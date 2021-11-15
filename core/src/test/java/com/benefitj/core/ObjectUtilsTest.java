package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ObjectUtilsTest extends TestCase {


  @Test
  public void testObject_ToString() {
    Userinfo userinfo = new Userinfo();
    userinfo.setName("华天");
    userinfo.setGender("男");
    userinfo.setBirthday(DateFmtter.toDate(2000, 10, 8));
    userinfo.setArray(new byte[]{0x09, (byte) 0x88, (byte) 0x82});
    System.err.println(ObjectUtils.toString(userinfo, (field, o) ->
        (o instanceof byte[]) ? HexUtils.bytesToHex((byte[]) o) : ObjectUtils.defaultToString(field, o)));
  }


  public static class Userinfo {
    /**
     * 姓名
     */
    private String name;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 性别
     */
    private String gender;

    private byte[] array;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Date getBirthday() {
      return birthday;
    }

    public void setBirthday(Date birthday) {
      this.birthday = birthday;
    }

    public String getGender() {
      return gender;
    }

    public void setGender(String gender) {
      this.gender = gender;
    }

    public byte[] getArray() {
      return array;
    }

    public void setArray(byte[] array) {
      this.array = array;
    }
  }


}