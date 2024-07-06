package com.benefitj.mqtt.paho.v3;

import com.benefitj.core.*;
import com.benefitj.core.log.ILogger;
import com.benefitj.mqtt.MqttLogger;
import com.benefitj.mqtt.paho.MqttPahoClientException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


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

  static final BiConsumer<Boolean, Throwable> IGNORE_CONNECT_RESULT = (succeed, cause) -> {
    if (cause != null) {
      cause.printStackTrace();
    }
  };

  static final PahoMqttV3Callback NONE = (topic, message) -> {};


  static final ILogger log = MqttLogger.get();

  /**
   * 客户端
   */
  private final IPahoMqttV3Client raw;
  /**
   * 参数
   */
  private MqttConnectOptions options;
  /**
   * 回调
   */
  private PahoMqttV3Callback callback;
  private final Supplier<PahoMqttV3Callback> callbackSupplier = () -> callback != null ? callback : NONE;

  /**
   * 自动连接器
   */
  private final AutoConnectTimer autoConnectTimer = new AutoConnectTimer();
  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {
    @Override
    public boolean isConnected() {
      return raw.isConnected();
    }

    @Override
    public void doConnect() {
      tryConnect(IGNORE_CONNECT_RESULT);
    }
  };
  /**
   * 状态监听
   */
  private final PahoMqttV3Callback listener = new PahoMqttV3Callback() {

    @Override
    public void onConnected(PahoMqttV3Client client) {
      log.debug("onConnected, clientId: {}, serverURI: {}", client.getClientId(), client.getServerURI());
      autoConnectTimer.stop();
      callbackSupplier.get().onConnected(client);
    }

    @Override
    public void onDisconnected(PahoMqttV3Client client, @Nullable Throwable cause) {
      log.debug("onDisconnected, clientId: {}, serverURI: {}", client.getClientId(), client.getServerURI());
      try {
        callbackSupplier.get().onDisconnected(client, cause);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        autoConnectTimer.start(connector); // 自动连接
      }
    }

    @Override
    public void connectionLost(Throwable cause) {
      log.debug("connectionLost, cause: {}", cause.getMessage());
      try {
        callbackSupplier.get().connectionLost(cause);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        autoConnectTimer.start(connector); // 自动连接
      }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      callbackSupplier.get().messageArrived(topic, message);
    }
  };

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
    this.options = options;
    source.setCallback(listener);
    Class<? extends IMqttClient> type = source.getClass();
    this.raw = ProxyUtils.newProxy(IPahoMqttV3Client.class, (proxy, m, args) -> {
      try {
        Method method = ReflectUtils.getMethod(type, m.getName(), m.getParameterTypes());
        return ReflectUtils.invoke(source, method, args);
      } catch (Throwable e) {
        throw new MqttPahoClientException(CatchUtils.findRoot(e));
      }
    });
  }

  protected IPahoMqttV3Client getClient() {
    return this.raw;
  }

  boolean connect0(IPahoMqttV3Client raw,
                   MqttConnectOptions options,
                   BiConsumer<Boolean, Throwable> status) {
    try {
      if (raw.isConnected()) return true;
      if (options != null && !options.isAutomaticReconnect()) {
        options.setAutomaticReconnect(false);
        options.setConnectionTimeout((int) Math.min(options.getConnectionTimeout(), getAutoConnectTimer().getInterval().toSeconds()));
      }
      log.debug("connect0, clientId: {}, serverURI: {}", raw.getClientId(), raw.getServerURI());
      raw.connect(options);
      status.accept(raw.isConnected(), null);
    } catch (Throwable e) {
      status.accept(false, e);
    }
    return raw.isConnected();
  }

  /**
   * 尝试连接
   */
  protected boolean tryConnect(BiConsumer<Boolean, Throwable> result) {
    if (raw.isConnected()) {
      result.accept(true, null);
      return true;
    } else {
      return connect0(raw, options, (status, cause) -> {
        if (status) listener.onConnected(this);  // 连接成功
        else listener.connectionLost(cause); // 连接失败
        result.accept(status, cause);
      });
    }
  }

  /**
   * 尝试连接
   */
  protected void disconnect0() {
    if (raw.isConnected()) {
      try {
        log.debug("disconnect0, clientId: {}, serverURI: {}", raw.getClientId(), raw.getServerURI());
        raw.disconnectForcibly();
      } catch (MqttPahoClientException ignore) {/*^_^*/}
      finally {
        listener.onDisconnected(this, null);
        getAutoConnectTimer().stop(); // 停止自定重连
      }
    }
  }

  /**
   * 自动连接器
   */
  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public PahoMqttV3Client setAutoConnectTimer(Consumer<AutoConnectTimer> consumer) {
    consumer.accept(getAutoConnectTimer());
    return this;
  }

  @Override
  public void connect() {
    tryConnect(IGNORE_CONNECT_RESULT);
  }

  @Override
  public void connect(MqttConnectOptions options) {
    this.options = options;
    tryConnect(IGNORE_CONNECT_RESULT);
  }

  @Override
  public IMqttToken connectWithResult(MqttConnectOptions options) {
    throw new MqttPahoClientException("不支持此方法!");
  }

  @Override
  public void disconnect() {
    disconnect0();
  }

  @Override
  public void disconnect(long quiesceTimeout) {
    disconnect0();
  }

  @Override
  public void disconnectForcibly() {
    disconnect0();
  }

  @Override
  public void disconnectForcibly(long disconnectTimeout) {
    disconnect0();
  }

  @Override
  public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) {
    disconnect0();
  }

  @Override
  public void subscribe(String topicFilter) {
    getClient().subscribe(topicFilter);
  }

  @Override
  public void subscribe(String[] topicFilters) {
    getClient().subscribe(topicFilters);
  }

  @Override
  public void subscribe(String topicFilter, int qos) {
    getClient().subscribe(topicFilter, qos);
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos) {
    getClient().subscribe(topicFilters, qos);
  }

  @Override
  public void subscribe(String topicFilter, IMqttMessageListener messageListener) {
    getClient().subscribe(topicFilter, messageListener);
  }

  @Override
  public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    getClient().subscribe(topicFilters, messageListeners);
  }

  @Override
  public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) {
    getClient().subscribe(topicFilter, qos, messageListener);
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    getClient().subscribe(topicFilters, qos, messageListeners);
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter) {
    return getClient().subscribeWithResponse(topicFilter);
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) {
    return getClient().subscribeWithResponse(topicFilter, messageListener);
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos) {
    return getClient().subscribeWithResponse(topicFilter, qos);
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) {
    return getClient().subscribeWithResponse(topicFilter, qos, messageListener);
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters) {
    return getClient().subscribeWithResponse(topicFilters);
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    return getClient().subscribeWithResponse(topicFilters, messageListeners);
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos) {
    return getClient().subscribeWithResponse(topicFilters, qos);
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    return getClient().subscribeWithResponse(topicFilters, qos, messageListeners);
  }

  @Override
  public void unsubscribe(String topicFilter) {
    getClient().unsubscribe(topicFilter);
  }

  @Override
  public void unsubscribe(String[] topicFilters) {
    getClient().unsubscribe(topicFilters);
  }

  @Override
  public void publish(String topic, byte[] payload, int qos, boolean retained) {
    getClient().publish(topic, payload, qos, retained);
  }

  @Override
  public void publish(String topic, MqttMessage message) {
    getClient().publish(topic, message);
  }

  @Override
  public void setCallback(MqttCallback cb) {
    if (cb instanceof PahoMqttV3Callback) {
      this.callback = (PahoMqttV3Callback) cb;
    } else {
      if (cb != null) {
        this.callback = new PahoMqttV3Callback() {
          @Override
          public void messageArrived(String topic, MqttMessage message) throws Exception {
            cb.messageArrived(topic, message);
          }

          @Override
          public void deliveryComplete(IMqttDeliveryToken token) {
            cb.deliveryComplete(token);
          }

          @Override
          public void connectionLost(Throwable cause) {
            cb.connectionLost(cause);
          }
        };
      } else {
        this.callback = null;
      }
    }
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
    tryConnect(IGNORE_CONNECT_RESULT);
  }

  @Override
  public void messageArrivedComplete(int messageId, int qos) {
    getClient().messageArrivedComplete(messageId, qos);
  }

  @Override
  public void close() {
    getClient().close();
  }

}
