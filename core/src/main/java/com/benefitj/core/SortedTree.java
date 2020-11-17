package com.benefitj.core;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对树结构进行归类排序，最后组成一棵或多棵树
 */
public class SortedTree {

  /**
   * 对树形结构排序，使其组成一棵树
   *
   * @param map  需要排序的树结点
   * @param <ID> ID类型
   * @param <T>  树结点类型
   * @return 返回排序好的Map
   */
  public static <ID, T extends Tree<ID, T>> Map<ID, T> sort(Map<ID, T> map) {
    // 缓存父节点
    final Map<ID, TreeWrapper<ID, T>> cachedMap = new ConcurrentHashMap<>(map.size());
    for (Map.Entry<ID, T> entry : map.entrySet()) {
      cachedMap.put(entry.getKey(), new TreeWrapper<>(entry.getValue()));
    }

    int count = cachedMap.size();
    // 遍历元素，查找父节点
    for (Map.Entry<ID, TreeWrapper<ID, T>> entry : cachedMap.entrySet()) {
      TreeWrapper<ID, T> st = entry.getValue();
      if (st.hasParent()) {
        TreeWrapper<ID, T> pst = cachedMap.get(st.getParentId());
        if (pst != null && pst.addChild(st.getParentId(), st.getTree())) {
          st.setCorrelated(true);
          count--;
        }
      }
    }
    // 取出未找到父节点的元素
    final Map<ID, T> sortedMap = new ConcurrentHashMap<>(count);
    for (TreeWrapper<ID, T> st : cachedMap.values()) {
      if (!st.isCorrelated()) {
        sortedMap.put(st.getId(), st.getTree());
      }
    }
    return sortedMap;
  }

  /**
   * ID 是否存在
   *
   * @param id   ID
   * @param <ID> ID类型
   * @return 返回是否存在
   */
  private static <ID> boolean isExist(ID id) {
    boolean flag = id != null;
    if (flag && (id instanceof CharSequence)) {
      CharSequence idStr = (CharSequence) id;
      int length = idStr.length();
      for (int i = 0; i < length; i++) {
        if (idStr.charAt(i) != ' ') {
          return true;
        }
      }
      return false;
    }
    return flag;
  }

  /**
   * 树结点接口
   */
  public interface Tree<ID, N extends Tree<ID, N>> {

    /**
     * 获取结点的ID
     *
     * @return 返回结点ID
     */
    ID getId();

    /**
     * 设置结点ID
     *
     * @param id 结点ID
     */
    void setId(ID id);

    /**
     * 获取父结点的ID
     *
     * @return 返回父结点ID
     */
    ID getParentId();

    /**
     * 设置父结点ID
     *
     * @param parentId 父结点ID
     */
    void setParentId(ID parentId);

    /**
     * 获取全部的子结点
     *
     * @return 返回全部的子结点
     */
    Collection<N> getChildren();

    /**
     * 获取子结点的数量（包含子结点）
     *
     * @return 子结点的数量
     */
    default int getChildrenSize() {
      final Collection<N> nodes = getChildren();
      if (!nodes.isEmpty()) {
        int size = nodes.size();
        for (N node : nodes) {
          size += node.getChildrenSize();
        }
        return size;
      }
      return 0;
    }

    /**
     * 获取直接子结点的数量
     *
     * @return 返回子结点的数量
     */
    default int getNearChildrenSize() {
      return this.getChildren().size();
    }

    /**
     * 添加子结点
     *
     * @param node 子结点
     * @return 是否添加成功
     */
    default boolean addChild(N node) {
      if (node != null) {
        if (!isExist(node.getParentId())) {
          // 给子结点设置当前的ID为其父结点
          node.setParentId(getId());
        }
        this.getChildren().add(node);
        return true;
      }
      return false;
    }

    /**
     * 添加结点
     *
     * @param pid  父结点的ID
     * @param node 子结点
     * @return 是否添加成功
     */
    default boolean addChild(ID pid, N node) {
      if (!isExist(pid)) {
        return this.addChild(node);
      }
      final N child = getChild(pid);
      return child != null && child.addChild(node);
    }

    /**
     * 添加多个子结点
     *
     * @param nodes 子结点集合
     */
    default int addChildren(List<N> nodes) {
      if (!nodes.isEmpty()) {
        int count = 0;
        for (N node : nodes) {
          count += addChild(node) ? 1 : 0;
        }
        return count;
      }
      return 0;
    }

    /**
     * 添加结点
     *
     * @param children key为父结点的ID，value为子结点
     */
    default int addChildren(Map<ID, N> children) {
      if (!children.isEmpty()) {
        int count = 0;
        for (Map.Entry<ID, N> entry : children.entrySet()) {
          final N n = entry.getValue();
          count += addChild(entry.getKey(), n) ? n.getChildrenSize() + 1 : 0;
        }
        return count;
      }
      return 0;
    }

    /**
     * 根据ID获取子结点
     *
     * @param id 子结点的ID
     * @return 返回对相应的子结点
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    default N getChild(ID id) {
      if (id != null) {
        // 判断是否为当前结点的ID
        if (getId().equals(id)) {
          return (N) this;
        }
        final Collection<N> nodes = this.getChildren();
        if (!nodes.isEmpty()) {
          for (N node : nodes) {
            if (id.equals(node.getId())) {
              return node;
            } else {
              // 递归查询子结点的子结点
              N child = node.getChild(id);
              if (child != null) {
                return child;
              }
            }
          }
        }
      }
      return null;
    }

    /**
     * 移除子结点
     *
     * @param id 子结点ID
     * @return 返回移除的子结点或null
     */
    default N removeChild(ID id) {
      if (!getId().equals(id)) {
        final Collection<N> nodes = this.getChildren();
        for (N node : nodes) {
          if (id.equals(node.getId())) {
            nodes.remove(node);
            return node;
          } else {
            // 递归移除子结点的子结点
            N child = node.removeChild(id);
            if (child != null) {
              return child;
            }
          }
        }
      }
      return null;
    }

    /**
     * 移除子结点(递归删除对应的子结点)
     *
     * @param node 子结点
     */
    default boolean removeChild(N node) {
      final ID id = node.getId();
      return (id != null) && (removeChild(id) != null);
    }

    /**
     * 清空
     */
    default void clear() {
      getChildren().clear();
    }
  }


  static class TreeWrapper<ID, T extends Tree<ID, T>> implements Tree<ID, T> {

    private final T tree;
    /**
     * 是否已关联
     */
    private boolean correlated = false;

    public TreeWrapper(T tree) {
      this.tree = tree;
    }

    public boolean isCorrelated() {
      return correlated;
    }

    public void setCorrelated(boolean correlated) {
      this.correlated = correlated;
    }

    public boolean hasParent() {
      return isExist(tree.getParentId());
    }

    @Override
    public ID getId() {
      return tree.getId();
    }

    @Deprecated
    @Override
    public void setId(ID id) {
      throw new IllegalStateException();
    }

    @Override
    public ID getParentId() {
      return tree.getParentId();
    }

    @Deprecated
    @Override
    public void setParentId(ID parentId) {
      throw new IllegalStateException();
    }

    @Override
    public Collection<T> getChildren() {
      return tree.getChildren();
    }

    public T getTree() {
      return tree;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TreeWrapper<?, ?> that = (TreeWrapper<?, ?>) o;
      return Objects.equals(tree, that.tree);
    }

    @Override
    public int hashCode() {
      return Objects.hash(tree);
    }
  }

}
