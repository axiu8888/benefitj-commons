package com.benefitj.jdbc.sql;

import com.benefitj.core.functions.WrappedMap;
import com.benefitj.frameworks.cglib.SourceRoot;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class StatementRoot extends SourceRoot<Statement> implements WrappedMap<String, Object> {

  private final ThreadLocal<Boolean> transactionLocal = new ThreadLocal<>();

  private final Map<String, Object> map = new ConcurrentHashMap<>();

  public StatementRoot() {
  }

  public StatementRoot(EnhanceStatement source) {
    super(source);
  }

  @Override
  public Map<String, Object> getOriginal() {
    return map;
  }

  public <T> T safeTransaction(Callable<T> call) throws Exception {
    Boolean active = transactionLocal.get();
    if (!Boolean.TRUE.equals(active)) {
      Connection conn = getSource().getConnection();
      boolean autoCommit = conn.getAutoCommit();
      try {
        transactionLocal.set(Boolean.TRUE);
        if (autoCommit) {
          conn.setAutoCommit(false);
        }
        T result = call.call();
        conn.commit();
        return result;
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        transactionLocal.remove();
        if (autoCommit) {
          conn.setAutoCommit(autoCommit);
        }
      }
    }
    return call.call();
  }

}
