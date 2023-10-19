package com.benefitj.extension.comment;


import com.alibaba.fastjson2.JSON;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SystemProperty;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CommentTokenizerTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetJavaCode() {
    File src = new File("D:\\code\\mine\\java\\benefitj-commons\\extensions\\src\\main\\java\\com\\benefitj\\extension\\comment\\CommentTokenizer.java");
    CommentTokenizer tokenizer = new CommentTokenizer();
    char[] chars = IOUtils.readFileAsString(src).toCharArray();
    List<Comment> comments = tokenizer.parseJavaComments(chars);
    String code = tokenizer.getCode(chars, comments);
    System.err.println(JSON.toJSONString(comments));
    System.err.println(code);
    List<String> lines = CommentTokenizer.splitToLines(code);
    System.err.println(JSON.toJSONString(lines));

  }

  @Test
  public void testGetJsCode() {
//    File src = new File("D:\\code\\company\\Android\\znsx-android\\js-bridge\\src\\main\\assets\\WebViewJavascriptBridge.js");
    File src = new File("D:\\code\\company\\h5\\mui-h5\\libs\\h5-rpc.js");
    CommentTokenizer tokenizer = new CommentTokenizer();
    char[] chars = IOUtils.readFileAsString(src).toCharArray();
    List<Comment> comments = tokenizer.parseJsComments(chars);
    String code = tokenizer.getCode(chars, comments);
    System.err.println(JSON.toJSONString(comments));
    System.err.println(code);
    List<String> lines = CommentTokenizer.splitToLines(code);
    System.err.println(JSON.toJSONString(lines));
    System.err.println(CommentTokenizer.trimCode(lines));
  }

  @Test
  public void testCountCodeLine() {
    File src = new File("D:\\code\\mine\\java\\benefitj-commons\\http\\src\\main\\java");
    Set<String> set = new HashSet<>(Arrays.asList(
        "D:\\code\\company\\Android\\syzy\\app\\src\\main\\java\\com\\hsrg\\transfer\\ui",
        "D:\\code\\company\\Android\\syzy\\app\\src\\main\\java\\com\\hsrg\\transfer\\model"
    ));
    long sum = IOUtils.listFiles(src, pathname -> true, true)
        .stream()
        .filter(File::isFile)
        .filter(file -> set.stream().anyMatch(s -> file.getAbsolutePath().startsWith(s)))
        .filter(f -> f.getName().endsWith(".kt") || f.getName().endsWith(".java"))
        .mapToLong(value -> getLineCount(src.getName(), value))
        .sum();
    System.err.println("-----------------------------------");
    System.err.println("sum ==>: " + sum);
    System.err.println("-----------------------------------");
  }

  private long getLineCount(String id, File f) {
    AtomicLong counter = new AtomicLong();
    CommentTokenizer tokenizer = CommentTokenizer.INSTANCE;
    String javaCode = tokenizer.getJavaCode(IOUtils.readFileAsString(f).toCharArray());
    String javaIOTmpDir = SystemProperty.getJavaIOTmpDir();
    File tmp = IOUtils.createFile(new File(javaIOTmpDir, id + "/" + f.getName()));
    IOUtils.write(tmp, javaCode.getBytes(StandardCharsets.UTF_8), false);
    IOUtils.readLines(tmp, (str, index) -> counter.addAndGet(StringUtils.isNotBlank(str.trim()) ? 1 : 0));
    //tmp.delete();
    return counter.get();
  }
}