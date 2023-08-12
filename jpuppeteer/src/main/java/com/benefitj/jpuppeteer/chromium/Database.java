package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;
import netscape.javascript.JSObject;

/**
 * Database Domain
 */
@ChromiumApi("Database")
public interface Database {

  /**
   * Disables database tracking, prevents database events from being sent to the client.
   */
  void disable();

  /**
   * Enables database tracking, database events will now be delivered to the client.
   */
  void enable();

  /**
   * @param databaseId DatabaseId
   * @param query      string
   * @return {
   * columnNames: array[ string ]
   * values: array[ any ]
   * sqlError: Error
   * }
   */
  JSObject executeSQL(String databaseId, String query);

  /**
   * @param databaseId
   * @return {
   * tableNames: array[ string ]
   * }
   */
  JSObject getDatabaseTableNames(String databaseId, String query);

  /**
   * 事件
   */
  @Event("Database")
  public interface Events {

    /**
     * @param database Database
     */
    @Event("addDatabase")
    void addDatabase(InnerDatabase database);
  }

  /**
   * Database object.
   */
  @Data
  public class InnerDatabase {
    /**
     * Database ID.
     * DatabaseId
     */
    String id;
    /**
     * Database domain.
     */
    String domain;
    /**
     * Database name.
     */
    String name;
    /**
     * Database version.
     */
    String version;
  }

  /**
   * Database error.
   */
  @Data
  public class Error {
    /**
     * Error message.
     */
    String message;
    /**
     * Error code.
     */
    Integer code;
  }

}
