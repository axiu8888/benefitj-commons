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

/**
 * 虚拟节点
 */
public class VirtualNode<T extends Node> implements Node {

  /**
   * 物理结点
   */
  private final T physicalNode;
  /**
   * 副本索引
   */
  private final int replicaIndex;

  private final String key;

  public VirtualNode(T physicalNode, int replicaIndex) {
    this.replicaIndex = replicaIndex;
    this.physicalNode = physicalNode;

    this.key = physicalNode.getKey() + "-" + replicaIndex;
  }

  @Override
  public String getNodeAddress() {
    return physicalNode.getNodeAddress();
  }

  @Override
  public String getKey() {
    return key;
  }

  public boolean isVirtualNodeOf(T pNode) {
    if (pNode == null) {
      return false;
    }
    if (pNode == physicalNode) {
      return true;
    }
    return physicalNode.getKey().equals(pNode.getKey());
  }

  public T getPhysicalNode() {
    return physicalNode;
  }

  public int getReplicaIndex() {
    return replicaIndex;
  }
}
