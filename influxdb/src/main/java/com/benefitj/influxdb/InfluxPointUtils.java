package com.benefitj.influxdb;

import org.apache.commons.lang3.StringUtils;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.benefitj.influxdb.annotations.InfluxIgnore;
import com.benefitj.influxdb.annotations.InfluxTagNullable;
import com.benefitj.influxdb.converter.AbstractConverter;
import com.benefitj.influxdb.converter.ColumnProperty;
import com.benefitj.influxdb.converter.LineProtocolConverter;
import com.benefitj.influxdb.converter.PointConverter;
import com.benefitj.influxdb.dto.FieldKey;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InfluxDB的工具类
 */
public final class InfluxPointUtils {

  public static final Map<Class, PointConverter> POINT_CONVERTERS = new ConcurrentHashMap<>();
  public static final Map<Class, LineProtocolConverter> LINE_PROTOCOL_CONVERTERS = new ConcurrentHashMap<>();

  private static volatile boolean TIME_METHOD_ERROR = false;
  private static volatile Method TIME_METHOD = null;

  private static final Logger log = LoggerFactory.getLogger(InfluxPointUtils.class);


  /**
   * 获取 Point 转换器
   *
   * @param clazz 类
   * @param <T>
   * @return 返回 Point 转换器
   */
  @SuppressWarnings("all")
  public static <T> PointConverter<T> getPointConverter(Class<? extends T> clazz) {
    return POINT_CONVERTERS.computeIfAbsent(clazz, c -> createPointConverter(c));
  }

  /**
   * 获取行协议转换器
   *
   * @param clazz 类
   * @param <T>
   * @return 返回行协议转换器
   */
  @SuppressWarnings("all")
  public static <T> LineProtocolConverter<T> getLineProtocolConverter(Class<T> clazz) {
    return LINE_PROTOCOL_CONVERTERS.computeIfAbsent(clazz, c -> createLineProtocol(c));
  }

  public static <T> PointConverter<T> createPointConverter(Class<T> type) {
    return parse(new PointConverter<T>(type));
  }

  public static <T> LineProtocolConverter<T> createLineProtocol(Class<T> type) {
    return parse(new LineProtocolConverter<T>(type));
  }

  public static <T, U, C extends AbstractConverter<T, U>> C parse(C converter) {
    final Class<T> type = converter.getType();
    if (!type.isAnnotationPresent(Measurement.class)) {
      throw new IllegalStateException("\"" + type + "\"没有被\"org.influxdb.annotation.Measurement\"注解注释!");
    }

    Measurement measurement = type.getAnnotation(Measurement.class);
    converter.setMeasurement(isNotBlank(measurement.name()) ? measurement.name() : type.getSimpleName());

    // 排除被static和final修饰的字段、忽略InfluxIgnore注解的字段
    Map<String, Field> fieldMap = InfluxPointUtils.getFieldMap(type, field ->
        !(InfluxPointUtils.isStaticOrFinal(field)
            || field.isAnnotationPresent(InfluxIgnore.class)));

    if (fieldMap.isEmpty()) {
      throw new IllegalArgumentException("The columns is empty!");
    }

    // 时间戳单位
    converter.setTimestampUnit(measurement.timeUnit());

    for (Field field : fieldMap.values()) {
      final ColumnProperty property = parseColumn(field);
      if (isTimestamp(field)) {
        converter.setTimestamp(property);
      } else {
        if (property.isTag()) {
          converter.putTag(property);
        } else {
          converter.putColumn(property);
        }
      }
    }
    return converter;
  }

  /**
   * 判断是否为时间戳
   *
   * @param field 字段
   * @return 返回是否为时间戳的判断
   */
  public static boolean isTimestamp(Field field) {
    return field.isAnnotationPresent(TimeColumn.class);
  }

  /**
   * 解析字段
   */
  public static ColumnProperty parseColumn(Field field) {
    ColumnProperty property = new ColumnProperty(field);
    return parseColumn(property, isTimestamp(field));
  }

  /**
   * 解析字段
   */
  public static ColumnProperty parseColumn(ColumnProperty property, boolean timestamp) {
    Field field = property.getField();
    Column column = field.getAnnotation(Column.class);
    if (column != null) {
      property.setColumn(isNotBlank(column.name()) ? column.name() : field.getName());
      if (timestamp) {
        property.setTagNullable(false);
      } else {
        property.setTag(column.tag());
        if (column.tag()) {
          if (field.getType() != String.class) {
            throw new IllegalStateException("InfluxDB中tag只能为java.lang.String类型, \""
                + column.name() + "\"的类型为\"" + field.getType() + "\"");
          }
          // 设置TAG是否允许为 null
          InfluxTagNullable tagNullable = field.getAnnotation(InfluxTagNullable.class);
          property.setTagNullable(tagNullable == null || tagNullable.value());
        }
      }
    } else {
      property.setColumn(field.getName());
      property.setTag(false);
    }
    return property;
  }

  /**
   * 转换成 Point
   *
   * @param item ITEM
   * @param <T>  类型
   * @return 返回 Point
   */
  public static <T> Point toPoint(T item) {
    return ((PointConverter<T>) getPointConverter(item.getClass())).convert(item);
  }

  /**
   * 转换成 Point
   *
   * @param item ITEM
   * @param <T>  类型
   * @return 返回 Point
   */
  public static <T> Point toPoint(AbstractConverter<T, ?> converter, T item) {
    if (item instanceof Point) {
      return (Point) item;
    }

    final Point.Builder builder = Point.measurement(converter.getMeasurement());
    // 设置时间戳
    Long timestamp = getTimestamp(converter.getTimestamp(), item);
    if (TIME_METHOD_ERROR) {
      try {
        TIME_METHOD.invoke(builder, timestamp, converter.getTimestampUnit());
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    } else {
      try {
        builder.time(timestamp, converter.getTimestampUnit());
      } catch (NoSuchMethodError e) {
        try {
          TIME_METHOD = builder.getClass().getDeclaredMethod("time", long.class, TimeUnit.class);
          TIME_METHOD_ERROR = true;
          TIME_METHOD.invoke(builder, timestamp, converter.getTimestampUnit());
        } catch (Exception ee) {
          throw new IllegalStateException(ee);
        }
        log.warn("throw: {}", e.getMessage());
      }
    }

    // TAG
    final Map<String, ColumnProperty> tags = converter.getTags();
    tags.forEach((tag, property) -> {
      Object value = InfluxPointUtils.getFieldValue(property.getField(), item);
      // 检查是否允许tag为null，默认不允许
      if (!property.isTagNullable() && value == null) {
        throw new NullPointerException("tag is null.");
      }
      if (value != null) {
        builder.tag(tag, (String) value);
      }
      // ~ 忽略值为null的tag
    });

    // column
    final Map<String, ColumnProperty> columns = converter.getColumns();
    columns.forEach((name, property) -> {
      Object value = InfluxPointUtils.getFieldValue(property.getField(), item);
      if (value instanceof Number) {
        builder.addField(name, (Number) value);
      } else if (value instanceof Boolean) {
        builder.addField(name, (Boolean) value);
      } else if (value instanceof String) {
        builder.addField(name, (String) value);
      } else {
        if (value != null) {
          builder.addField(name, String.valueOf(value));
        }
        // ~ 忽略值为null的字段
      }
    });
    return builder.build();
  }

  /**
   * 转换成行协议数据
   *
   * @param payload
   * @return 返回 line protocol String
   */
  public static List<String> toLineProtocol(Object payload) {
    if (payload instanceof Collection) {
      return toLineProtocol((Collection)payload);
    } else {
      LineProtocolConverter<Object> converter = getLineProtocolConverter((Class<Object>) payload.getClass());
      return Collections.singletonList(converter.convert(payload));
    }
  }

  /**
   * 转换成行协议数据
   *
   * @param payload
   * @return 返回 line protocol String
   */
  public static List<String> toLineProtocol(Collection<?> payload) {
    return payload.stream()
        .filter(Objects::nonNull)
        .flatMap(t -> {
          if (t instanceof CharSequence) {
            return Stream.of(t.toString());
          } else if (t instanceof Point) {
            return Stream.of(((Point) t).lineProtocol());
          } else if (t instanceof Collection) {
            return toLineProtocol((Collection) t).stream();
          } else {
            return Stream.of(toPoint(t).lineProtocol());
          }
        })
        .collect(Collectors.toList());
  }

  /**
   * 转换成行协议数据
   *
   * @param item ITEM
   * @param <T>  类型
   * @return 返回 Point
   */
  @SuppressWarnings("all")
  public static <T> String toLineProtocol(LineProtocolConverter<T> converter, T item) {
    if (item instanceof CharSequence) {
      return item.toString();
    }
    return toPoint(converter, item).lineProtocol();
  }

  /**
   * 转换成行协议数据
   *
   * @param points Point list
   * @return 返回 line protocol string
   */
  public static String toLineProtocol(List<Point> points) {
    return toLineProtocol(points, null);
  }

  /**
   * 转换成行协议数据
   *
   * @param points    Point list
   * @param precision 时间片单位
   * @return 返回 line protocol string
   */
  public static String toLineProtocol(List<Point> points, TimeUnit precision) {
    final BatchPoints ops = BatchPoints.database(null)
        .precision(precision)
        .build();
    points.forEach(ops::point);
    return ops.lineProtocol();
  }

  /**
   * 转换成 Point
   *
   * @param result    query result
   * @param fieldKeys field keys
   * @return return Point
   */
  public static List<Point> toPoint(QueryResult result, Map<String, FieldKey> fieldKeys) {
    return result.getResults()
        .stream()
        .flatMap(r -> {
          List<QueryResult.Series> series = r.getSeries();
          return series != null ? series.stream() : Stream.empty();
        })
        .flatMap(series -> {
          List<String> columns = series.getColumns();
          for (int i = 0; i < columns.size(); i++) {
            final FieldKey fieldKey = fieldKeys.get(columns.get(i));
            if (fieldKey == null) {
              throw new IllegalArgumentException("not found column[\"" + columns.get(i) + "\"]");
            }
            fieldKey.setIndex(i);
          }
          final List<Point> points = new LinkedList<>();
          List<List<Object>> values = series.getValues();
          for (List<Object> value : values) {
            points.add(toPoint(series.getName(), fieldKeys, value));
          }
          return points.stream();
        })
        .collect(Collectors.toList());
  }

  /**
   * 转换成 Point
   *
   * @param measurement MEASUREMENT
   * @param fieldKeys   field keys
   * @param values      values
   * @return return Point
   */
  public static Point toPoint(String measurement, Map<String, FieldKey> fieldKeys, List<Object> values) {
    final Point.Builder builder = Point.measurement(measurement);
    for (FieldKey fieldKey : fieldKeys.values()) {
      String column = fieldKey.getColumn();
      Object value = values.get(fieldKey.getIndex());
      if (fieldKey.isTimestamp()) {
        builder.time(TimeUtil.fromInfluxDBTimeFormat((String) value), TimeUnit.MILLISECONDS);
      } else {
        if (fieldKey.isTag()) {
          if (value != null) {
            builder.tag(column, (String) value);
          }
        } else {
          if (value instanceof Number) {
            builder.addField(column, fieldKey.getNumber(value));
          } else if (value instanceof CharSequence) {
            builder.addField(column, value.toString());
          } else if (value instanceof Boolean) {
            builder.addField(column, (Boolean) value);
          }
        }
      }
    }
    return builder.build();
  }

  /**
   * 获取对象的时间戳
   */
  public static long getTimestamp(ColumnProperty property, Object item) {
    // 设置时间戳
    Object value = InfluxPointUtils.getFieldValue(property.getField(), item);
    if (value instanceof Date) {
      return ((Date) value).getTime();
    } else if (value instanceof Long) {
      return (Long) value;
    }
    throw new NullPointerException("timestamp");
  }


  private static boolean isNotBlank(CharSequence cs) {
    return StringUtils.isNotBlank(cs);
  }


  /**
   * 获取父类的泛型参数类型
   */
  public static Class getGenericSuperclassBounds(Class clazz) {
    Type type = clazz.getGenericSuperclass();
    while (!(type instanceof Class)) {
      if (type instanceof WildcardType) {
        type = ((WildcardType) type).getUpperBounds()[0];
      } else if (type instanceof TypeVariable<?>) {
        type = ((TypeVariable<?>) type).getBounds()[0];
      } else if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) {
          return Object.class;
        }
        if (types.length > 1) {
          throw new RuntimeException(clazz.getName()
              + "继承的泛型" + parameterizedType + "的实参数量多于1个");
        }
        type = parameterizedType.getActualTypeArguments()[0];
      } else if (type instanceof GenericArrayType) {
        type = ((GenericArrayType) type).getGenericComponentType();
      }
    }
    return (Class) type;
  }

  /**
   * 迭代Class
   *
   * @param type        类
   * @param interceptor 拦截器
   * @param filter      过滤器
   * @param handler     处理器
   */
  private static <T> void foreach(Class<?> type, Call<Class<?>, T[]> call, Predicate<T> interceptor,
                                  Predicate<T> filter, Handler<T> handler) {

    if (type == null || type == Object.class) {
      return;
    }

    T[] ts = call.apply(type);
    for (T field : ts) {
      if (filter != null) {
        if (filter.test(field)) {
          handler.onHandle(field);
        }
      } else {
        handler.onHandle(field);
      }
      if (interceptor.test(field)) {
        return;
      }
    }
    foreach(type.getSuperclass(), call, interceptor, filter, handler);
  }

  /**
   * 是否被static 和 final 修饰
   *
   * @param modifiers 修饰符
   * @return 返回结果
   */
  public static boolean isStaticOrFinal(int modifiers) {
    return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
  }

  /**
   * 是否为静态或者final字段
   *
   * @param member 字段
   * @return 返回判断的值
   */
  public static boolean isStaticOrFinal(Member member) {
    return isStaticOrFinal(member.getModifiers());
  }

  /**
   * 获取某个字段
   *
   * @param type  类型
   * @param field 字段
   * @return 返回获取的字段对象
   */
  public static Field getField(Class<?> type, String field) {
    if (isNonNull(type, field) && !field.isEmpty() && type != Object.class) {
      try {
        return type.getDeclaredField(field);
      } catch (NoSuchFieldException e) {/* ignore */}
      return getField(type.getSuperclass(), field);
    }
    return null;
  }

  /**
   * 获取存储类字段的Map
   *
   * @param type    类
   * @param filter  过滤器
   * @param handler 处理器
   * @return 返回存储字段的Map
   */
  public static void foreachField(Class<?> type, Predicate<Field> filter, Handler<Field> handler) {
    foreachField(type, FIELD_INTERCEPTOR, filter, handler);
  }

  /**
   * 获取存储类字段的Map
   *
   * @param type        类
   * @param interceptor 拦截器
   * @param filter      过滤器
   * @param handler     处理器
   * @return 返回存储字段的Map
   */
  public static void foreachField(Class<?> type, Predicate<Field> interceptor,
                                  Predicate<Field> filter, Handler<Field> handler) {
    foreach(type, FIELDS_CALL, interceptor, filter, handler);
  }

  /**
   * 获取存储类字段的Map
   *
   * @param type   类
   * @param filter 过滤器
   * @return 返回存储字段的Map
   */
  public static Map<String, Field> getFieldMap(Class<?> type, Predicate<Field> filter) {
    Map<String, Field> fieldMap = new LinkedHashMap<>();
    return getFieldMap(type, fieldMap, filter);
  }

  /**
   * 获取存储类字段的Map
   *
   * @param type     类
   * @param fieldMap 存储字段的Map
   * @param filter   过滤器
   * @return 返回存储字段的Map
   */
  public static Map<String, Field> getFieldMap(
      Class<?> type, Map<String, Field> fieldMap, Predicate<Field> filter) {
    foreachField(type, filter, field -> fieldMap.putIfAbsent(field.getName(), field));
    return fieldMap;
  }

  /**
   * 给对象中某个字段设置值
   *
   * @param o     对象
   * @param field 字段
   * @param value 值
   * @param <T>   类型
   */
  public static <T> void setFieldValue(T o, Field field, Object value) {
    if (field != null && o != null) {
      setAccessible(field, true);
      try {
        field.set(o, value);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 获取对象某字段的值
   *
   * @param field 字段对象
   * @param o     对象
   * @return 返回获取的值
   */
  public static <T> Object getFieldValue(Field field, T o) {
    return getFieldValue(field, o, null);
  }

  /**
   * 获取对象某字段的值
   *
   * @param field 字段对象
   * @param o     对象
   * @return 返回获取的值
   */
  public static Object getFieldValue(Field field, Object o, Object defaultValue) {
    if (isNonNull(o, field)) {
      setAccessible(field, true);
      try {
        Object value = field.get(o);
        return value != null ? value : defaultValue;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return defaultValue;
  }

  /**
   * 获取基本的字段和值对象
   *
   * @param o 对象
   * @return 返回字段集合对应的值
   */
  public static Map<String, Object> getFieldValues(Object o) {
    if (o != null && o.getClass() != Object.class) {
      Map<String, Field> fieldMap = getFieldMap(
          o.getClass(), field -> !isStaticOrFinal(field));
      return getFieldValues(fieldMap, o);
    }
    return Collections.emptyMap();
  }

  /**
   * 获取基本的字段和值对象
   *
   * @param fieldMap 字段集合
   * @param o        对象
   * @return 返回字段集合对应的值
   */
  public static Map<String, Object> getFieldValues(Map<String, Field> fieldMap, Object o) {
    if (fieldMap != null && !fieldMap.isEmpty()) {
      final Map<String, Object> values = new LinkedHashMap<>(fieldMap.size());
      for (String name : fieldMap.keySet()) {
        Object value = getFieldValue(fieldMap.get(name), o);
        if (value != null && value.getClass() != Object.class && !(value instanceof Date)) {
          value = getFieldValues(value);
        }
        values.put(name, value);
      }
      return values;
    }
    return Collections.emptyMap();
  }

  /**
   * 设置是否可以访问
   *
   * @param ao   可访问对象
   * @param flag 是否可以访问
   */
  public static void setAccessible(AccessibleObject ao, boolean flag) {
    if (ao != null) {
      if (!(flag && ao.isAccessible())) {
        ao.setAccessible(flag);
      }
    }
  }


  private static final Predicate<Field> FIELD_INTERCEPTOR = field -> false;
  private static final Call<Class<?>, Field[]> FIELDS_CALL = Class::getDeclaredFields;

  /**
   * 回调
   *
   * @param <T>
   */
  public interface Handler<T> {
    void onHandle(T t);
  }


  public interface Call<T, V> {
    /**
     * @param t
     * @return
     */
    V apply(T t);
  }

  private static boolean isNonNull(Object... os) {
    for (Object o : os) {
      if (o == null) {
        return false;
      }
    }
    return true;
  }
}
