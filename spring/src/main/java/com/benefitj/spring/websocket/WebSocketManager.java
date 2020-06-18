package com.benefitj.spring.websocket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 管理WebSocket连接
 *
 * @param <WS>
 */
public class WebSocketManager<Key, WS extends IWebSocket<? extends WsSession<?>>> implements Map<Key, WS> {

  /**
   * 默认的状态监听
   */
  private static final DefaultStateListener DEFAULT_STATE_LISTENER = new DefaultStateListener();

  /**
   * 缓存WebSocket
   */
  private final Map<Key, WS> socketMap = new ConcurrentHashMap<>();

  /**
   * 监听WebSocket的状态
   */
  private StateListener<Key, WS> stateListener;

  public WebSocketManager() {
    this(DEFAULT_STATE_LISTENER);
  }

  public WebSocketManager(StateListener<Key, WS> stateListener) {
    this.setStateListener(stateListener);
  }

  /**
   * @return 获取状态的监听
   */
  public StateListener<Key, WS> getStateListener() {
    return stateListener;
  }

  /**
   * 设置状态的监听
   *
   * @param stateListener 监听
   */
  public void setStateListener(StateListener<Key, WS> stateListener) {
    this.stateListener = (stateListener != null ? stateListener : DEFAULT_STATE_LISTENER);
  }

  /**
   * @return 获取WebSocket的集合
   */
  public Map<Key, WS> getSocketMap() {
    return socketMap;
  }

  /**
   * @return 返回WebSocket数量
   */
  @Override
  public int size() {
    return getSocketMap().size();
  }

  /**
   * @return WebSocket集合是否为空
   */
  @Override
  public boolean isEmpty() {
    return getSocketMap().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getSocketMap().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getSocketMap().containsValue(value);
  }

  @Override
  public WS get(Object key) {
    return getSocketMap().get(key);
  }

  /**
   * 添加 WebSocket
   *
   * @param key       键
   * @param newSocket 对被添加的WebSocket
   * @return 返回是否被添加
   */
  @Override
  public WS put(Key key, WS newSocket) {
    WS oldSocket = null;
    try {
      oldSocket = getSocketMap().put(key, newSocket);
    } finally {
      if (oldSocket == null) {
        getStateListener().onAdd(key, newSocket);
      } else {
        getStateListener().onReplace(key, oldSocket, newSocket);
      }
    }
    return oldSocket;
  }

  /**
   * 移除 WebSocket
   *
   * @param key 对被移除的WebSocket的Key
   * @return 是否被移除
   */
  @Override
  public WS remove(Object key) {
    WS remove = null;
    try {
      remove = getSocketMap().remove(key);
    } finally {
      if (remove != null) {
        getStateListener().onRemove((Key) key, remove);
      }
    }
    return remove;
  }

  @Override
  public void putAll(Map<? extends Key, ? extends WS> m) {
    m.forEach((BiConsumer<Key, WS>) this::put);
  }

  /**
   * 清空
   */
  @Override
  public void clear() {
    try {
      getSocketMap().clear();
    } finally {
      getStateListener().onClear();
    }
  }

  @Override
  public Set<Key> keySet() {
    return getSocketMap().keySet();
  }

  @Override
  public Collection<WS> values() {
    return getSocketMap().values();
  }

  @Override
  public Set<Entry<Key, WS>> entrySet() {
    return getSocketMap().entrySet();
  }

  /**
   * @return 拷贝
   */
  @SuppressWarnings("unchecked")
  public Map<Key, WS> copy() {
    final Map<Key, WS> socketMap = getSocketMap();
    return socketMap.isEmpty() ? Collections.EMPTY_MAP : new HashMap<>(socketMap);
  }


  /**
   * 迭代WebSocket，如果返回false，表示停止迭代
   */
  public void forEach(Predicate<WS> consumer) {
    for (Entry<Key, WS> entry : entrySet()) {
      if (!consumer.test(entry.getValue())) {
        break;
      }
    }
  }

  /**
   * WebSocket的状态
   *
   * @param <Key> 键
   * @param <WS>  WebSocket
   */
  public interface StateListener<Key, WS extends IWebSocket> {
    /**
     * 添加WebSocket
     *
     * @param key       键
     * @param webSocket 被添加的WebSocket
     */
    void onAdd(Key key, WS webSocket);

    /**
     * 替换WebSocket
     *
     * @param key       键
     * @param oldSocket 被替换的WebSocket
     * @param newSocket 新的WebSocket
     */
    void onReplace(Key key, WS oldSocket, WS newSocket);

    /**
     * 移除WebSocket
     *
     * @param key       键
     * @param webSocket 被移除的WebSocket
     */
    void onRemove(Key key, WS webSocket);

    /**
     * 被清空
     */
    void onClear();
  }

  /**
   * 默认的实现
   */
  public static class DefaultStateListener<Key, WS extends IWebSocket>
      implements StateListener<Key, WS> {
    @Override
    public void onAdd(Key key, WS webSocket) {
      // ~
    }

    @Override
    public void onReplace(Key key, WS oldSocket, WS newSocket) {
      // ~
    }

    @Override
    public void onRemove(Key key, WS webSocket) {
      // ~
    }

    @Override
    public void onClear() {
      // ~
    }
  }
}
