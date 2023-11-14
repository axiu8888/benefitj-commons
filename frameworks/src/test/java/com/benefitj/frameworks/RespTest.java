package com.benefitj.frameworks;

import com.benefitj.core.IOUtils;
import com.benefitj.core.file.IWriter;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class RespTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void test1() {
    RespFilter1 ch_filter = RespFilter1.get(0, "01000860");
    RespFilter1 abd_filter = RespFilter1.get(1, "01000860");
    File src = new File("D:/home/resp/ch_zhang.txt");

  }

  @Test
  public void test2() {
    RespFilter2 ch_filter = RespFilter2.get(0, "01000860");
    RespFilter2 abd_filter = RespFilter2.get(1, "01000860");
    File src = new File("D:/home/resp/ch_zhang.txt");
    List<Double> values = IOUtils.readLines(IOUtils.newBufferedReader(src, Charset.defaultCharset().name()))
        .stream()
        .map(Double::parseDouble)
        .collect(Collectors.toList());
    System.err.println(values);
    IWriter writer = IWriter.createWriter(new File(src.getParentFile(), src.getName().replace(".txt", "_d.txt")), false);
    for (Double value : values) {
      int v = ch_filter.res_proc(value.intValue());
      writer.writeAndFlush(value + ", " + v + "\n");
    }


  }


}
