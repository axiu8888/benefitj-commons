package com.benefitj.mqtt.paho.v5;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.mqtt.paho.MqttPahoClientException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.lang.reflect.Method;

/**
 * MQTT客户端
 */
public interface PahoMqttV5Client extends IMqttClient {

  /**
   * 创建客户端代理
   *
   * @param opts     配置
   * @param clientId 客户端ID
   * @return 返回客户端代理
   */
  static PahoMqttV5Client newProxy(MqttConnectionOptions opts, String clientId) {
    MemoryPersistence persistence = new MemoryPersistence();
    return newProxy(provide(opts, clientId, persistence));
  }

  /**
   * 创建客户端代理
   *
   * @param client 客户端
   * @return 返回客户端代理
   */
  static PahoMqttV5Client newProxy(MqttClient client) {
    Class<? extends MqttClient> type = client.getClass();
    return ProxyUtils.newProxy(PahoMqttV5Client.class, (proxy, m, args) -> {
      try {
        Method method = ReflectUtils.findFirstMethod(type, m.getName(), m.getParameterTypes());
        return ReflectUtils.invoke(client, method, args);
      } catch (Throwable e) {
        throw CatchUtils.throwing(e.getCause() != null ? e.getCause() : e, MqttPahoClientException.class);
      }
    });
  }

  /**
   * 创建客户端
   *
   * @param opts        参数
   * @param clientId    客户端ID
   * @param persistence 持久化
   * @return 返回客户端
   * @throws MqttPahoClientException
   */
  static MqttClient provide(MqttConnectionOptions opts, String clientId, MemoryPersistence persistence) throws MqttPahoClientException {
    try {
      clientId = StringUtils.getIfBlank(clientId, () -> "mqttv5-" + IdUtils.uuid(0, 16));
      return new MqttClient(opts.getServerURIs()[0], clientId, persistence);
    } catch (MqttException e) {
      throw new MqttPahoClientException(e);
    }
  }


  @Override
  void connect();

  @Override
  void connect(MqttConnectionOptions options);

  @Override
  IMqttToken connectWithResult(MqttConnectionOptions options);

  @Override
  void disconnect();

  @Override
  void disconnect(long quiesceTimeout);

  @Override
  void disconnectForcibly();

  @Override
  void disconnectForcibly(long disconnectTimeout);

  @Override
  void disconnectForcibly(long quiesceTimeout, long disconnectTimeout);

  @Override
  IMqttToken subscribe(String topicFilter, int qos);

  @Override
  IMqttToken subscribe(String[] topicFilters, int[] qos);

  @Override
  IMqttToken subscribe(String topicFilter, int qos, IMqttMessageListener messageListener);

  @Override
  IMqttToken subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners);

  @Override
  void unsubscribe(String topicFilter);

  @Override
  void unsubscribe(String[] topicFilters);

  @Override
  void publish(String topic, byte[] payload, int qos, boolean retained);

  @Override
  void publish(String topic, MqttMessage message);

  @Override
  void setCallback(MqttCallback callback);

  @Override
  MqttTopic getTopic(String topic);

  @Override
  boolean isConnected();

  @Override
  String getClientId();

  @Override
  String getServerURI();

  @Override
  IMqttToken[] getPendingTokens();

  @Override
  void setManualAcks(boolean manualAcks);

  @Override
  void reconnect();

  @Override
  void messageArrivedComplete(int messageId, int qos);

  @Override
  void close();
}
