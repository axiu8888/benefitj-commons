package com.benefitj.influxdb.template;

import com.benefitj.influxdb.query.QueryConsumer;
import com.benefitj.influxdb.query.SimpleValueConverter;
import io.reactivex.subscribers.DefaultSubscriber;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 查询的消费者
 */
public abstract class RxJavaQuerySubscriber extends DefaultSubscriber<QueryResult>
    implements QueryConsumer<SimpleValueConverter> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RxJavaQuerySubscriber.class);

  private SimpleValueConverter converter;

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
      LOGGER.debug("result error: {}", result.getError());
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
    e.printStackTrace();
  }

  /**
   * 当新的Series开始时
   *
   * @param series 序列
   * @param c      转换器对象
   */
  @Override
  public void onSeriesStart(QueryResult.Series series, SimpleValueConverter c) {
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
  public abstract void onSeriesNext(List<Object> values, SimpleValueConverter converter, int position);

  /**
   * 迭代Series的完成
   *
   * @param series 序列
   * @param c      转换器对象
   */
  @Override
  public void onSeriesComplete(QueryResult.Series series, SimpleValueConverter c) {
    // ~
  }

  /**
   * 获取 SeriesConverter
   */
  @Override
  public final SimpleValueConverter getConverter() {
    SimpleValueConverter vc = this.converter;
    if (vc == null) {
      synchronized (this) {
        if ((vc = this.converter) == null) {
          vc = (this.converter = new SimpleValueConverter());
        }
      }
    }
    return vc;
  }

  @Override
  public final void iterator(QueryResult.Series series) {
    final SimpleValueConverter c = this.getConverter();
    int position = c.getPosition();
    c.setupSeries(series);
    c.setPosition(0);
    this.onSeriesStart(series, c);
    int size = c.getValues().size();
    for (int i = 0; i < size; i++) {
      c.setPosition(i);
      this.onSeriesNext(c.getValue(i), c, i);
    }
    this.onSeriesComplete(series, c);
    c.setPosition(position);
  }
}
