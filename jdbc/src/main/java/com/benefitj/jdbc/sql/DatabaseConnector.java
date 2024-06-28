package com.benefitj.jdbc.sql;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.ShutdownHook;
import com.benefitj.core.functions.IFunction;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 数据库连接器
 */
public class DatabaseConnector {

  public static final Set<String> SCHEMAS;

  static {
    Set<String> databases = new HashSet<>(3);
    databases.add("information_schema");
    databases.add("performance_schema");
    databases.add("mysql");
    SCHEMAS = Collections.unmodifiableSet(databases);
  }


  private String url = "jdbc:mysql://localhost:3306";
  private String user = "root";
  private String password = "admin";
  private String driver = "com.mysql.cj.jdbc.Driver";

  /**
   * 连接器
   */
  private volatile EnhanceConnection connection;
  /**
   * 是否只读
   */
  private boolean readOnly = false;
  /**
   * 是否自动提交
   */
  private boolean autoCommit = false;
  /**
   * SCHEMA
   */
  private String schema;
  /**
   * 分类
   */
  private String catalog;
  /**
   * 事物隔离等级
   */
  private Integer transactionIsolation;
  /**
   * 事物隔离等级
   */
  private Properties clientInfo = new Properties();

  public DatabaseConnector() {
  }

  public DatabaseConnector(String url, String user, String password, String driver) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.driver = driver;
  }

  public EnhanceConnection getConnection() {
    EnhanceConnection conn = this.connection;
    if (conn == null) {
      synchronized (this) {
        if ((conn = this.connection) == null) {
          try {
            Class.forName(getDriver());
            conn = (this.connection = SqlUtils.newProxy(
                EnhanceConnection.class,
                DriverManager.getConnection(getUrl(), getUser(), getPassword()))
            );
            conn.setAutoCommit(isAutoCommit());
            conn.setReadOnly(isReadOnly());

            if (StringUtils.isNotBlank(getSchema())) {
              conn.setSchema(getSchema());
            }
            if (StringUtils.isNotBlank(getCatalog())) {
              conn.setCatalog(getCatalog());
            }

            if (getTransactionIsolation() != null) {
              conn.setTransactionIsolation(getTransactionIsolation());
            }

            Properties clientInfo = conn.getClientInfo();
            if (clientInfo != null) {
              clientInfo.forEach((key, value) -> this.clientInfo.putIfAbsent(key, value));
            }
            if (!this.clientInfo.isEmpty()) {
              conn.setClientInfo(this.clientInfo);
            }

            ShutdownHook.register(() -> SqlUtils.tryThrow(connection::close));
          } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalStateException(CatchUtils.findRoot(e));
          }
        }
      }
    }
    return conn;
  }

  public String getUrl() {
    return url;
  }

  public DatabaseConnector setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getUser() {
    return user;
  }

  public DatabaseConnector setUser(String user) {
    this.user = user;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public DatabaseConnector setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getDriver() {
    return driver;
  }

  public DatabaseConnector setDriver(String driver) {
    this.driver = driver;
    return this;
  }

  public boolean isAutoCommit() {
    return autoCommit;
  }

  public DatabaseConnector setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
    return this;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public DatabaseConnector setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public String getSchema() {
    return schema;
  }

  public DatabaseConnector setSchema(String schema) {
    this.schema = schema;
    return this;
  }

  public String getCatalog() {
    return catalog;
  }

  public DatabaseConnector setCatalog(String catalog) {
    this.catalog = catalog;
    return this;
  }

  public Integer getTransactionIsolation() {
    return transactionIsolation;
  }

  public DatabaseConnector setTransactionIsolation(Integer transactionIsolation) {
    this.transactionIsolation = transactionIsolation;
    return this;
  }

  public Properties getClientInfo() {
    return clientInfo;
  }

  public DatabaseConnector setClientInfo(Properties clientInfo) {
    this.clientInfo = clientInfo;
    return this;
  }

  public DatabaseConnector setClientProperties(Consumer<Properties> consumer) {
    consumer.accept(this.clientInfo);
    return this;
  }

  /**
   * 创建EnhanceStatement
   */
  public EnhanceStatement createStmt() {
    return SqlUtils.tryThrow(() -> SqlUtils.newProxy(EnhanceStatement.class, getConnection().createStatement()));
  }

  /**
   * 创建EnhancePreparedStatement
   */
  public EnhancePreparedStatement createPstmt(IFunction<Connection, PreparedStatement> mappedFunc) {
    return SqlUtils.tryThrow(() -> SqlUtils.newProxy(EnhancePreparedStatement.class, mappedFunc.apply(getConnection())));
  }

  /**
   * 导出MYSQL数据库
   *
   * @param user       用户名
   * @param password   密码
   * @param db         数据库
   * @param exportPath 导出的路径
   */
  @Deprecated
  public void mysqldump(String user, String password, String db, String exportPath) {
    exportPath = exportPath.endsWith("/") ? exportPath : exportPath + "/";
    EnhanceStatement stmt = createStmt();
    stmt.use(db);
    stmt.execute(String.format("mysqldump -u %s -p %s > %s%s.dump", user, db, exportPath, db), false);
  }

}
