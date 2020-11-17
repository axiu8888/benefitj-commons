package com.benefitj.core.file;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用戶文件管理
 */
public class UserFileManager extends FileManager implements IUserFileManager {

  /**
   * 用户目录的根目录
   */
  private final AtomicReference<File> userRootRef = new AtomicReference<>();

  /**
   * 用户目录的文件对象
   */
  public UserFileManager() {
  }

  public UserFileManager(File root) {
    super(root);
  }

  protected AtomicReference<File> getUserRootRef() {
    return userRootRef;
  }

  @Override
  public File getUserRoot() {
    File userRoot = getUserRootRef().get();
    if (userRoot == null) {
      throw new IllegalStateException("未设置用户");
    }
    return userRoot;
  }

  @Override
  public boolean setUsername(String username) {
    if (!isNotEmpty(username)) {
      throw new IllegalArgumentException("用户根目录名不能为空!");
    }
    this.getUserRootRef().set(new File(getRoot(), username));
    return true;
  }

  @Override
  public String getUsername() {
    return getUserRoot().getName();
  }

  @Override
  public String getUserRootPath() {
    return getUserRoot().getAbsolutePath();
  }

  @Override
  public String getFilepath(String filename) {
    filename = append(getUsername(), File.separator, filename);
    return super.getFilepath(filename);
  }

}
