package com.benefitj.frameworks;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.DUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SystemProperty;
import com.benefitj.frameworks.smb.Jsmb;
import com.benefitj.frameworks.smb.JsmbFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JcifsTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void testProperty() {
    System.err.println("userHome: " + SystemProperty.getUserHome());
    System.err.println("getUserDir: " + SystemProperty.getUserDir());
    System.err.println("getUserScript: " + SystemProperty.getUserScript());
    System.err.println("getJavaIOTmpDir: " + SystemProperty.getJavaIOTmpDir());
  }

  @Test
  public void testCopyTo() throws IOException {
    long start = DUtils.now();

//    Jsmb jsmb = new Jsmb("hsrg", "hsrg8888", "192.168.0.180", "/");
    Jsmb jsmb = new Jsmb("dingxiuan", "123456", "192.168.124.13", "/");
    JsmbFile jf = jsmb.connect("");
    System.err.println(jf.list());

    List<JsmbFile> jsmbFiles = jf.listFiles(f -> CatchUtils.ignore(f::exists, false) && !"IPC$/".equalsIgnoreCase(f.getName()));
    System.err.println(jsmbFiles.stream()
        .map(JsmbFile::getRelativePath)
        .collect(Collectors.joining(", ")));

//    // 传输到远程
//    jsmbFiles[0].createSub("紫衫龙王/超短交易悟道心路").transferFrom(new File("E:\\全部数据\\炒股\\紫衫龙王\\超短交易悟道心路"));

    // 从远程拷贝
    JsmbFile sub = jsmbFiles.get(0).createSub("紫衫龙王/超短交易悟道心路");
    System.err.println(JSON.toJSONString(
        sub.listFiles(file -> true, true)
            .stream()
            .map(JsmbFileInfo::of)
            .collect(Collectors.toList())));
//    sub.transferTo(new File("E:\\D$\\紫衫龙王\\超短交易悟道心路"));

    JsmbFile first = sub.listFiles(file -> true, true)
        .stream()
        .findFirst()
        .orElseGet(null);

    System.err.println(first.getPath() + ", " + first.length() + ", " + first.exists());
    first.connect();
    IOUtils.write(first.openInputStream(), new File("E:\\D$\\紫衫龙王\\超短交易悟道心路"), true);
    first.close();

//    // 从一个远程传到另一个远程
////    JsmbUtils.transfer(jsmbFiles[0], jsmbFiles[1].connectSub(jsmbFiles[0].getName()));
//
//    //jf.copyTo(new File("D:\\company\\肺康复系统"));
//    //jf.copyTo(new File("D:\\company\\月报"));

    jf.close();

    System.err.println("耗时: " + DUtils.diffNow(start));

  }


  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
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

    static JsmbFileInfo of(JsmbFile f) {
      return JsmbFileInfo.builder()
          .name(f.getName())
          .path(f.getPath())
          .parent(f.getParent())
          .share(f.getShare())
          .relativePath(f.getRelativePath())
          .length(f.length())
          .createTime(f.createTime())
          .isFile(f.isFile())
          .build();
    }

  }

}
