package com.benefitj.core;

import java.util.Calendar;
import java.util.Date;


/**
 * 时间获取
 */
public class TimeGetter {

  final Calendar calendar;
  final Date time;

  public TimeGetter(long time) {
    this(new Date(time));
  }

  public TimeGetter(Date time) {
    this.time = time;
    this.calendar = Calendar.getInstance();
    this.calendar.setTime(time);
  }

  public Date getTime() {
    return time;
  }

  public long getTimeMillis() {
    return time.getTime();
  }

  public int get(int field) {
    return calendar.get(field);
  }

  public int getYear() {
    return get(Calendar.YEAR);
  }

  public int getMonth() {
    return get(Calendar.MONTH) + 1;
  }

  public int getDay() {
    return get(Calendar.DAY_OF_MONTH);
  }

  public int getHour() {
    return get(Calendar.HOUR_OF_DAY);
  }

  public int getMinute() {
    return get(Calendar.MINUTE);
  }

  public int getSecond() {
    return get(Calendar.SECOND);
  }

  public int getMillisecond() {
    return get(Calendar.MILLISECOND);
  }

}
