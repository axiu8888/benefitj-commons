package com.benefitj.frameworks.smb;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import jcifs.smb.SmbFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jsmb {

  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 远程地址
   */
  private String remote;
  /**
   * 路径
   */
  private String path;

  private JsmbFile root;

  public Jsmb(String username, String password, String remote) {
    this(username, password, remote, "");
  }

  public Jsmb(String username, String password, String remote, String path) {
    this.username = username;
    this.password = password;
    this.remote = remote;
    this.path = path;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public JsmbFile getRoot() {
    JsmbFile root = this.root;
    synchronized (this) {
      if (root == null || !root.getPath().equals(getPath())) {
        IOUtils.closeQuietly(root);
        this.root = root = create("/");
      }
    }
    return root;
  }

  public String getURL() {
    return "smb://" + getUsername() + ":" + getPassword() + "@" + getRemote() + getPath();
  }

  @Override
  public String toString() {
    // smb://Administrator:123456@192.168.1.10/share-files
    return getURL();
  }

  public String getURL(String path) {
    return JsmbUtils.joint(getURL(), path);
  }

  public JsmbFile create(String filename) {
    return CatchUtils.tryThrow(() -> JsmbFile.createSub(this, new SmbFile(getURL(filename))));
  }

  /**
   * 拷贝文件或文件夹
   *
   * @param src 源文件
   */
  public void transferTo(File src) {
    transferTo(src, src.getName());
  }

  /**
   * 拷贝文件或文件夹
   *
   * @param src      源文件
   * @param filename 文件名：/tmp/abc/xyz.txt
   */
  public void transferTo(File src, String filename) {
    try (JsmbFile jf = create(filename);) {
      jf.transferFrom(src, true);
    }
  }

  public List<String> list() {
    return listFiles().stream()
        .filter(f -> filter(f.getSource()))
        .map(JsmbFile::getName)
        .collect(Collectors.toList());
  }

  public List<JsmbFile> listFiles() {
    return listFiles(false);
  }

  public List<JsmbFile> listFiles(boolean multiLevel) {
    return getRoot().listFiles(this::filter)
        .stream()
        .flatMap(jf -> multiLevel ? Stream.concat(Stream.of(jf), jf.listFiles(true).stream()) : Stream.of(jf))
        .collect(Collectors.toList());
  }

  public boolean filter(SmbFile f) {
    return CatchUtils.ignore(f::exists, false) && !"IPC$/".equalsIgnoreCase(f.getName());
  }

}
