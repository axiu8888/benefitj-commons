package com.benefitj.mqtt.paho.v3;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.core.TimeUtils;
import com.benefitj.core.functions.IConsumer;
import com.benefitj.core.functions.IFunction;
import com.benefitj.mqtt.paho.MqttPahoClientException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.annotation.Nullable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 简单的MQTT客户端
 */
public class PahoMqttV3Client implements IPahoMqttV3Client {

  /**
   * 创建MQTT客户端
   *
   * @param options  配置可选项
   * @param clientId 客户端ID
   * @return 返回MQTT客户端对象
   */
  public static IMqttClient provide(MqttConnectOptions options, String clientId) {
    return provide(options, clientId, new MemoryPersistence());
  }

  /**
   * 创建MQTT客户端
   *
   * @param options  配置可选项
   * @param clientId 客户端ID
   * @return 返回MQTT客户端对象
   */
  public static IMqttClient provide(MqttConnectOptions options, String clientId, MqttClientPersistence persistence) {
    try {
      clientId = StringUtils.getIfBlank(clientId, () -> "mqttv3-" + IdUtils.uuid().substring(0, 16));
      return new MqttClient(options.getServerURIs()[0], clientId, persistence);
    } catch (MqttException e) {
      throw new MqttPahoClientException(e);
    }
  }

  private final MqttCallbackListener listener = new MqttCallbackListener();
  /**
   * 客户端
   */
  private IMqttClient raw;
  /**
   * 参数
   */
  private MqttConnectOptions options;
  /**
   * 连接数次统计
   */
  private final AtomicInteger connectCounter = new AtomicInteger(0);
  /**
   * 是否自动重连
   */
  private boolean autoReconnect = false;
  /**
   * 是否已经断开
   */
  private boolean disconnected = false;
  /**
   * 自动连接的间隔，默认1000毫秒
   */
  private long delay = 1000;
  /**
   * 最新一次尝试连接的时间
   */
  private long lastReconnectTime = 0;
  /**
   * 回调
   */
  private PahoMqttV3Callback callback = new MqttDelegateCallback(null);
  /**
   * 调度器
   */
  private ScheduledExecutorService executor;

  public PahoMqttV3Client(String clientId) {
    this(new MqttConnectOptions(), clientId);
  }

  public PahoMqttV3Client(MqttConnectOptions options, String clientId) {
    this(provide(options, clientId), options);
  }

  public PahoMqttV3Client(IMqttClient source) {
    this(source, null);
  }

  public PahoMqttV3Client(IMqttClient source, MqttConnectOptions options) {
    this.raw = source;
    this.options = options;
    source.setCallback(listener);
  }

  /**
   * 尝试连接
   *
   * @param raw     客户端
   * @param options 连接参数
   */
  protected boolean tryConnect(IMqttClient raw, MqttConnectOptions options) {
    try {
      if (!raw.isConnected()) {
        this.setDisconnected(false);
        synchronized (this) {
          if (!raw.isConnected()) {
            if (connectCounter.get() > 0) {
              try {
                raw.reconnect();
              } catch (NoSuchMethodError ignore) {
                raw.connect();
              }
            } else {
              if (options != null) {
                raw.connect(options);
              } else {
                raw.connect();
              }
            }
            if (raw.isConnected()) {
              // 连接成功
              listener.onConnected(connectCounter.getAndIncrement() > 0);
            }
          }
        }
      }
      return true;
    } catch (MqttException ignore) { /* ~ */ }
    return false;
  }

  /**
   * 尝试连接
   *
   * @param raw 客户端
   */
  protected void tryDisconnect(IMqttClient raw) {
    setDisconnected(true);
    if (raw.isConnected()) {
      try {
        raw.disconnectForcibly();
      } catch (MqttException ignore) { /* ~ */ }
      finally {
        callback.onDisconnected(this, null);
      }
    }
    listener.cancel();
  }

  public boolean isDisconnected() {
    return disconnected;
  }

  public void setDisconnected(boolean disconnected) {
    this.disconnected = disconnected;
  }

  public boolean isAutoReconnect() {
    return autoReconnect;
  }

  public void setAutoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  protected IMqttClient getClient() {
    return getClient(false);
  }

  protected IMqttClient getClient(boolean autoConnect) {
    IMqttClient c = this.raw;
    if (autoConnect && !isDisconnected() && !c.isConnected()) {
      if (TimeUtils.diffNow(getLastReconnectTime()) > this.delay) {
        try {
          tryConnect(c, options);
          c = this.raw;
        } finally {
          this.setLastReconnectTime(System.currentTimeMillis());
        }
      }
    }
    return c;
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public long getLastReconnectTime() {
    return lastReconnectTime;
  }

  public void setLastReconnectTime(long lastReconnectTime) {
    this.lastReconnectTime = lastReconnectTime;
  }

  public ScheduledExecutorService getExecutor() {
    return executor;
  }

  public void setExecutor(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  protected <T> T tryCatch(IFunction<IMqttClient, T> func) {
    return tryCatch(func, true);
  }

  protected <T> T tryCatch(IFunction<IMqttClient, T> func, boolean autoReconnect) {
    try {
      return func.apply(getClient(autoReconnect));
    } catch (Exception e) {
      throw new MqttPahoClientException(e);
    }
  }

  protected void tryCatch(IConsumer<IMqttClient> c) {
    tryCatch(c, true);
  }

  protected void tryCatch(IConsumer<IMqttClient> c, boolean autoReconnect) {
    try {
      c.accept(getClient(autoReconnect));
    } catch (Exception e) {
      throw new MqttPahoClientException(e);
    }
  }

  @Override
  public void connect() {
    tryConnect(getClient(false), options);
  }

  @Override
  public void connect(MqttConnectOptions options) {
    this.options = options;
    tryConnect(getClient(false), options);
  }

  @Override
  public IMqttToken connectWithResult(MqttConnectOptions options) {
    throw new MqttPahoClientException("不支持此方法!");
  }

  @Override
  public void disconnect() {
    tryDisconnect(getClient(false));
  }

  @Override
  public void disconnect(long quiesceTimeout) {
    tryDisconnect(getClient(false));
  }

  @Override
  public void disconnectForcibly() {
    tryDisconnect(getClient(false));
  }

  @Override
  public void disconnectForcibly(long disconnectTimeout) {
    tryDisconnect(getClient(false));
  }

  @Override
  public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) {
    tryDisconnect(getClient(false));
  }

  @Override
  public void subscribe(String topicFilter) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilter));
  }

  @Override
  public void subscribe(String[] topicFilters) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilters));
  }

  @Override
  public void subscribe(String topicFilter, int qos) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilter, qos));
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilters, qos));
  }

  @Override
  public void subscribe(String topicFilter, IMqttMessageListener messageListener) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilter, messageListener));
  }

  @Override
  public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilters, messageListeners));
  }

  @Override
  public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilter, qos, messageListener));
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    tryCatch((IConsumer<IMqttClient>) c -> c.subscribe(topicFilters, qos, messageListeners));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, messageListener));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, qos));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, qos, messageListener));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, messageListeners));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, qos));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    return tryCatch((IFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, qos, messageListeners));
  }

  @Override
  public void unsubscribe(String topicFilter) {
    tryCatch((IConsumer<IMqttClient>) c -> c.unsubscribe(topicFilter));
  }

  @Override
  public void unsubscribe(String[] topicFilters) {
    tryCatch((IConsumer<IMqttClient>) c -> c.unsubscribe(topicFilters));
  }

  @Override
  public void publish(String topic, byte[] payload, int qos, boolean retained) {
    tryCatch((IConsumer<IMqttClient>) c -> c.publish(topic, payload, qos, retained));
  }

  @Override
  public void publish(String topic, MqttMessage message) {
    tryCatch((IConsumer<IMqttClient>) c -> c.publish(topic, message));
  }

  @Override
  public void setCallback(MqttCallback callback) {
    if (callback instanceof PahoMqttV3Callback) {
      this.callback = (PahoMqttV3Callback) callback;
    } else {
      this.callback = new MqttDelegateCallback(callback);
    }
  }

  public MqttCallback getCallback() {
    return callback;
  }

  public PahoMqttV3Dispatcher getDispatcher() {
    MqttCallback cb = getCallback();
    return cb instanceof PahoMqttV3Dispatcher ? (PahoMqttV3Dispatcher) cb : null;
  }

  @Override
  public MqttTopic getTopic(String topic) {
    return getClient().getTopic(topic);
  }

  @Override
  public boolean isConnected() {
    return getClient().isConnected();
  }

  @Override
  public String getClientId() {
    return getClient().getClientId();
  }

  @Override
  public String getServerURI() {
    return getClient().getServerURI();
  }

  @Override
  public IMqttDeliveryToken[] getPendingDeliveryTokens() {
    return getClient().getPendingDeliveryTokens();
  }

  @Override
  public void setManualAcks(boolean manualAcks) {
    getClient().setManualAcks(manualAcks);
  }

  @Override
  public void reconnect() {
    tryConnect(this.raw, options);
  }

  @Override
  public void messageArrivedComplete(int messageId, int qos) {
    tryCatch((IConsumer<IMqttClient>) c -> c.messageArrivedComplete(messageId, qos));
  }

  @Override
  public void close() {
    tryCatch(IMqttClient::close);
  }


  public class MqttCallbackListener implements MqttCallback, Runnable {
    /**
     * 重新连接的任务
     */
    private final AtomicReference<ScheduledFuture<?>> autoConnectRef = new AtomicReference<>();

    public void onConnected(boolean reconnect) {
      callback.onConnected(PahoMqttV3Client.this, reconnect);
    }

    @Override
    public void connectionLost(Throwable cause) {
      try {
        if (isAutoReconnect() && !isDisconnected()) {
          long delay = Math.max(Math.min(300_000, getDelay()), 1000);
          EventLoop.cancel(autoConnectRef.getAndSet(getExecutor().scheduleAtFixedRate(
              this, Math.max(getDelay(), 100), delay, TimeUnit.MILLISECONDS)));
        }
      } finally {
        callback.onDisconnected(PahoMqttV3Client.this, cause);
      }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      callback.messageArrived(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
      callback.deliveryComplete(token);
    }

    @Override
    public void run() {
      try {
        if (isAutoReconnect()) {
          if (getClient(true).isConnected()) {
            cancel();
            callback.onConnected(PahoMqttV3Client.this, true);
          }
        } else {
          cancel();
        }
      } catch (Exception ignore) { /* ~ */ }
    }

    private void cancel() {
      EventLoop.cancel(autoConnectRef.getAndSet(null));
    }
  }


  static class MqttDelegateCallback implements PahoMqttV3Callback {

    private MqttCallback callback;

    public MqttDelegateCallback(MqttCallback callback) {
      this.callback = callback;
    }

    @Override
    public void connectionLost(Throwable cause) {
      if (callback != null) {
        callback.connectionLost(cause);
      } else {
        if (cause != null) {
          cause.printStackTrace();
        }
      }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      if (callback != null) {
        callback.messageArrived(topic, message);
      }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
      if (callback != null) {
        callback.deliveryComplete(token);
      }
    }

    @Override
    public void onConnected(PahoMqttV3Client client, boolean reconnect) {
    }

    @Override
    public void onDisconnected(PahoMqttV3Client client, @Nullable Throwable cause) {
      connectionLost(cause);
    }
  }

}
