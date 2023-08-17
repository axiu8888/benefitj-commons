package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * Performance Domain
 */
@ChromiumApi("Performance")
public interface Performance {

  /**
   * Disable collecting and reporting metrics.
   */
  void disable();

  /**
   * Enable collecting and reporting metrics.
   *
   * @param timeDomain string
   *                   Time domain to use for collecting and reporting duration metrics.
   *                   Allowed Values: timeTicks, threadTicks
   */
  void enable(String timeDomain);

  /**
   * Retrieve current values of run-time metrics.
   *
   * @param metrics array[ Metric ]
   *                Current values for run-time metrics.
   */
  void getMetrics(List<Metric> metrics);

  /**
   * Sets time domain to use for collecting and reporting duration metrics. Note that this must be called before
   * enabling metrics collection. Calling this method while metrics collection is enabled returns an error.
   *
   * @param timeDomain string
   *                   Time domain
   *                   Allowed Values: timeTicks, threadTicks
   */
  void setTimeDomain(String timeDomain);

  @Event("Performance")
  public interface Events {
    /**
     * Current values of the metrics.
     *
     * @param metrics array[ Metric ]
     *                Current values of the metrics.
     * @param title   string
     *                Timestamp title.
     */
    @Event("metrics")
    void metrics(List<Metric> metrics, String title);

  }

  /**
   * Run-time execution metric.
   */
  @Data
  public class Metric {
    /**
     * Metric name.
     */
    String name;
    /**
     * Metric value.
     */
    Number value;
  }
}
