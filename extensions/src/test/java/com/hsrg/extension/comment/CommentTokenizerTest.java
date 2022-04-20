package com.hsrg.extension.comment;


import com.alibaba.fastjson.JSON;
import com.benefitj.core.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CommentTokenizerTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetJavaCode() {
    File src = new File("D:\\code\\mine\\java\\benefitj-commons\\extensions\\src\\main\\java\\com\\hsrg\\extension\\comment\\CommentTokenizer.java");
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
}