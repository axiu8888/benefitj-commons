package com.benefitj.event;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理事件的Adapter
 */
public abstract class BaseEventAdapter<E> implements EventAdapter<E> {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final TypeParameterMatcher matcher;

  public BaseEventAdapter() {
    this(BaseEventAdapter.class);
  }

  public BaseEventAdapter(Class<?> superclassType) {
    this.matcher = TypeParameterMatcher.find(this, superclassType, "E");
  }

  public boolean support(Object o) {
    return matcher.match(o);
  }

  @Subscribe
  @Override
  public final void onEvent(E event) {
    try {
      if (support(event)) {
        process(event);
      }
    } catch (Exception e) {
      log.error("throw: " + e.getMessage(), e);
    }
  }

  /**
   * 处理
   *
   * @param e 事件
   */
  public abstract void process(E e);


  /**
   * 类型参数匹配器
   */
  public static abstract class TypeParameterMatcher {

    private static final TypeParameterMatcher NOOP = new TypeParameterMatcher() {
      @Override
      public boolean match(Object msg) {
        return true;
      }
    };

    private static final Map<Class<?>, TypeParameterMatcher> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, TypeParameterMatcher>> PARAMETER_MATCHER_CACHE = new ConcurrentHashMap<>();

    public static Map<Class<?>, TypeParameterMatcher> getClassCache() {
      return CLASS_CACHE;
    }

    public static Map<Class<?>, Map<String, TypeParameterMatcher>> getParameterMatcherCache() {
      return PARAMETER_MATCHER_CACHE;
    }

    public static TypeParameterMatcher get(final Class<?> parameterType) {
      final Map<Class<?>, TypeParameterMatcher> getCache = getClassCache();

      TypeParameterMatcher matcher = getCache.get(parameterType);
      if (matcher == null) {
        if (parameterType == Object.class) {
          matcher = NOOP;
        } else {
          matcher = new ReflectiveMatcher(parameterType);
        }
        getCache.put(parameterType, matcher);
      }

      return matcher;
    }

    public static TypeParameterMatcher find(
        final Object object, final Class<?> parametrizedSuperclass, final String typeParamName) {

      final Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = getParameterMatcherCache();
      final Class<?> thisClass = object.getClass();

      Map<String, TypeParameterMatcher> map = findCache.computeIfAbsent(thisClass, k -> new HashMap<>(2));
      TypeParameterMatcher matcher = map.get(typeParamName);
      if (matcher == null) {
        matcher = get(find0(object, parametrizedSuperclass, typeParamName));
        map.put(typeParamName, matcher);
      }
      return matcher;
    }

    private static Class<?> find0(final Object object, Class<?> parametrizedSuperclass, String typeParamName) {
      final Class<?> thisClass = object.getClass();
      Class<?> currentClass = thisClass;
      for (;;) {
        if (currentClass.getSuperclass() == parametrizedSuperclass) {
          int typeParamIndex = -1;
          TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
          for (int i = 0; i < typeParams.length; i++) {
            if (typeParamName.equals(typeParams[i].getName())) {
              typeParamIndex = i;
              break;
            }
          }

          if (typeParamIndex < 0) {
            throw new IllegalStateException(
                "unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass);
          }

          Type genericSuperType = currentClass.getGenericSuperclass();
          if (!(genericSuperType instanceof ParameterizedType)) {
            return Object.class;
          }

          Type[] actualTypeParams = ((ParameterizedType) genericSuperType).getActualTypeArguments();

          Type actualTypeParam = actualTypeParams[typeParamIndex];
          if (actualTypeParam instanceof ParameterizedType) {
            actualTypeParam = ((ParameterizedType) actualTypeParam).getRawType();
          }
          if (actualTypeParam instanceof Class) {
            return (Class<?>) actualTypeParam;
          }
          if (actualTypeParam instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) actualTypeParam).getGenericComponentType();
            if (componentType instanceof ParameterizedType) {
              componentType = ((ParameterizedType) componentType).getRawType();
            }
            if (componentType instanceof Class) {
              return Array.newInstance((Class<?>) componentType, 0).getClass();
            }
          }
          if (actualTypeParam instanceof TypeVariable) {
            // Resolved type parameter points to another type parameter.
            TypeVariable<?> v = (TypeVariable<?>) actualTypeParam;
            currentClass = thisClass;
            if (!(v.getGenericDeclaration() instanceof Class)) {
              return Object.class;
            }

            parametrizedSuperclass = (Class<?>) v.getGenericDeclaration();
            typeParamName = v.getName();
            if (parametrizedSuperclass.isAssignableFrom(thisClass)) {
              continue;
            } else {
              return Object.class;
            }
          }

          return fail(thisClass, typeParamName);
        }
        currentClass = currentClass.getSuperclass();
        if (currentClass == null) {
          return fail(thisClass, typeParamName);
        }
      }
    }

    private static Class<?> fail(Class<?> type, String typeParamName) {
      throw new IllegalStateException(
          "cannot determine the type of the type parameter '" + typeParamName + "': " + type);
    }

    public abstract boolean match(Object msg);

    private static final class ReflectiveMatcher extends TypeParameterMatcher {
      private final Class<?> type;

      ReflectiveMatcher(Class<?> type) {
        this.type = type;
      }

      @Override
      public boolean match(Object msg) {
        return type.isInstance(msg);
      }
    }

    TypeParameterMatcher() {
    }
  }

}
