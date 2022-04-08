package com.benefitj.frameworks;

import com.benefitj.core.CatchUtils;
import com.benefitj.frameworks.smb.Jsmb;
import com.benefitj.frameworks.smb.JsmbFile;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JcifsTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void testCopyTo() {

    Jsmb jsmb = new Jsmb("hsrg", "hsrg8888", "192.168.0.180", "/");
    JsmbFile jf = jsmb.connect("");
    System.err.println(Arrays.toString(jf.list()));

    JsmbFile[] jsmbFiles = jf.listFiles(f -> CatchUtils.ignore(f::exists, false));
    System.err.println(Stream.of(jsmbFiles)
        .map(JsmbFile::getRelativePath)
        .collect(Collectors.joining(", ")));

//    // 传输到远程
//    jsmbFiles[0].transferFrom(new File("D:\\company\\肺康复系统"));

    // 从远程拷贝
    jsmbFiles[0].transferTo(new File("D:\\develop\\share-files2\\肺康复系统2"));

//    // 从一个远程传到另一个远程
//    JsmbUtils.transfer(jsmbFiles[0], jsmbFiles[1].connectSub(jsmbFiles[0].getName()));

    //jf.copyTo(new File("D:\\company\\肺康复系统"));
    //jf.copyTo(new File("D:\\company\\月报"));

    jf.close();

  }


}
