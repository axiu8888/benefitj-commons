package com.benefitj.pipeline;

import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.core.local.LocalMapCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 默认的Pipeline实现
 */
public class DefaultPipeline implements Pipeline {

  private final AbstractHandlerContext head = new DefaultHandlerContext("head", null, null, null);
  private final AbstractHandlerContext tail = new DefaultHandlerContext("tail", null, null, null);

  private final AtomicInteger counter = new AtomicInteger(0);

  private final AtomicReference<Thread> sign = new AtomicReference<>();
  /**
   * 本地线程调用次数统计
   */
  private final ThreadLocal<AtomicInteger> localCallCount = ThreadLocal.withInitial(AtomicInteger::new);

  private final Set<String> nameCaches = Collections.synchronizedSet(new HashSet<>());
  private final Map<Class<?>, AtomicInteger> nameGenerator = new ConcurrentHashMap<>();

  public DefaultPipeline() {
    this.head.setNext(tail);
    this.tail.setPrev(head);
  }

  private DefaultPipeline self() {
    return this;
  }

  /**
   * 加锁
   */
  protected DefaultPipeline lock(final Runnable r) {
    final Thread current = Thread.currentThread();
    while (true) {
      if ((sign.get() == current) || sign.compareAndSet(null, current)) {
        try {
          localCallCount.get().incrementAndGet();
          r.run();
          break;
        } finally {
          if (localCallCount.get().decrementAndGet() < 1) {
            sign.set(null);
          }
        }
      }
    }
    return self();
  }

  /**
   * 创建新的 HandlerContext
   *
   * @param name    PipelineHandler名称
   * @param prev    前一个 HandlerContext
   * @param handler PipelineHandler名称
   * @param next    后一个 HandlerContext
   * @return 返回 HandlerContext
   */
  protected AbstractHandlerContext newCtx(String name,
                                          AbstractHandlerContext prev,
                                          PipelineHandler handler,
                                          AbstractHandlerContext next) {
    Class<?> handlerClass = handler != null ? handler.getClass() : DefaultHandlerContext.class;
    if (name != null && nameCaches.contains(name)) {
      throw new IllegalStateException("已经包含名称为\"" + name + "\"的Handler");
    }
    if (name == null) {
      AtomicInteger generator =
          nameGenerator.computeIfAbsent(handlerClass, clazz -> new AtomicInteger(0));
      String simpleName = handlerClass.getSimpleName();
      String thread = Thread.currentThread().getName();
      name = String.format("%s-%s-%d", thread, simpleName, generator.incrementAndGet());
    }
    nameCaches.add(name);
    return new DefaultHandlerContext(name, prev, handler, next);
  }

  /**
   * 添加 PipelineHandler
   *
   * @param name    PipelineHandler的名称
   * @param prevCtx 前一个上下文对象
   * @param handler 当前的 PipelineHandler
   * @param nextCtx 后一个上下文对象
   * @return 返回Pipeline对象
   */
  protected Pipeline addCtx0(@Nullable String name,
                             @Nonnull AbstractHandlerContext prevCtx,
                             @Nonnull PipelineHandler handler,
                             @Nonnull AbstractHandlerContext nextCtx) {
    return lock(() -> {
      AbstractHandlerContext ctx = newCtx(name, prevCtx, handler, nextCtx);
      prevCtx.setNext(ctx);
      nextCtx.setPrev(ctx);
      counter.incrementAndGet();
    });
  }

  /**
   * 移除 PipelineHandler
   *
   * @param prevCtx 前一个上下文对象
   * @param ctx     当前的 HandlerContext
   * @param nextCtx 后一个上下文对象
   * @return 返回Pipeline对象
   */
  protected Pipeline removeCtx0(@Nonnull AbstractHandlerContext prevCtx,
                                @Nonnull AbstractHandlerContext ctx,
                                @Nonnull AbstractHandlerContext nextCtx) {
    return lock(() -> {
      prevCtx.setNext(ctx);
      nextCtx.setPrev(ctx);
      ctx.setPrev(null);
      ctx.setNext(null);
      counter.decrementAndGet();
    });
  }

  /**
   * 移除 PipelineHandler
   *
   * @param test 过滤匹配的 PipelineHandler
   * @return 返回 HandlerContext 对象
   */
  protected AbstractHandlerContext removeCtx0(Predicate<AbstractHandlerContext> test) {
    final AbstractHandlerContext ctx = getCtx0(test);
    if (ctx != null) {
      removeCtx0(ctx.getPrev(), ctx, ctx.getNext());
    }
    return ctx;
  }

  /**
   * @param canHead 是否可以为 head
   * @return 返回第一个 HandlerContext
   */
  protected AbstractHandlerContext firstCtx0(boolean canHead) {
    final AbstractHandlerContext ctx = head.getNext();
    return (ctx != tail && ctx != null) ? ctx : canHead ? head : null;
  }

  /**
   * @param canTail 是否可以为 tail
   * @return 返回最后一个 HandlerContext
   */
  protected AbstractHandlerContext lastCtx0(boolean canTail) {
    final AbstractHandlerContext ctx = tail.getPrev();
    return (ctx != head && ctx != null) ? ctx : canTail ? tail : null;
  }

  /**
   * 获取 HandlerContext
   *
   * @param filter 消费
   * @return 返回查找到的 HandlerContext
   */
  @Nullable
  protected AbstractHandlerContext getCtx0(Predicate<AbstractHandlerContext> filter) {
    final AbstractHandlerContext[] ctxs = new AbstractHandlerContext[1];
    foreachDuplex(ctx -> {
      if (filter.test(ctx)) {
        ctxs[0] = ctx;
        return true;
      }
      return false;
    });
    return ctxs[0];
  }

  /**
   * 单向迭代整个Pipeline
   *
   * @param test         过滤器
   * @param defaultValue 默认值
   */
  protected void foreachSimplex(Consumer<AbstractHandlerContext> test, boolean defaultValue) {
    foreachSimplex(context -> {
      test.accept(context);
      return defaultValue;
    });
  }

  /**
   * 单向迭代整个Pipeline
   *
   * @param test 过滤器
   */
  protected void foreachSimplex(Predicate<AbstractHandlerContext> test) {
    lock(() -> {
      final int size = size();
      int count = 0;
      AbstractHandlerContext nextCtx = firstCtx0(false);
      while (nextCtx != null && count < size) {
        if (test.test(nextCtx)) {
          break;
        }
        nextCtx = nextCtx.getNext();
        if ((nextCtx == tail)) {
          break;
        }
        count++;
      }
    });
  }

  /**
   * 双向迭代整个Pipeline
   *
   * @param test 过滤器
   */
  protected void foreachDuplex(Predicate<AbstractHandlerContext> test) {
    lock(() -> {
      final int size = size();
      int count = 0;
      AbstractHandlerContext frontCtx = firstCtx0(false);
      AbstractHandlerContext backCtx = lastCtx0(false);
      while (frontCtx != null && backCtx != null && count < size) {
        if (test.test(frontCtx)) {
          break;
        }

        if (test.test(backCtx)) {
          break;
        }

        frontCtx = frontCtx.getNext();
        backCtx = backCtx.getPrev();

        count++;

        if ((frontCtx == backCtx) || (frontCtx == tail) || (backCtx == head)) {
          break;
        }
      }
    });
  }

  /**
   * 获取 HandlerContext
   *
   * @param handler handler
   * @return 返回查找到的 HandlerContext
   */
  @Nullable
  protected AbstractHandlerContext getCtx(@Nonnull PipelineHandler handler) {
    return getCtx0(ctx -> handler.equals(ctx.getHandler()));
  }

  /**
   * 获取 HandlerContext
   *
   * @param name handler的名称
   * @return 返回查找到的 HandlerContext
   */
  @Nullable
  protected AbstractHandlerContext getCtx(@Nonnull String name) {
    return getCtx0(ctx -> name.equals(ctx.getName()));
  }

  /**
   * 从前往后传递消息
   *
   * @param msg 消息
   */
  @Override
  public void fireNext(Object msg) {
    final AbstractHandlerContext ctx = firstCtx0(true);
    if (ctx != null) {
      ctx.processNext(ctx, msg);
    }
  }

  @Override
  public void fireNext(String baseName, Object msg) {
    AbstractHandlerContext ctx = getCtx(baseName);
    if (ctx != null) {
      ctx.processNext(ctx, msg);
    }
  }

  /**
   * 从后往前传递消息
   *
   * @param msg 消息
   */
  @Override
  public void firePrev(Object msg) {
    final AbstractHandlerContext ctx = lastCtx0(true);
    if (ctx != null) {
      ctx.processPrev(ctx, msg);
    }
  }

  @Override
  public void firePrev(String baseName, Object msg) {
    AbstractHandlerContext ctx = getCtx(baseName);
    if (ctx != null) {
      ctx.processPrev(ctx, msg);
    }
  }

  /**
   * @return 获取Pipeline中Handler的个数
   */
  @Override
  public int size() {
    return counter.get();
  }

  /**
   * 判断是否包含对相应的Handler
   *
   * @param name the name of the handler
   * @return 如果包含返回true，否则返回false
   */
  @Override
  public boolean contains(String name) {
    return getCtx(name) != null;
  }

  /**
   * 判断是否包含对相应的Handler
   *
   * @param handler 判断的handler
   * @return 如果包含返回true，否则返回false
   */
  @Override
  public boolean contains(PipelineHandler handler) {
    return getCtx(handler) != null;
  }

  /**
   * 在Pipeline头部添加一个 PipelineHandler
   *
   * @param name    当前PipelineHandler的名称
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  @Override
  public Pipeline addFirst(String name, PipelineHandler handler) {
    return addCtx0(name, head, handler, head.getNext());
  }

  /**
   * 在Pipeline尾部添加一个 PipelineHandler
   *
   * @param name    当前PipelineHandler的名称
   * @param handler 当前PipelineHandler
   * @return 返回Pipeline
   */
  @Override
  public Pipeline addLast(String name, PipelineHandler handler) {
    return addCtx0(name, tail.getPrev(), handler, tail);
  }

  /**
   * 在Pipeline中名称为 basicName的 PipelineHandler 之后插入一个 PipelineHandler
   *
   * @param baseName 后一个PipelineHandler的名称
   * @param name     当前PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  @Override
  public Pipeline addBefore(String baseName, String name, PipelineHandler handler) {
    final AbstractHandlerContext baseCtx = getCtx(baseName);
    if (baseCtx == null) {
      throw new NoSuchElementException("baseName = \"" + baseName + "\"");
    }
    return addCtx0(name, baseCtx.getPrev(), handler, baseCtx);
  }

  /**
   * 在Pipeline中名称为 basicName的 PipelineHandler 之后插入一个 PipelineHandler
   *
   * @param baseName 前一个PipelineHandler的名称
   * @param name     当前PipelineHandler的名称
   * @param handler  当前PipelineHandler
   * @return 返回Pipeline
   */
  @Override
  public Pipeline addAfter(String baseName, String name, PipelineHandler handler) {
    final AbstractHandlerContext baseCtx = getCtx(baseName);
    if (baseCtx == null) {
      throw new NoSuchElementException("baseName: " + baseName);
    }
    return addCtx0(name, baseCtx, handler, baseCtx.getNext());
  }

  /**
   * 移除一个PipelineHandler
   *
   * @param handler 被移除的PipelineHandler
   */
  @Override
  public void remove(@Nonnull PipelineHandler handler) {
    removeCtx0(ctx -> handler.equals(ctx.getHandler()));
  }

  /**
   * 根据名称移除一个 PipelineHandler
   *
   * @param name 被移除的PipelineHandler的名称
   * @return 返回被移除的 PipelineHandler 对象
   */
  @Nullable
  @Override
  public PipelineHandler remove(@Nonnull final String name) {
    return removeCtx0(ctx -> name.equals(ctx.getName()));
  }

  /**
   * Removes the first {@link PipelineHandler} in this pipeline.
   *
   * @return the removed handler
   */
  @Nullable
  @Override
  public PipelineHandler removeFirst() {
    final AbstractHandlerContext ctx = (AbstractHandlerContext) firstContext();
    if (ctx != null) {
      removeCtx0(ctx.getPrev(), ctx, ctx.getNext());
      return ctx.getHandler();
    }
    return null;
  }

  /**
   * Removes the last {@link PipelineHandler} in this pipeline.
   *
   * @return the removed handler
   * @throws NoSuchElementException if this pipeline is empty
   */
  @Nullable
  @Override
  public PipelineHandler removeLast() {
    final AbstractHandlerContext ctx = (AbstractHandlerContext) lastContext();
    if (ctx != null) {
      removeCtx0(ctx.getPrev(), ctx, ctx.getNext());
      return ctx.getHandler();
    }
    return null;
  }

  /**
   * Returns the first {@link PipelineHandler} in this pipeline.
   *
   * @return the first handler. {@code null} if this pipeline is empty.
   */
  @Override
  public PipelineHandler first() {
    final AbstractHandlerContext ctx = (AbstractHandlerContext) firstContext();
    return ctx != null ? ctx.getHandler() : null;
  }

  /**
   * Returns the context of the first {@link PipelineHandler} in this pipeline.
   *
   * @return the context of the first handler. {@code null} if this pipeline is empty.
   */
  @Nullable
  @Override
  public HandlerContext firstContext() {
    return firstCtx0(false);
  }

  /**
   * Returns the last {@link PipelineHandler} in this pipeline.
   *
   * @return the last handler. {@code null} if this pipeline is empty.
   */
  @Nullable
  @Override
  public PipelineHandler last() {
    final HandlerContext ctx = lastContext();
    return ctx != null ? ctx.getHandler() : null;
  }

  /**
   * Returns the context of the last {@link PipelineHandler} in this pipeline.
   *
   * @return the context of the last handler. {@code null} if this pipeline is empty.
   */
  @Nullable
  @Override
  public HandlerContext lastContext() {
    return lastCtx0(false);
  }

  /**
   * Returns the {@link PipelineHandler} with the specified name in this pipeline.
   *
   * @param name PipelineHandler的名称
   * @return the handler with the specified name. {@code null} if there's no such handler in this
   * pipeline.
   */
  @Override
  public PipelineHandler get(String name) {
    final HandlerContext ctx = getCtx(name);
    return ctx != null ? ctx.getHandler() : null;
  }

  /**
   * Returns the context object of the specified {@link PipelineHandler} in this pipeline.
   *
   * @param handler PipelineHandler
   * @return the context object of the specified handler. {@code null} if there's no such handler in
   * this pipeline.
   */
  @Override
  public HandlerContext context(PipelineHandler handler) {
    return getCtx(handler);
  }

  /**
   * Returns the context object of the {@link PipelineHandler} with the specified name in this
   * pipeline.
   *
   * @param name PipelineHandler的名称
   * @return the context object of the handler with the specified name. {@code null} if there's no
   * such handler in this pipeline.
   */
  @Override
  public HandlerContext context(String name) {
    return getCtx(name);
  }

  /**
   * Returns the {@link List} of the handler names.
   */
  @Override
  public List<String> names() {
    final List<String> nameList = new ArrayList<>(size());
    foreachSimplex(ctx -> nameList.add(ctx.getName()), false);
    return nameList;
  }

  /**
   * Converts this pipeline into an ordered {@link Map} whose keys are handler names and whose
   * values are handlers.
   */
  @Override
  public Map<String, PipelineHandler> toMap() {
    final Map<String, PipelineHandler> map = new LinkedHashMap<>(size());
    foreachSimplex((ctx) -> map.put(ctx.getName(), ctx.getHandler()), false);
    return map;
  }

  /**
   * 清空
   */
  @Override
  public void clear() {
    lock(() -> {
      final AbstractHandlerContext nextCtx = head.getNext();
      final AbstractHandlerContext prevCtx = tail.getPrev();
      if (nextCtx != tail) {
        nextCtx.setPrev(null);
      }
      if (prevCtx != head) {
        prevCtx.setNext(null);
      }
      head.setNext(tail);
      tail.setPrev(head);
      counter.set(0);
    });
  }

  /**
   * 缓存处理状态
   */
  private final LocalMapCache<String, Thread> prevHandleState = LocalCacheFactory.newConcurrentHashMapCache();
  private final LocalMapCache<String, Thread> nextHandleState = LocalCacheFactory.newConcurrentHashMapCache();

  private final class DefaultHandlerContext extends AbstractHandlerContext {

    DefaultHandlerContext(
        String name,
        AbstractHandlerContext prev,
        PipelineHandler handler,
        AbstractHandlerContext next) {
      super(name, prev, handler, next);
    }

    /**
     * @return 获取Pipeline对象
     */
    @Override
    public Pipeline pipeline() {
      return self();
    }

    /**
     * 往上一个处理器传递消息
     *
     * @param msg 消息
     */
    @Override
    public void firePrev(Object msg) {
      final String name = getName();
      try {
        final Thread state = prevHandleState.get(name);
        final AbstractHandlerContext prevCtx = getPrev();
        if (prevCtx != null) {
          if (state == null) {
            // 当前处理器处理
            prevHandleState.put(name, Thread.currentThread());
            prevCtx.processPrev(prevCtx, msg);
          } else {
            // 继续传递消息
            prevCtx.firePrev(msg);
          }
        }
      } finally {
        prevHandleState.remove(name);
      }
    }

    /**
     * 往下一个处理器传递消息
     *
     * @param msg 消息
     */
    @Override
    public void fireNext(Object msg) {
      final String name = getName();
      try {
        final AbstractHandlerContext nextCtx = getNext();
        if (nextCtx != null) {
          final Thread state = nextHandleState.get(name);
          if (state == null) {
            // 当前处理器处理
            nextHandleState.put(name, Thread.currentThread());
            nextCtx.processNext(nextCtx, msg);
          } else {
            // 继续传递消息
            nextCtx.fireNext(msg);
          }
        }
      } finally {
        nextHandleState.remove(name);
      }
    }
  }
}
