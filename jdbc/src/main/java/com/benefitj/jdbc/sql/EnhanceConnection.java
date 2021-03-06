package com.benefitj.jdbc.sql;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public interface EnhanceConnection extends Connection {

  @Override
  Statement createStatement();

  @Override
  PreparedStatement prepareStatement(String sql);

  @Override
  CallableStatement prepareCall(String sql);

  @Override
  String nativeSQL(String sql);

  @Override
  void setAutoCommit(boolean autoCommit);

  @Override
  boolean getAutoCommit();

  @Override
  void commit();

  @Override
  void rollback();

  @Override
  void close();

  @Override
  boolean isClosed();

  @Override
  DatabaseMetaData getMetaData();

  @Override
  void setReadOnly(boolean readOnly);

  @Override
  boolean isReadOnly();

  @Override
  void setCatalog(String catalog);

  @Override
  String getCatalog();

  @Override
  void setTransactionIsolation(int level);

  @Override
  int getTransactionIsolation();

  @Override
  SQLWarning getWarnings();

  @Override
  void clearWarnings();

  @Override
  Statement createStatement(int resultSetType, int resultSetConcurrency);

  @Override
  PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency);

  @Override
  CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency);

  @Override
  Map<String, Class<?>> getTypeMap();

  @Override
  void setTypeMap(Map<String, Class<?>> map);

  @Override
  void setHoldability(int holdability);

  @Override
  int getHoldability();

  @Override
  Savepoint setSavepoint();

  @Override
  Savepoint setSavepoint(String name);

  @Override
  void rollback(Savepoint savepoint);

  @Override
  void releaseSavepoint(Savepoint savepoint);

  @Override
  Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability);

  @Override
  PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability);

  @Override
  CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability);

  @Override
  PreparedStatement prepareStatement(String sql, int autoGeneratedKeys);

  @Override
  PreparedStatement prepareStatement(String sql, int[] columnIndexes);

  @Override
  PreparedStatement prepareStatement(String sql, String[] columnNames);

  @Override
  Clob createClob();

  @Override
  Blob createBlob();

  @Override
  NClob createNClob();

  @Override
  SQLXML createSQLXML();

  @Override
  boolean isValid(int timeout);

  @Override
  void setClientInfo(String name, String value) throws SQLClientInfoException;

  @Override
  void setClientInfo(Properties properties) throws SQLClientInfoException;

  @Override
  String getClientInfo(String name);

  @Override
  Properties getClientInfo();

  @Override
  Array createArrayOf(String typeName, Object[] elements);

  @Override
  Struct createStruct(String typeName, Object[] attributes);

  @Override
  void setSchema(String schema);

  @Override
  String getSchema();

  @Override
  void abort(Executor executor);

  @Override
  void setNetworkTimeout(Executor executor, int milliseconds);

  @Override
  int getNetworkTimeout();

  @Override
  void beginRequest();

  @Override
  void endRequest();

  @Override
  boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout);

  @Override
  boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout);

  @Override
  void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey);

  @Override
  void setShardingKey(ShardingKey shardingKey);

  @Override
  <T> T unwrap(Class<T> iface);

  @Override
  boolean isWrapperFor(Class<?> iface);

}
