package com.benefitj.jdbc.sql;

import com.alibaba.fastjson.JSONObject;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.functions.IRunnable;
import com.benefitj.frameworks.cglib.CGLibProxy;
import com.benefitj.frameworks.cglib.SourceRoot;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SqlUtils {

  /**
   * 创建代理
   *
   * @param interfaceType 接口类型
   * @param target        代理对象
   * @param <T>           接口类型
   * @return 返回代理
   */
  public static <T> T newProxy(Class<? extends T> interfaceType, Object target) {
    return newProxy(interfaceType, target, SourceRoot.class);
  }

  /**
   * 创建代理
   *
   * @param interfaceType 接口类型
   * @param target        代理对象
   * @param root          父类
   * @param <T>           接口类型
   * @return 返回代理
   */
  public static <T> T newProxy(Class<? extends T> interfaceType, Object target, Class<? extends SourceRoot> root) {
    T proxy = CGLibProxy.newProxy(root
        , new Class[]{interfaceType}
        , new Object[]{target});
    ((SourceRoot) proxy).setSource(target);
    return proxy;
  }

  /**
   * 获取 MetaData 信息
   */
  public static List<SqlMetaData> getMataDatas(ResultSetMetaData metaData) {
    return IOUtils.tryThrow(() -> {
      int columnCount = metaData.getColumnCount();
      List<SqlMetaData> metaDataList = new ArrayList<>(metaData.getColumnCount());
      for (int i = 1; i <= columnCount; i++) {
        SqlMetaData smd = new SqlMetaData();
        smd.setAutoIncrement(metaData.isAutoIncrement(i));
        smd.setCaseSensitive(metaData.isCaseSensitive(i));
        smd.setCatalogName(metaData.getCatalogName(i));
        smd.setColumnClassName(metaData.getColumnClassName(i));
        smd.setColumnDisplaySize(metaData.getColumnDisplaySize(i));
        smd.setColumnLabel(metaData.getColumnLabel(i));
        smd.setColumnName(metaData.getColumnName(i));
        smd.setColumnType(metaData.getColumnType(i));
        smd.setColumnTypeName(metaData.getColumnTypeName(i));
        smd.setCurrency(metaData.isCurrency(i));
        smd.setDefinitelyWritable(metaData.isDefinitelyWritable(i));
        smd.setNullable(metaData.isNullable(i));
        smd.setPrecision(metaData.getPrecision(i));
        smd.setReadOnly(metaData.isReadOnly(i));
        smd.setScale(metaData.getScale(i));
        smd.setSchemaName(metaData.getSchemaName(i));
        smd.setSearchable(metaData.isSearchable(i));
        smd.setSigned(metaData.isSigned(i));
        smd.setTableName(metaData.getTableName(i));
        smd.setWritable(metaData.isWritable(i));
        metaDataList.add(smd);
      }
      return metaDataList;
    });
  }

  /**
   * 获取记录
   *
   * @param set 结果集
   * @return 记录的集合
   */
  public static List<JSONObject> getRecords(ResultSet set) {
    return getRecords(set, true);
  }

  /**
   * 获取记录
   *
   * @param set   结果集
   * @param close 是否关闭数据集
   * @return 记录的集合
   */
  public static List<JSONObject> getRecords(ResultSet set, boolean close) {
    try {
      final ResultSetMetaData metaData = set.getMetaData();
      final List<SqlMetaData> data = SqlUtils.getMataDatas(metaData);
      final List<JSONObject> values = new ArrayList<>();
      while (set.next()) {
        JSONObject record = new JSONObject();
        for (SqlMetaData smd : data) {
          String columnLabel = smd.getColumnLabel();
          switch (smd.getColumnType()) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
              record.put(columnLabel, set.getString(columnLabel));
              break;
            case Types.TINYINT:
              record.put(columnLabel, set.getByte(columnLabel));
              break;
            case Types.SMALLINT:
              record.put(columnLabel, set.getShort(columnLabel));
              break;
            case Types.INTEGER:
              record.put(columnLabel, set.getInt(columnLabel));
              break;
            case Types.BIGINT:
              record.put(columnLabel, set.getLong(columnLabel));
              break;
            case Types.FLOAT:
              record.put(columnLabel, set.getFloat(columnLabel));
              break;
            case Types.DOUBLE:
              record.put(columnLabel, set.getDouble(columnLabel));
              break;
            case Types.BOOLEAN:
              record.put(columnLabel, set.getBoolean(columnLabel));
              break;
            case Types.ARRAY:
              record.put(columnLabel, set.getArray(columnLabel));
              break;
            case Types.DATE:
              record.put(columnLabel, fmt(set.getDate(columnLabel)));
              break;
            case Types.TIME:
              record.put(columnLabel, fmt(set.getTime(columnLabel)));
              break;
            case Types.TIMESTAMP:
              record.put(columnLabel, fmt(set.getTimestamp(columnLabel)));
              break;
            case Types.BLOB:
              record.put(columnLabel, set.getBlob(columnLabel));
              break;
            case Types.BINARY:
              record.put(columnLabel, set.getBinaryStream(columnLabel));
              break;
            default:
              record.put(columnLabel, "@" + JdbcType.of(smd.getColumnType()) + "__unknown@");
              break;
          }
        }
        values.add(record);
      }
      return values;
    } catch (SQLException e) {
      throw new IllegalSQLException(e);
    } finally {
      if (close) {
        IOUtils.closeQuietly(set);
      }
    }
  }

  /**
   * 判断是否为 Getter 方法
   *
   * @param name 方法名称
   * @return 返回是否为Getter方法
   */
  public static boolean isGetter(String name) {
    return name.startsWith("get") || name.startsWith("is");
  }

  /**
   * 获取字段名
   *
   * @param name 方法名
   * @return 返回字段名
   */
  public static String getFieldName(String name) {
    if (name.startsWith("get")) {
      return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    } else if (name.startsWith("is")) {
      return Character.toLowerCase(name.charAt(2)) + name.substring(3);
    }
    return name;
  }

  /**
   * 判断参数类型
   *
   * @param method Method对象
   * @param types  类型数组
   * @return 返回是否匹配
   */
  public static boolean isParameterType(Method method, Class<?>... types) {
    if (types != null && types.length == method.getParameterCount()) {
      for (int i = 0; i < types.length; i++) {
        //if (!types[i].isAssignableFrom(method.getParameterTypes()[i])) {
        if (types[i] != method.getParameterTypes()[i]) {
          return false;
        }
      }
      return true;
    }
    return types != null && types.length == 0;
  }

  public static String fmt(Object time) {
    return time != null ? DateFmtter.fmt(time) : "";
  }


  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call) {
    try {
      return call.call();
    } catch (IllegalSQLException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalSQLException(e);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r) {
    try {
      r.run();
    } catch (IllegalSQLException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalSQLException(e);
    }
  }

  public static String joint(List<?> values, String separator) {
    if (values.isEmpty()) {
      return "";
    }
    if (values.size() == 1) {
      return String.valueOf(values.get(0));
    }
    StringBuilder sb = new StringBuilder();
    for (Object v : values) {
      sb.append(separator).append(v);
    }
    sb.delete(0, sb.length());
    return sb.toString();
  }
}
