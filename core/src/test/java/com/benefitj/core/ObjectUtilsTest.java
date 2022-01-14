package com.benefitj.core;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectUtilsTest extends TestCase {


  private final Logger logger = StackLogger.getLogger();

  @Test
  public void testObject_ToString() {
    Userinfo userinfo = new Userinfo();
    userinfo.setName("华天");
    userinfo.setGender("男");
    userinfo.setBirthday(TimeUtils.toDate(2000, 10, 8));
    userinfo.setArray(new byte[]{0x09, (byte) 0x88, (byte) 0x82});
    System.err.println(ObjectUtils.toString(userinfo, (field, o) ->
        (o instanceof byte[]) ? HexUtils.bytesToHex((byte[]) o) : ObjectUtils.defaultToString(field, o)));

    // 测试map
    Map<String, String> map = new LinkedHashMap<>();
    map.put("abc1", "hhh1");
    map.put("abc2", "hhh2");
    map.put("abc3", "hhh3");
    System.err.println(ObjectUtils.toString(map));

    logger.info(ObjectUtils.toString(map));

    logger.info(ObjectUtils.toString(new Object[]{
        new LinkedHashMap(){{
          put("1.key1", "value1");
          put("2.key2", "value2");
          put("3.key3", "value3");
        }},
        new LinkedHashMap(){{
          put("1.key1", "value1");
          put("2.key2", "value2");
          put("3.key3", "value3");
        }},
    }));

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