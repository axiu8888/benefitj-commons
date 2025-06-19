package com.benefitj.core.annotation;

import com.benefitj.core.PlaceHolder;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.executable.Instantiator;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 获取方法返回结果
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface MethodReturn {
  /**
   * 参数名，默认和方法名一致，如果是get开头，则去掉get，并首字母小写
   */
  String name() default "";

  /**
   * 处理类
   */
  Class<? extends Handler> handler() default DefaultHandler.class;

  /**
   * 处理函数
   */
  interface Handler {

    /**
     * 处理
     *
     * @param target     目标对象
     * @param method     方法
     * @param args       参数
     * @param annotation 注解对象
     * @return 返回值
     */
    Object process(Object target, Method method, Object[] args, MethodReturn annotation);



    /**
     * 创建代理，自定义对象的返回值
     *
     * @param interfaceType 接口类型
     * @param handler       处理器
     * @param attrs         属性Map
     * @param <T>           接口类型
     * @return 返回代理对象
     */
    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler, Map<String, Object> attrs) {
      return newProxy(interfaceType, handler, new MethodReturn.DefaultHandler(attrs));
    }

    /**
     * 创建代理，自定义对象的返回值
     *
     * @param interfaceType 接口类型
     * @param handler       处理器
     * @param resultHandler 返回处理器
     * @param <T>           接口类型
     * @return 返回代理对象
     */
    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler, MethodReturn.Handler resultHandler) {
      return ProxyUtils.newProxy(interfaceType, (proxy, method, args) ->
          method.isAnnotationPresent(MethodReturn.class)
              ? resultHandler.process(proxy, method, args, method.getAnnotation(MethodReturn.class))
              : handler.invoke(proxy, method, args)
      );
    }
  }

  /**
   * 默认的处理方式
   */
  class DefaultHandler implements Handler {

    Map<String, Object> values;

    final Map<String, List<String>> methodNames = new ConcurrentHashMap<>();

    public DefaultHandler(Map<String, Object> values) {
      this.values = values;
    }

    @Override
    public Object process(Object target, Method method, Object[] args, MethodReturn annotation) {
      if (annotation.handler() != Handler.class && annotation.handler() != DefaultHandler.class) {
        Handler newHandler;
        try {
          newHandler = Instantiator.get().create(annotation.handler());
        } catch (Exception e) {
          throw new IllegalStateException(PlaceHolder.get().format("无法实例化{}对象，此对象必须有无参构造函数！", annotation.handler()));
        }
        return newHandler.process(target, method, args, annotation);
      }
      List<String> names = methodNames.get(method.getName());
      if (names == null) names = methodNames.computeIfAbsent(method.getName(), name -> {
        String newName;
        newName = name.startsWith("get") ? name.substring(3) : null;
        newName = newName == null && name.startsWith("is") ? name.substring(2) : null;
        newName = newName == null && name.startsWith("set") ? name.substring(3) : null;
        return Stream.of(
                StringUtils.isNotBlank(annotation.name()) ? annotation.name() : null,
                name,
                newName != null ? newName : null,
                newName != null ? (newName.substring(0, 1).toLowerCase() + newName.substring(1)) : null
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      });
      for (String name : names) {
        Object value = values.get(name);
        if (value != null)
          return value;
      }
      return null;
    }
  }

}
