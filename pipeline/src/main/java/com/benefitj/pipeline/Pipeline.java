package com.benefitj.pipeline;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Pipeline接口
 */
public interface Pipeline {

  /**
   * 从前往后传递消息
   *
   * @param msg 消息
   */
  void fireNext(Object msg);

  /**
   * 从前往后传递消息
   *
   * @param baseName Handler的名称
   * @param msg      消息
   */
  void fireNext(String baseName, Object msg);

  /**
   * 从后往前传递消息
   *
   * @param msg 消息
   */
  void firePrev(Object msg);

  /**
   * 从后往前传递消息
   *
   * @param baseName Handler的名称
   * @param msg      消息
   */
  void firePrev(String baseName, Object msg);

  /**
   * @return 获取Pipeline中Handler的个数
   */
  int size();

  /**
   * 判断是否包含对相应的Handler
   *
   * @param name the name of the handler
   * @return 如果包含返回true，否则返回false
   */
  boolean contains(String name);

  /**
   * 判断是否包含对相应的Handler
   *
   * @param handler 判断的handler
   * @return 如果包含返回true，否则返回false
   */
  boolean contains(PipelineHandler handler);

  /**
   * 在Pipeline头部添加一个 PipelineHandler
   *
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  default Pipeline addFirst(PipelineHandler handler) {
    return addFirst(null, handler);
  }

  /**
   * 在Pipeline头部添加一个 PipelineHandler
   *
   * @param name    当前PipelineHandler的名称
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  Pipeline addFirst(String name, PipelineHandler handler);

  /**
   * 在Pipeline尾部添加一个 PipelineHandler
   *
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  default Pipeline addLast(PipelineHandler handler) {
    return addLast(null, handler);
  }

  /**
   * 在Pipeline尾部添加一个 PipelineHandler
   *
   * @param name    当前PipelineHandler的名称
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  Pipeline addLast(String name, PipelineHandler handler);

  /**
   * 在Pipeline中插入一个 PipelineHandler
   *
   * @param baseName 后一个PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  default Pipeline addBefore(String baseName, PipelineHandler handler) {
    return addBefore(baseName, null, handler);
  }

  /**
   * 在Pipeline中名称为 basicName的 PipelineHandler 之后插入一个 PipelineHandler
   *
   * @param baseName 后一个PipelineHandler的名称
   * @param name     当前PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  Pipeline addBefore(String baseName, String name, PipelineHandler handler);

  /**
   * 在Pipeline中插入一个 PipelineHandler
   *
   * @param baseName 前一个PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  default Pipeline addAfter(String baseName, PipelineHandler handler) {
    return addAfter(baseName, null, handler);
  }

  /**
   * 在Pipeline中名称为 basicName的 PipelineHandler 之后插入一个 PipelineHandler
   *
   * @param baseName 前一个PipelineHandler的名称
   * @param name     当前PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  Pipeline addAfter(String baseName, String name, PipelineHandler handler);

  /**
   * 移除一个PipelineHandler
   *
   * @param handler 被移除的PipelineHandler
   */
  void remove(PipelineHandler handler);

  /**
   * 根据名称移除一个 PipelineHandler
   *
   * @param name 被移除的PipelineHandler的名称
   * @return 返回被移除的 PipelineHandler 对象
   */
  @Nullable
  PipelineHandler remove(String name);

  /**
   * Removes the first {@link PipelineHandler} in this pipeline.
   *
   * @return the removed handler
   * @throws NoSuchElementException if this pipeline is empty
   */
  PipelineHandler removeFirst() throws NoSuchElementException;

  /**
   * Removes the last {@link PipelineHandler} in this pipeline.
   *
   * @return the removed handler
   * @throws NoSuchElementException if this pipeline is empty
   */
  PipelineHandler removeLast() throws NoSuchElementException;

  /**
   * Returns the first {@link PipelineHandler} in this pipeline.
   *
   * @return the first handler. {@code null} if this pipeline is empty.
   */
  PipelineHandler first();

  /**
   * Returns the context of the first {@link PipelineHandler} in this pipeline.
   *
   * @return the context of the first handler. {@code null} if this pipeline is empty.
   */
  HandlerContext firstContext();

  /**
   * Returns the last {@link PipelineHandler} in this pipeline.
   *
   * @return the last handler. {@code null} if this pipeline is empty.
   */
  PipelineHandler last();

  /**
   * Returns the context of the last {@link PipelineHandler} in this pipeline.
   *
   * @return the context of the last handler. {@code null} if this pipeline is empty.
   */
  HandlerContext lastContext();

  /**
   * Returns the {@link PipelineHandler} with the specified name in this pipeline.
   *
   * @return the handler with the specified name. {@code null} if there's no such handler in this
   * pipeline.
   */
  PipelineHandler get(String name);

  /**
   * Returns the context object of the specified {@link PipelineHandler} in this pipeline.
   *
   * @return the context object of the specified handler. {@code null} if there's no such handler in
   * this pipeline.
   */
  HandlerContext context(PipelineHandler handler);

  /**
   * Returns the context object of the {@link PipelineHandler} with the specified name in this
   * pipeline.
   *
   * @return the context object of the handler with the specified name. {@code null} if there's no
   * such handler in this pipeline.
   */
  HandlerContext context(String name);

  /**
   * Returns the {@link List} of the handler names.
   */
  List<String> names();

  /**
   * Converts this pipeline into an ordered {@link Map} whose keys are handler names and whose
   * values are handlers.
   */
  Map<String, PipelineHandler> toMap();

  /**
   * 清空
   */
  void clear();
}
