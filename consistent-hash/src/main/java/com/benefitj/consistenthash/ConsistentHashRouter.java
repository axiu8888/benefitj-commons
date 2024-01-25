/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.benefitj.consistenthash;

import java.util.*;

/**
 * To hash Node objects to a hash ring with a certain amount of virtual node.
 * Method routeNode will return a Node instance which the object key should be allocated to according to consistent hash algorithm
 *
 * @param <T>
 */
public class ConsistentHashRouter<T extends Node> {

  private final SortedMap<Long, VirtualNode<T>> ring = new TreeMap<>();
  private final HashFunction hashFunction;

  private final ReadWriteLockDelegate lock = new ReadWriteLockDelegate(false);
  /**
   * 最大虚拟节点数
   */
  private volatile int maxVirtualNodeCount;

  public ConsistentHashRouter() {
    this(Integer.MAX_VALUE);
  }

  public ConsistentHashRouter(int maxVirtualNodeCount) {
    this(maxVirtualNodeCount, null);
  }

  public ConsistentHashRouter(int maxVirtualNodeCount, HashFunction hashFunction) {
    this.maxVirtualNodeCount = Math.min(maxVirtualNodeCount, Integer.MAX_VALUE);
    this.hashFunction = hashFunction != null ? hashFunction : new MessageDigestHash("MD5");
  }

  public int getMaxVirtualNodeCount() {
    return maxVirtualNodeCount;
  }

  public HashFunction getHashFunction() {
    return hashFunction;
  }

  public int size() {
    return lock.readLock(ring::size);
  }

  public boolean isEmpty() {
    return lock.readLock(ring::isEmpty);
  }

  /**
   * add physic node to the hash ring with some virtual nodes
   *
   * @param pNode      physical node needs added to hash ring
   * @param vNodeCount the number of virtual node of the physical node. Value should be greater than or equals to 0
   */
  public void addNode(final T pNode, final int vNodeCount) {
    addNode(pNode, vNodeCount, getMaxVirtualNodeCount());
  }

  /**
   * add physic node to the hash ring with some virtual nodes
   *
   * @param pNode               physical node needs added to hash ring
   * @param vNodeCount          the number of virtual node of the physical node. Value should be greater than or equals to 0
   * @param maxVirtualNodeCount 最大虚拟节点数量
   */
  public void addNode(final T pNode, final int vNodeCount, int maxVirtualNodeCount) {
    addNodes(Collections.singleton(pNode), vNodeCount, maxVirtualNodeCount);
  }

  /**
   * add physic node to the hash ring with some virtual nodes
   *
   * @param pNodes     physical node needs added to hash ring
   * @param vNodeCount the number of virtual node of the physical node. Value should be greater than or equals to 0
   */
  public void addNodes(Collection<T> pNodes, int vNodeCount) {
    addNodes(pNodes, vNodeCount, getMaxVirtualNodeCount());
  }

  /**
   * add physic node to the hash ring with some virtual nodes
   *
   * @param pNodes              physical node needs added to hash ring
   * @param vNodeCount          the number of virtual node of the physical node. Value should be greater than or equals to 0
   * @param maxVirtualNodeCount 最大虚拟节点数量
   */
  public void addNodes(final Collection<T> pNodes, final int vNodeCount, final int maxVirtualNodeCount) {
    if (vNodeCount < 0) {
      throw new IllegalArgumentException("illegal virtual node counts :" + vNodeCount);
    }
    if (pNodes != null && !pNodes.isEmpty()) {
      lock.writeLock(() -> {
        for (T pNode : pNodes) {
          int replicas = getUnsafeReplicas(pNode);
          if (maxVirtualNodeCount > replicas) {
            for (int i = 0; i < vNodeCount; i++) {
              VirtualNode<T> vNode = new VirtualNode<>(pNode, i + replicas);
              Long vHash = getHashFunction().hash(vNode.getKey());
              ring.put(vHash, vNode);
            }
          }
        }
      });
    }
  }

  /**
   * remove the physical node from the hash ring
   *
   * @param pNode
   */
  public void removeNode(T pNode) {
    removeNodes(Collections.singleton(pNode));
  }

  /**
   * remove the physical node from the hash ring
   *
   * @param pNodes
   */
  public void removeNodes(Collection<T> pNodes) {
    lock.writeLock(() -> {
      for (T pNode : pNodes) {
        final Iterator<Long> it = ring.keySet().iterator();
        while (it.hasNext()) {
          Long key = it.next();
          VirtualNode<T> virtualNode = ring.get(key);
          if (virtualNode.isVirtualNodeOf(pNode)) {
            it.remove();
          }
        }
      }
    });
  }

  /**
   * with a specified key, route the nearest Node instance in the current hash ring
   *
   * @param objectKey the object key to find a nearest Node
   * @return
   */
  public T routeNode(String objectKey) {
    return lock.readLock(() -> {
      if (ring.isEmpty()) {
        return null;
      }
      Long hashVal = hashFunction.hash(objectKey);
      SortedMap<Long, VirtualNode<T>> tailMap = ring.tailMap(hashVal);
      Long nodeHashVal = !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
      return ring.get(nodeHashVal).getPhysicalNode();
    });
  }

  /**
   * 获取某个结点的副本数量
   *
   * @param pNode 结点
   * @return 返回节点副本数量
   */
  public int getReplicas(T pNode) {
    return lock.readLock(() -> getUnsafeReplicas(pNode));
  }

  /**
   * 获取某个结点的副本数量（不加锁）
   *
   * @param pNode 结点
   * @return 返回节点副本数量
   */
  private int getUnsafeReplicas(T pNode) {
    int replicas = 0;
    for (VirtualNode<T> vNode : ring.values()) {
      if (vNode.isVirtualNodeOf(pNode)) {
        replicas++;
      }
    }
    return replicas;
  }

  /**
   * 清空结点
   */
  public void clear() {
    lock.writeLock(ring::clear);
  }
}
