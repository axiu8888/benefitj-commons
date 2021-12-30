package com.benefitj.jdbc.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public interface EnhanceResultSet extends ResultSet {

  @Override
  boolean next();

  @Override
  void close();

  @Override
  boolean wasNull();

  @Override
  String getString(int columnIndex);

  @Override
  boolean getBoolean(int columnIndex);

  @Override
  byte getByte(int columnIndex);

  @Override
  short getShort(int columnIndex);

  @Override
  int getInt(int columnIndex);

  @Override
  long getLong(int columnIndex);

  @Override
  float getFloat(int columnIndex);

  @Override
  double getDouble(int columnIndex);

  @Override
  BigDecimal getBigDecimal(int columnIndex, int scale);

  @Override
  byte[] getBytes(int columnIndex);

  @Override
  Date getDate(int columnIndex);

  @Override
  Time getTime(int columnIndex);

  @Override
  Timestamp getTimestamp(int columnIndex);

  @Override
  InputStream getAsciiStream(int columnIndex);

  @Override
  InputStream getUnicodeStream(int columnIndex);

  @Override
  InputStream getBinaryStream(int columnIndex);

  @Override
  String getString(String columnLabel);

  @Override
  boolean getBoolean(String columnLabel);

  @Override
  byte getByte(String columnLabel);

  @Override
  short getShort(String columnLabel);

  @Override
  int getInt(String columnLabel);

  @Override
  long getLong(String columnLabel);

  @Override
  float getFloat(String columnLabel);

  @Override
  double getDouble(String columnLabel);

  @Override
  BigDecimal getBigDecimal(String columnLabel, int scale);

  @Override
  byte[] getBytes(String columnLabel);

  @Override
  Date getDate(String columnLabel);

  @Override
  Time getTime(String columnLabel);

  @Override
  Timestamp getTimestamp(String columnLabel);

  @Override
  InputStream getAsciiStream(String columnLabel);

  @Override
  InputStream getUnicodeStream(String columnLabel);

  @Override
  InputStream getBinaryStream(String columnLabel);

  @Override
  SQLWarning getWarnings();

  @Override
  void clearWarnings();

  @Override
  String getCursorName();

  @Override
  ResultSetMetaData getMetaData();

  @Override
  Object getObject(int columnIndex);

  @Override
  Object getObject(String columnLabel);

  @Override
  int findColumn(String columnLabel);

  @Override
  Reader getCharacterStream(int columnIndex);

  @Override
  Reader getCharacterStream(String columnLabel);

  @Override
  BigDecimal getBigDecimal(int columnIndex);

  @Override
  BigDecimal getBigDecimal(String columnLabel);

  @Override
  boolean isBeforeFirst();

  @Override
  boolean isAfterLast();

  @Override
  boolean isFirst();

  @Override
  boolean isLast();

  @Override
  void beforeFirst();

  @Override
  void afterLast();

  @Override
  boolean first();

  @Override
  boolean last();

  @Override
  int getRow();

  @Override
  boolean absolute(int row);

  @Override
  boolean relative(int rows);

  @Override
  boolean previous();

  @Override
  void setFetchDirection(int direction);

  @Override
  int getFetchDirection();

  @Override
  void setFetchSize(int rows);

  @Override
  int getFetchSize();

  @Override
  int getType();

  @Override
  int getConcurrency();

  @Override
  boolean rowUpdated();

  @Override
  boolean rowInserted();

  @Override
  boolean rowDeleted();

  @Override
  void updateNull(int columnIndex);

  @Override
  void updateBoolean(int columnIndex, boolean x);

  @Override
  void updateByte(int columnIndex, byte x);

  @Override
  void updateShort(int columnIndex, short x);

  @Override
  void updateInt(int columnIndex, int x);

  @Override
  void updateLong(int columnIndex, long x);

  @Override
  void updateFloat(int columnIndex, float x);

  @Override
  void updateDouble(int columnIndex, double x);

  @Override
  void updateBigDecimal(int columnIndex, BigDecimal x);

  @Override
  void updateString(int columnIndex, String x);

  @Override
  void updateBytes(int columnIndex, byte[] x);

  @Override
  void updateDate(int columnIndex, Date x);

  @Override
  void updateTime(int columnIndex, Time x);

  @Override
  void updateTimestamp(int columnIndex, Timestamp x);

  @Override
  void updateAsciiStream(int columnIndex, InputStream x, int length);

  @Override
  void updateBinaryStream(int columnIndex, InputStream x, int length);

  @Override
  void updateCharacterStream(int columnIndex, Reader x, int length);

  @Override
  void updateObject(int columnIndex, Object x, int scaleOrLength);

  @Override
  void updateObject(int columnIndex, Object x);

  @Override
  void updateNull(String columnLabel);

  @Override
  void updateBoolean(String columnLabel, boolean x);

  @Override
  void updateByte(String columnLabel, byte x);

  @Override
  void updateShort(String columnLabel, short x);

  @Override
  void updateInt(String columnLabel, int x);

  @Override
  void updateLong(String columnLabel, long x);

  @Override
  void updateFloat(String columnLabel, float x);

  @Override
  void updateDouble(String columnLabel, double x);

  @Override
  void updateBigDecimal(String columnLabel, BigDecimal x);

  @Override
  void updateString(String columnLabel, String x);

  @Override
  void updateBytes(String columnLabel, byte[] x);

  @Override
  void updateDate(String columnLabel, Date x);

  @Override
  void updateTime(String columnLabel, Time x);

  @Override
  void updateTimestamp(String columnLabel, Timestamp x);

  @Override
  void updateAsciiStream(String columnLabel, InputStream x, int length);

  @Override
  void updateBinaryStream(String columnLabel, InputStream x, int length);

  @Override
  void updateCharacterStream(String columnLabel, Reader reader, int length);

  @Override
  void updateObject(String columnLabel, Object x, int scaleOrLength);

  @Override
  void updateObject(String columnLabel, Object x);

  @Override
  void insertRow();

  @Override
  void updateRow();

  @Override
  void deleteRow();

  @Override
  void refreshRow();

  @Override
  void cancelRowUpdates();

  @Override
  void moveToInsertRow();

  @Override
  void moveToCurrentRow();

  @Override
  Statement getStatement();

  @Override
  Object getObject(int columnIndex, Map<String, Class<?>> map);

  @Override
  Ref getRef(int columnIndex);

  @Override
  Blob getBlob(int columnIndex);

  @Override
  Clob getClob(int columnIndex);

  @Override
  Array getArray(int columnIndex);

  @Override
  Object getObject(String columnLabel, Map<String, Class<?>> map);

  @Override
  Ref getRef(String columnLabel);

  @Override
  Blob getBlob(String columnLabel);

  @Override
  Clob getClob(String columnLabel);

  @Override
  Array getArray(String columnLabel);

  @Override
  Date getDate(int columnIndex, Calendar cal);

  @Override
  Date getDate(String columnLabel, Calendar cal);

  @Override
  Time getTime(int columnIndex, Calendar cal);

  @Override
  Time getTime(String columnLabel, Calendar cal);

  @Override
  Timestamp getTimestamp(int columnIndex, Calendar cal);

  @Override
  Timestamp getTimestamp(String columnLabel, Calendar cal);

  @Override
  URL getURL(int columnIndex);

  @Override
  URL getURL(String columnLabel);

  @Override
  void updateRef(int columnIndex, Ref x);

  @Override
  void updateRef(String columnLabel, Ref x);

  @Override
  void updateBlob(int columnIndex, Blob x);

  @Override
  void updateBlob(String columnLabel, Blob x);

  @Override
  void updateClob(int columnIndex, Clob x);

  @Override
  void updateClob(String columnLabel, Clob x);

  @Override
  void updateArray(int columnIndex, Array x);

  @Override
  void updateArray(String columnLabel, Array x);

  @Override
  RowId getRowId(int columnIndex);

  @Override
  RowId getRowId(String columnLabel);

  @Override
  void updateRowId(int columnIndex, RowId x);

  @Override
  void updateRowId(String columnLabel, RowId x);

  @Override
  int getHoldability();

  @Override
  boolean isClosed();

  @Override
  void updateNString(int columnIndex, String nString);

  @Override
  void updateNString(String columnLabel, String nString);

  @Override
  void updateNClob(int columnIndex, NClob nClob);

  @Override
  void updateNClob(String columnLabel, NClob nClob);

  @Override
  NClob getNClob(int columnIndex);

  @Override
  NClob getNClob(String columnLabel);

  @Override
  SQLXML getSQLXML(int columnIndex);

  @Override
  SQLXML getSQLXML(String columnLabel);

  @Override
  void updateSQLXML(int columnIndex, SQLXML xmlObject);

  @Override
  void updateSQLXML(String columnLabel, SQLXML xmlObject);

  @Override
  String getNString(int columnIndex);

  @Override
  String getNString(String columnLabel);

  @Override
  Reader getNCharacterStream(int columnIndex);

  @Override
  Reader getNCharacterStream(String columnLabel);

  @Override
  void updateNCharacterStream(int columnIndex, Reader x, long length);

  @Override
  void updateNCharacterStream(String columnLabel, Reader reader, long length);

  @Override
  void updateAsciiStream(int columnIndex, InputStream x, long length);

  @Override
  void updateBinaryStream(int columnIndex, InputStream x, long length);

  @Override
  void updateCharacterStream(int columnIndex, Reader x, long length);

  @Override
  void updateAsciiStream(String columnLabel, InputStream x, long length);

  @Override
  void updateBinaryStream(String columnLabel, InputStream x, long length);

  @Override
  void updateCharacterStream(String columnLabel, Reader reader, long length);

  @Override
  void updateBlob(int columnIndex, InputStream inputStream, long length);

  @Override
  void updateBlob(String columnLabel, InputStream inputStream, long length);

  @Override
  void updateClob(int columnIndex, Reader reader, long length);

  @Override
  void updateClob(String columnLabel, Reader reader, long length);

  @Override
  void updateNClob(int columnIndex, Reader reader, long length);

  @Override
  void updateNClob(String columnLabel, Reader reader, long length);

  @Override
  void updateNCharacterStream(int columnIndex, Reader x);

  @Override
  void updateNCharacterStream(String columnLabel, Reader reader);

  @Override
  void updateAsciiStream(int columnIndex, InputStream x);

  @Override
  void updateBinaryStream(int columnIndex, InputStream x);

  @Override
  void updateCharacterStream(int columnIndex, Reader x);

  @Override
  void updateAsciiStream(String columnLabel, InputStream x);

  @Override
  void updateBinaryStream(String columnLabel, InputStream x);

  @Override
  void updateCharacterStream(String columnLabel, Reader reader);

  @Override
  void updateBlob(int columnIndex, InputStream inputStream);

  @Override
  void updateBlob(String columnLabel, InputStream inputStream);

  @Override
  void updateClob(int columnIndex, Reader reader);

  @Override
  void updateClob(String columnLabel, Reader reader);

  @Override
  void updateNClob(int columnIndex, Reader reader);

  @Override
  void updateNClob(String columnLabel, Reader reader);

  @Override
  <T> T getObject(int columnIndex, Class<T> type);

  @Override
  <T> T getObject(String columnLabel, Class<T> type);

  @Override
  void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength);

  @Override
  void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength);

  @Override
  void updateObject(int columnIndex, Object x, SQLType targetSqlType);

  @Override
  void updateObject(String columnLabel, Object x, SQLType targetSqlType);

  @Override
  <T> T unwrap(Class<T> iface);

  @Override
  boolean isWrapperFor(Class<?> iface);
}
