package com.benefitj.core.cmd;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitPull {

  private File dir;
  private int tryCount;

  public GitPull(File dir) {
    this(dir, 3);
  }

  public GitPull(File dir, int tryCount) {
    this.dir = dir;
    this.tryCount = tryCount;
  }

  public void pull() {
    List<File> pull = pull(dir);
    System.err.println("\nfail:");
    System.err.println(pull.stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.joining("\n")));
  }

  public List<File> pull(File dir) {
    List<File> failList = new LinkedList<>();
    if (dir.isDirectory()) {
      File[] files = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().equals(".git"));
      if (files != null && files.length > 0) {
        if (!pullNow(dir, getTryCount())) {
          failList.add(dir);
        }
      } else {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
          if (file.isDirectory()) {
            failList.addAll(pull(file));
          }
        }
      }
    }
    return failList;
  }

  public boolean pullNow(File dir, int tryCount) {
    tryCount = 1 + Math.max(0, tryCount);
    for (int i = 0; i < tryCount; i++) {
      CmdCall call = CmdExecutor.get().call("git pull", null, dir);
      if (call.getExitCode() == 128) {
        CmdCall tmpCall = CmdExecutor.get().call("git config --global --add safe.directory " + dir.getAbsolutePath().replace("\\", "/"));
        if (tmpCall.isSuccessful()) {
          continue;
        }
      }
      System.err.println(call.toPrintInfo((i > 0 ? String.format("retry-%d ", i) : "") + "git pull (" + dir.getName() + ")", dir));
      if (call.isSuccessful()) {
        return true;
      }
    }
    return false;
  }

  public File getDir() {
    return dir;
  }

  public void setDir(File dir) {
    this.dir = dir;
  }

  public int getTryCount() {
    return tryCount;
  }

  public void setTryCount(int tryCount) {
    this.tryCount = tryCount;
  }
}
