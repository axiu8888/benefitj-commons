package com.benefitj.mqtt;

import com.benefitj.core.Slicer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * MQTT topic
 */
public class MqttTopic {

  private static final Map<String, MqttTopic> TOPICS = new WeakHashMap<>();
  private static final Function<String, MqttTopic> TOPIC_CREATOR = MqttTopic::new;

  public static MqttTopic get(String topicName) {
    return TOPICS.computeIfAbsent(topicName, TOPIC_CREATOR);
  }

  private final String topicName;

  private List<String> segments = new ArrayList<>();

  private Node node;

  public MqttTopic(String topicName) {
    this.topicName = topicName;
    this.node = parseToNode(topicName);
  }

  protected Node parseToNode(String name) {
    if (StringUtils.isNotBlank(name)) {
      List<String> slice = Slicer.slice(name, ((b, position, ch) -> ch == '/'));
      this.segments.addAll(slice);
      return recursiveNode(slice.toArray(new String[0]), 0, null);
    }
    return EMPTY_NODE;
  }

  protected Node recursiveNode(String[] parts, int index, Node prev) {
    if (parts.length <= index) {
      return null;
    }
    String part = parts[index].trim();
    if (part.contains("#") || part.contains("+")) {
      String part2 = part.trim();
      if (!(part2.equals("#") || part2.equals("+"))) {
        throw new IllegalStateException("错误的主题片段: " + part);
      }
    }
    Node current = new Node(part, prev, null, index);
    current.setPrev(prev);
    Node next = recursiveNode(parts, ++index, current);
    current.setNext(next);
    return current;
  }

  public List<String> getSegments() {
    return segments;
  }

  public Node getNode() {
    return node;
  }

  public boolean match(String topic) {
    return match(MqttTopic.get(topic));
  }

  public boolean match(MqttTopic topic) {
    return getNode().match(topic.getNode());
  }

  public String getTopicName() {
    return topicName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MqttTopic topic = (MqttTopic) o;
    return Objects.equals(topicName, topic.topicName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName);
  }

  public static final String MULTI = "#";
  public static final String SINGLE = "+";

  public static final Node EMPTY_NODE = new Node("", null, null, 0);
  public static final Node MULTI_NODE = new Node("#", null, null, 0);
  public static final Node SINGLE_NODE = new Node("+", null, null, 0);

  private static final Predicate<Node> PURE_FILTER = n -> !(n.part.equals(MULTI) || n.part.equals(SINGLE));

  public static class Node {

    private String part;

    private Node prev;
    private Node next;

    private boolean multi;
    private boolean single;

    private int level;

    public Node(String part, Node prev, Node next, int level) {
      this.part = part.trim();
      this.prev = prev;
      this.next = next;
      this.level = level;

      this.multi = this.part.equals(MULTI);
      this.single = this.part.equals(SINGLE);
    }

    public String getPart() {
      return part;
    }

    public void setPart(String part) {
      this.part = part;
    }

    public Node getPrev() {
      return prev;
    }

    public void setPrev(Node prev) {
      this.prev = prev;
    }

    public Node getNext() {
      return next;
    }

    public void setNext(Node next) {
      this.next = next;
    }

    public int getLevel() {
      return level;
    }

    public void setLevel(int level) {
      this.level = level;
    }

    public boolean isMulti() {
      return multi;
    }

    public void setMulti(boolean multi) {
      this.multi = multi;
    }

    public boolean isSingle() {
      return single;
    }

    public void setSingle(boolean single) {
      this.single = single;
    }

    public boolean hasNext() {
      return getNext() != null;
    }

    /**
     * 匹配规则
     *
     * @param node 节点
     * @return 返回是否匹配
     */
    public boolean match(Node node) {
      if (node == null) {
        return false;
      }
      if (EMPTY_NODE == node) {
        return false;
      }
      if (isMulti()) {
        return matchMulti(node);
      }
      if (isSingle()) {
        // 有单层的匹配
        return matchSingle(node);
      }
      return matchSpecial(node);
    }

    /**
     * 匹配多层级的规则
     *
     * @param node 节点
     * @return 返回是否匹配
     */
    public boolean matchMulti(Node node) {
      // 匹配多层级时，如果有下一级，继续检查下一级
      if (node.hasNext()) {
        Node next = next(this, PURE_FILTER);
        if (next != null) {
          // 获取第一个匹配的节点
          while (!(next.equalsPart(node))) {
            node = node.getNext();
            if (node == null) {
              // 找不到，不匹配
              return false;
            }
          }
          return next.match(node);
        }
      }
      return true;
    }

    /**
     * 匹配单层级规则
     *
     * @param node 节点
     * @return 返回是否匹配
     */
    public boolean matchSingle(Node node) {
      return hasNext() ? getNext().match(node.getNext()) : !node.hasNext();
    }

    /**
     * 匹配具体规则
     *
     * @param node 节点
     * @return 返回是否匹配
     */
    public boolean matchSpecial(Node node) {
      if (equalsPart(node)) {
        if (hasNext() || node.hasNext()) {
          return getNext() != null && getNext().match(node.getNext());
        }
        return true;
      }
      return false;
    }

    public boolean equalsPart(Node node) {
      if (node == null) {
        return false;
      }
      return getPart().equals(node.getPart());
    }


    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node) o;
      return Objects.equals(part, node.part) && (level == node.level);
    }

    @Override
    public int hashCode() {
      return Objects.hash(part, level);
    }
  }

  public static Node next(Node node, Predicate<Node> filter) {
    Node next = node.getNext();
    while (next != null) {
      if (filter.test(next)) {
        break;
      }
      next = next.getNext();
    }
    return next;
  }

}
