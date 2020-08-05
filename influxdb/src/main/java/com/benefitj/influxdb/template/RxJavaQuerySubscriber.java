package com.benefitj.influxdb.template;

import com.benefitj.influxdb.template.value.QueryConsumer;
import com.benefitj.influxdb.template.value.DefaultValueConverter;
import io.reactivex.subscribers.DefaultSubscriber;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 查询的消费者
 */
public abstract class RxJavaQuerySubscriber extends DefaultSubscriber<QueryResult> implements QueryConsumer<DefaultValueConverter> {

  private static final Logger log = LoggerFactory.getLogger(RxJavaQuerySubscriber.class);

  private DefaultValueConverter converter;

  public RxJavaQuerySubscriber() {
  }

  public long getRequest() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected final void onStart() {
    this.onQueryStart();
    this.request(getRequest());
  }

  @Override
  public final void onNext(QueryResult result) {
    if (result.hasError()) {
      log.warn("result error: {}", result.getError());
    } else {
      this.onResultNext(result);
    }
    this.request(getRequest());
  }

  @Override
  public final void onError(Throwable e) {
    this.onQueryError(e);
  }

  @Override
  public final void onComplete() {
    this.onQueryComplete();
  }

  /*****************************************/
  // QueryConsumer

  /**
   * 查询开始
   */
  @Override
  public void onQueryStart() {
    // ~
  }

  @Override
  public final void onResultNext(QueryResult result) {
    result.getResults()
        .stream()
        .filter(r -> {
          if (r != null && r.hasError()) {
            onError(new InfluxDBException(r.getError()));
            return false;
          }
          return RESULT_FILTER.test(r);
        })
        .flatMap(SERIES_STREAM)
        .forEach(this::iterator);
  }

  /**
   * 查询完成
   */
  @Override
  public void onQueryComplete() {
    // ~
  }

  /**
   * 查询被取消时
   */
  @Override
  public void onQueryCancel() {
    // ~
  }

  /**
   * 查询出现异常
   *
   * @param e
   */
  @Override
  public void onQueryError(Throwable e) {
    log.warn("onQueryError: " + e.getMessage());
  }

  /**
   * 当新的Series开始时
   *
   * @param series 序列
   * @param c      转换器对象
   */
  @Override
  public void onSeriesStart(QueryResult.Series series, DefaultValueConverter c) {
    // ~
  }

  /**
   * 迭代Series的下一个值
   *
   * @param values    值
   * @param converter 转换器对象
   * @param position  当前值的位置
   */
  @Override
  public abstract void onSeriesNext(List<Object> values, DefaultValueConverter converter, int position);

  /**
   * 迭代Series的完成
   *
   * @param series 序列
   * @param c      转换器对象
   */
  @Override
  public void onSeriesComplete(QueryResult.Series series, DefaultValueConverter c) {
    // ~
  }

  /**
   * 获取 SeriesConverter
   */
  @Override
  public final DefaultValueConverter getConverter() {
    DefaultValueConverter c = this.converter;
    if (c == null) {
      synchronized (this) {
        if ((c = this.converter) != null) {
          return c;
        }
        c = (this.converter = new DefaultValueConverter());
      }
    }
    return c;
  }

  @Override
  public final void iterator(QueryResult.Series series) {
    final DefaultValueConverter c = this.getConverter();
    final int index = c.getPosition();
    c.setSeries(series);
    c.setPosition(0);
    this.onSeriesStart(series, c);
    int size = c.getValues().size();
    for (int i = 0; i < size; i++) {
      c.setPosition(i);
      this.onSeriesNext(c.getValueList(i), c, i);
    }
    this.onSeriesComplete(series, c);
    c.setPosition(index);
  }
}
