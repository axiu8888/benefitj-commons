package com.benefitj.jdbc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.TimeUtils;
import com.benefitj.jdbc.sql.DatabaseConnector;
import com.benefitj.jdbc.sql.EnhanceStatement;
import com.benefitj.jdbc.sql.TableDDL;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class JdbcApplicationTests {

  private DatabaseConnector connector;

  public DatabaseConnector getConnector() {
    DatabaseConnector c = this.connector;
    if (c == null) {
      synchronized (this) {
        if ((c = this.connector) == null) {
          this.connector = new DatabaseConnector()
              .setUrl("jdbc:mysql://192.168.19.129:3306")
              .setUser("root")
              .setPassword("admin")
              .setDriver("com.mysql.cj.jdbc.Driver")
              .setAutoCommit(true)
              .setReadOnly(false)
              .setClientProperties(prop -> {
                prop.setProperty("useUnicode", "true");
                prop.setProperty("characterEncoding", "utf-8");
                prop.setProperty("serverTimezone", "Asia/Shanghai");
                prop.setProperty("allowMultiQueries", "true");
              });
          c = this.connector;
        }
      }
    }
    return c;
  }

  @Before
  public void before() throws SQLException {
    final Connection c = getConnector().getConnection();

    System.err.println("schema: " + c.getSchema()
        + ", catalog: " + c.getCatalog()
        + ", autoCommit: " + c.getAutoCommit()
        + ", holdability: " + c.getHoldability()
        + ", networkTimeout: " + c.getNetworkTimeout()
        + ", transactionIsolation: " + c.getTransactionIsolation()
        + ", typeMap: " + c.getTypeMap()
        + ", clientInfo: " + c.getClientInfo()
    );
    System.err.println();
  }

  @Test
  public void testDatabase() {
    System.err.println();
    EnhanceStatement stmt = getConnector().createStmt();
    System.err.println(stmt.getDatabases());
    System.err.println();

//    stmt.createDatabase("test");
//    stmt.use("test");
//
//    //stmt.dropDatabase("test");
//    stmt.execute("CREATE TABLE IF NOT EXISTS `ru3noob_tbl`(\n" +
//            "   `runoob_id` INT UNSIGNED AUTO_INCREMENT,\n" +
//            "   `runoob_title` VARCHAR(100) NOT NULL,\n" +
//            "   `runoob_author` VARCHAR(40) NOT NULL,\n" +
//            "   `submission_date` DATE,\n" +
//            "   PRIMARY KEY ( `runoob_id` )\n" +
//            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;"
//        , false);
    stmt.close();
  }

  @Test
  public void testTables() {
    EnhanceStatement stmt = getConnector().createStmt();
    stmt.use("quartz");
    System.err.println(stmt.showTables());
    System.err.println(JSON.toJSONString(stmt.showTableStatus()));
    stmt.close();
  }

  @Test
  public void testShowColumns() {
    EnhanceStatement stmt = getConnector().createStmt();
    stmt.use("test");
    List<JSONObject> records = stmt.queryList("show columns from `HS_PERSON`");
    System.err.println(records);
    stmt.close();
  }

  @Test
  public void testShowCreateTableDDL() {
    EnhanceStatement stmt = getConnector().createStmt();

    long start = TimeUtils.now();
    List<TableDDL> ddls = stmt.showTableDDLs("jeecg-boot");
    System.err.println(JSON.toJSONString(ddls));

//    // 创建表
//    stmt.use("test");
//    stmt.transactional(() -> {
//      TableDDL ddl = ddls.get(0);
////      for (TableDDL ddl : ddls) {
////
////      }
//      System.err.println("\n------------------------------\n");
//      System.err.println("create table: " + ddl.getDdl());
//      System.err.println("\n------------------------------\n");
//      stmt.dropTables(new String[]{ddl.getName()}, false);
//      stmt.execute(ddl.getDdl());
//    }, false);

    stmt.close();
    System.err.println("耗时: " + TimeUtils.diffNow(start));
  }

  @Test
  public void testQuery() {
    long start1 = TimeUtils.now();
    EnhanceStatement stmt = getConnector().createStmt();
    stmt.use("hsrg");
    long start2 = TimeUtils.now();
    List<JSONObject> json = stmt.queryList("SELECT count(t.type) AS count, t.type\n" +
        "FROM (SELECT DISTINCT hrt.zid AS reportZid, hrt.person_zid AS personZid, hrt.type AS type\n" +
        "      FROM HS_REPORT_TASK AS hrt\n" +
        "      LEFT JOIN HS_INPATIENT AS hi ON hi.org_zid = hrt.org_zid\n" +
        "      LEFT JOIN HS_ORG AS ho ON ho.zid = hi.org_zid\n" +
        "      WHERE hrt.`status` in ('FINISH') AND hrt.type in ('activity', 'jsyl', 'mse', 'hrv', 'physical', 'sleepStageAhi', 'holter') \t\t        AND (hi.org_zid = '0' OR (ho.auto_code LIKE concat( ( SELECT auto_code FROM HS_ORG WHERE zid = '0' LIMIT 1 ), ':%')))\n" +
        "\t) AS t\n" +
        "GROUP BY t.type");

    System.err.println(json);

    // 关闭
    stmt.close();

    System.err.println("耗时1: " + TimeUtils.diffNow(start1));
    System.err.println("耗时2: " + TimeUtils.diffNow(start2));
  }

  @Test
  public void testLoadSql() {
    EnhanceStatement stmt = getConnector().createStmt();
    stmt.use("quartz");
    try {
//      File sql = new File("D:\\临时文件\\quartz.sql");
//      List<String> lines = SqlUtils.refine(sql)
//          .stream()
//          .filter(StringUtils::isNotBlank)
//          .collect(Collectors.toList());
//      System.err.println("\n-----------------------------------------\n");
//      System.err.println(String.join("\n", lines));
//      System.err.println("\n-----------------------------------------\n");
//
//      //stmt.execute(String.join("\n", lines), true);
//
//      for (String line : lines) {
//        if (!line.startsWith("SET")) {
//          stmt.addBatch(line);
//        }
//      }
//      stmt.executeBatch();

      stmt.getRoot().safeTransaction(() -> {

        String sql = "DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`; " +
            "CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (\n" +
            "  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
            "  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
            "  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
            "  `BLOB_DATA` blob NULL,\n" +
            "  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,\n" +
            "  INDEX `SCHED_NAME`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,\n" +
            "  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT\n" +
            ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;"
                .replace("\n", "")
                .replace("\\s{2,}", " ");
        System.err.println("sql: " + sql);
        boolean execute = stmt.execute(sql);
        System.err.println("execute: " + execute);

        return null;
      });

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      stmt.close();
    }


//    File sql = new File("D:\\临时文件\\quartz.sql");
//    List<String> lines = SqlUtils.refine(sql);
//    System.err.println(String.join("\n", lines));


  }

  @Test
  public void testCreateTable() {
    EnhanceStatement stmt = getConnector().createStmt();
    stmt.use("test");
    try {
      stmt.getRoot().safeTransaction(() -> {

        String sql = "create table sys_role_permission (\n" +
            "       id bigint comment '主键' not null auto_increment,\n" +
            "        active tinyint(1) NOT NULL DEFAULT 1 comment '是否可用，默认可用',\n" +
            "        create_time datetime comment '创建时间',\n" +
            "        deleted tinyint(1) NOT NULL DEFAULT 1 comment '逻辑删除的状态: 否(0)/是(1)',\n" +
            "        update_time datetime DEFAULT NULL ON UPDATE current_timestamp() comment '修改时间',\n" +
            "        version int comment '乐观锁',\n" +
            "        permission_id varchar(32) comment '权限ID' not null,\n" +
            "        role_id varchar(32) comment '角色ID' not null,\n" +
            "        primary key (id)\n" +
            "    ) engine=InnoDB;"
                .replace("\n", "")
                .replace("\\s{2,}", " ");
        System.err.println("sql: " + sql);
        boolean execute = stmt.execute(sql);
        System.err.println("execute: " + execute);


        return null;
      });

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      stmt.close();
    }
  }

}
