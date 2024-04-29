package com.benefitj.core.cmd;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

  public List<CmdCall> pull() {
    return pull(dir);
  }

  public List<CmdCall> pull(File dir) {
    List<CmdCall> list = new LinkedList<>();
    if (dir.isDirectory()) {
      File[] files = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().equals(".git"));
      if (files != null && files.length > 0) {
        list.add(pullNow(dir));
      } else {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
          if (file.isDirectory()) {
            list.addAll(pull(file));
          }
        }
      }
    }
    return list;
  }

  public CmdCall pullNow(File dir) {
    return CmdExecutor.get().call("git pull", null, dir);
  }

  public boolean retry(File dir, int tryCount) {
    tryCount = Math.min(1, tryCount);
    for (int i = 0; i < tryCount; i++) {
      CmdCall call = CmdExecutor.get().call("git pull", null, dir);
      if (call.getExitCode() == 128) {
        CmdCall tmpCall = gitAddSafe(dir);
        if (tmpCall.isSuccessful()) {
          continue;
        }
      }
      System.err.println(call.toPrintInfo(String.format("retry-%d ", i) + "git pull (" + dir.getName() + ")", dir));
      if (call.isSuccessful()) {
        return true;
      }
    }
    return false;
  }

  public CmdCall gitAddSafe(File dir) {
    return CmdExecutor.get().call("git config --global --add safe.directory " + dir.getAbsolutePath().replace("\\", "/"));
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
