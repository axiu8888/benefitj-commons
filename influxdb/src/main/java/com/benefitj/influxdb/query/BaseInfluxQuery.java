package com.benefitj.influxdb.query;

import com.benefitj.influxdb.dto.InfluxCountInfo;
import com.benefitj.influxdb.template.InfluxDBTemplate;
import com.benefitj.influxdb.template.value.DefaultValueConverter;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

/**
 * 查询基类
 */
public abstract class BaseInfluxQuery<T extends InfluxDBTemplate> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static final ThreadLocal<Map<String, SimpleDateFormat>> sdfUtcMap = ThreadLocal.withInitial(WeakHashMap::new);

  private T template;

  public BaseInfluxQuery() {
  }

  public BaseInfluxQuery(T template) {
    this.template = template;
  }

  public void setTemplate(T template) {
    this.template = template;
  }

  public T getTemplate() {
    return template;
  }

  protected Query createQuery(String sql) {
    return new Query(sql, getTemplate().getDatabase());
  }

  /**
   * 统计
   *
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @return 返回统计信息
   */
  public InfluxCountInfo queryExactlyInfo(String measurement, String column, long startTime, long endTime) {
    return queryExactlyInfo(measurement, column, startTime, endTime, null);
  }

  /**
   * 统计
   *
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @param condition   其他条件
   * @return 返回统计信息
   */
  public InfluxCountInfo queryExactlyInfo(String measurement, String column, long startTime, long endTime, String condition) {
    String clause = String.format("FROM \"%s\" WHERE time >= '%s' AND time <= '%s' %s"
        , measurement, fmtUtcS(startTime), fmtUtcS(endTime), (condition != null ? condition : ""));
    // count
    String sql = String.format("SELECT count(%s) AS count %s", column, clause)
        // first
        + String.format("; SELECT %s AS first %s ORDER BY time ASC LIMIT 1", column, clause)
        // last
        + String.format("; SELECT %s AS last %s ORDER BY time DESC LIMIT 1", column, clause);
    logger.debug("查询统计信息, sql: {}", sql);
    final InfluxCountInfo countInfo = new InfluxCountInfo();
    getTemplate().query(createQuery(sql), 100, result -> {
      QueryResult qr = (QueryResult) result;
      if (qr.hasError()) {
        countInfo.setError(qr.getError());
        return;
      }

      final DefaultValueConverter c = new DefaultValueConverter();
      qr.getResults()
          .stream()
          .filter(r -> !r.hasError())
          .filter(r -> r.getSeries() != null && !r.getSeries().isEmpty())
          .flatMap(r -> r.getSeries().stream())
          .forEach(series -> {
            c.setSeries(series);
            c.setPosition(0);
            Long count = c.getLong("count");
            if (count != null) {
              countInfo.setCount(count);
            }
            Object first = c.getValue("first", null);
            if (first != null) {
              countInfo.setStartTime(c.getTime());
            }
            Object last = c.getValue("last", null);
            if (last != null) {
              countInfo.setEndTime(c.getTime());
            }
          });
    });
    return countInfo;
  }

  public SimpleDateFormat getUtcSdf(String pattern) {
    SimpleDateFormat sdf = sdfUtcMap.get().get(pattern);
    if (sdf == null) {
      sdfUtcMap.get().put(pattern, sdf = new SimpleDateFormat(pattern));
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    return sdf;
  }

  public String fmtUtcS(Object time) {
    return getUtcSdf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(time);
  }

  public String fmtUtc(Object time) {
    return getUtcSdf("yyyy-MM-dd'T'HH:mm:ss'Z'").format(time);
  }

}
