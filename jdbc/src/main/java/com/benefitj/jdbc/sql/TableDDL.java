package com.benefitj.jdbc.sql;

/**
 * 表 DDL
 */
public class TableDDL {

  private String name;

  private String ddl;
  /**
   * 是否未视图
   */
  private boolean view = false;

  private String characterSetClient;
  private String collationConnection;

  public TableDDL() {
  }

  public TableDDL(String name, String ddl) {
    this.name = name;
    this.ddl = ddl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDdl() {
    return ddl;
  }

  public void setDdl(String ddl) {
    this.ddl = ddl;
  }

  public boolean isView() {
    return view;
  }

  public void setView(boolean view) {
    this.view = view;
  }

  public String getCharacterSetClient() {
    return characterSetClient;
  }

  public void setCharacterSetClient(String characterSetClient) {
    this.characterSetClient = characterSetClient;
  }

  public String getCollationConnection() {
    return collationConnection;
  }

  public void setCollationConnection(String collationConnection) {
    this.collationConnection = collationConnection;
  }

  @Override
  public String toString() {
    return ddl;
  }
}
