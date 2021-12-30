package com.benefitj.jdbc.sql;

import com.benefitj.frameworks.cglib.SourceRoot;

import java.sql.Savepoint;

public class StatementRoot extends SourceRoot<EnhanceStatement> {

  private final ThreadLocal<Savepoint> savepointLocal = new ThreadLocal<>();

  public StatementRoot() {
  }

  public StatementRoot(EnhanceStatement source) {
    super(source);
  }

  public void setSavepoint(Savepoint savepoint) {
    savepointLocal.set(savepoint);
  }

  public Savepoint getSavepoint() {
    return savepointLocal.get();
  }

  public void removeSavepoint() {
    savepointLocal.remove();
  }

}
