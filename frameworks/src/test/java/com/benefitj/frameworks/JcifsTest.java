package com.benefitj.frameworks;

import com.benefitj.core.TimeUtils;
import com.benefitj.frameworks.smb.Jsmb;
import com.benefitj.frameworks.smb.JsmbFile;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.Test;

import java.util.stream.Collectors;

public class JcifsTest extends BaseTest {

  long start;

  @Override
  public void setUp() {
    start = TimeUtils.now();
  }

  @Override
  public void tearDown() {
    System.err.println("耗时: " + TimeUtils.diffNow(start));
  }

  @Test
  public void testTransfer() {
    Jsmb jsmb = new Jsmb("hsrg", "hsrg8888", "192.168.0.149", "/");
//    Jsmb jsmb = new Jsmb("dingxiuan", "123456", "192.168.124.13", "/");
//    System.err.println(String.join(", ", jsmb.list()));

    System.err.println(jsmb.listFiles(true)
        .stream()
        .map(JsmbFile::getRelativePath)
        .collect(Collectors.joining("\n")));

//    List<JsmbFile> files = jsmb.listFiles();
//    File src = new File("D:\\尘肺康复管理系统");
//    // 传输到远程
//    files.get(0).createSub(src).transferFrom(src);
//    // 从远程拷贝
//    files.get(0).createSub(src).transferTo(new File("D:\\尘肺康复管理系统\\tmp"));
//    // 从一个远程传到另一个远程
//    files.get(0).transferTo(files.get(1));

  }


  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class JsmbFileInfo {

    private String name;
    private String path;
    private String parent;
    private String share;
    private String relativePath;
    private long length;
    private long createTime;
    private boolean isFile;

  }

}
