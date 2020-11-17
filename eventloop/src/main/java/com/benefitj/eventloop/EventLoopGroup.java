package com.benefitj.eventloop;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;

import java.io.File;
import java.io.FileReader;

public interface EventLoopGroup {
  public static void main(String[] args) {

    new Thread(() -> {
      try {
        Thread.sleep(3);
      } catch (InterruptedException ignore) {}

      for (int i = 0; i < 5; i++) {
        long start = DateFmtter.now();
        File f = new File("D:/home/znsx/log/znsx-biz.log");
        try (final FileReader reader = new FileReader(f);) {
          IOUtils.readLine(reader, s -> System.err.print(s.length() + ", "));
          System.out.println("spend: " + (DateFmtter.now() - start));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
//      List<String> lines = IOUtils.readFileLines(f, s -> true, "UTF-8", 1024 << 14);
//      lines.forEach(line -> System.out.println(line));
//      System.out.println("spend: " + (DateFmtter.now() - start));
    }).start();

  }


}
