package com.benefitj.spring.websocket.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.websocket.server.ServerEndpoint;
import java.util.List;

/**
 * 注册被 {@link SpringServerEndpoint} 注解的WebSocket组件
 */
@Lazy
@ConditionalOnClass(WebSocketConfigurer.class)
@ConditionalOnMissingBean(SpringServerEndpointConfiguration.class)
@EnableWebSocket
@Configuration
public class SpringServerEndpointConfiguration implements WebSocketConfigurer {

  @Autowired(required = false)
  private List<SpringWebSocketClient> webSocketClients;
  @Autowired
  private ApplicationContext context;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    List<SpringWebSocketClient> clients = this.webSocketClients;
    if (!CollectionUtils.isEmpty(clients)) {
      for (SpringWebSocketClient client : clients) {
        Class<?> clazz = client.getClass();
        if (clazz.isAnnotationPresent(ServerEndpoint.class)
            && clazz.isAnnotationPresent(SpringServerEndpoint.class)) {
          throw new IllegalStateException("[" + clazz.getName() + "]无法注册多次，请在\"@ServerEndpoint\"和\"@SpringServerEndpoint\"中删除一个注解!");
        }
      }

      for (SpringWebSocketClient client : clients) {
        Class<? extends SpringWebSocketClient> clientClass = client.getClass();
        SpringServerEndpoint endpoint = clientClass.getAnnotation(SpringServerEndpoint.class);
        if (endpoint != null) {
          try {
            SpringWebSocketServer server;
            try {
              server = context.getBean(endpoint.serverType());
            } catch (BeansException ignore) {
              server = endpoint.serverType().newInstance();
            }
            server.setClientType(clientClass);
            registry.addHandler(server, endpoint.value())
                .setAllowedOrigins(endpoint.allowedOrigins())
                .addInterceptors(server);
          } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("无法实例化，请保证至少有一个public修饰的无参构造函数!");
          }
        }
      }
    }
  }

}
