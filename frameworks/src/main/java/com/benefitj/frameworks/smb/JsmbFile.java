package com.benefitj.frameworks.smb;

import com.benefitj.core.CatchUtils;
import jcifs.*;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFilenameFilter;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface JsmbFile extends SmbResource, SmbConstants {

  /**
   * 创建文件
   */
  static JsmbFile createSub(Jsmb jsmb, SmbFile smbFile) {
    return new JsmbFileImpl(jsmb, smbFile);
  }

  Jsmb getJsmb();

  SmbFile getSource();

  /**
   * 连接
   */
  default JsmbFile connect() {
    CatchUtils.tryThrow(() -> getSource().connect());
    return this;
  }

  /**
   * 连接子文件
   *
   * @param filename 文件名
   * @return 返回子文件连接
   */
  default JsmbFile createSub(String filename) {
    if (isFile()) {
      throw new IllegalStateException("仅支持目录");
    }
    return getJsmb().create(JsmbUtils.joint(getRelativePath(), filename));
  }

  /**
   * 获取路径
   */
  default String getPath() {
    return getSource().getPath();
  }

  /**
   * 获取 parent
   */
  default String getParent() {
    return getSource().getParent();
  }

  /**
   * 获取 share
   */
  default String getShare() {
    return getSource().getShare();
  }

  /**
   * 获取父目录
   */
  default JsmbFile getParentFile() {
    return getJsmb().connect(JsmbUtils.refine(getParentRelativePath()));
  }

  /**
   * 获取相对路径
   */
  default String getRelativePath() {
    return JsmbUtils.refine(getPath().substring(JsmbUtils.refine(getJsmb().getURL()).length()), false, true);
  }

  /**
   * 获取父目录的相对路径
   */
  default String getParentRelativePath() {
    String path = getRelativePath();
    return JsmbUtils.refine(path.substring(0, path.length() - getName().length()), false, true);
  }

  /**
   * 创建如果不存在
   */
  default void createIfNotExist() {
    createIfNotExist(false);
  }

  /**
   * 如果存在就删除
   *
   * @return 是否删除
   */
  default boolean deleteOnExist() {
    if (exists()) {
      return CatchUtils.ignore(() -> {
        getSource().delete();
        return true;
      }, false);
    }
    return false;
  }

  /**
   * 创建如果不存在
   *
   * @param dir 是否为文件夹
   */
  default void createIfNotExist(boolean dir) {
    CatchUtils.tryThrow(() -> {
      if (!getSource().exists()) {
        if (dir) {
          mkdirs();
        } else {
          try (JsmbFile parentFile = getParentFile();) {
            if (!parentFile.exists()) {
              parentFile.mkdirs();
            }
          }
          getSource().createNewFile();
        }
      }
    });
  }

  /**
   * 拷贝文件
   *
   * @param src 本地源文件
   */
  default void transferFrom(File src) {
    transferFrom(src, true);
  }

  /**
   * 拷贝文件到远程
   *
   * @param src        本地源文件
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  default void transferFrom(File src, boolean multiLevel) {
    transferFrom(src, null, multiLevel);
  }

  /**
   * 拷贝文件到远程
   *
   * @param src        本地源文件
   * @param filter     文件过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  default void transferFrom(File src, @Nullable FileFilter filter, boolean multiLevel) {
    JsmbUtils.transferFrom(src, connect(), filter, multiLevel);
  }

  /**
   * 从远程拷贝
   *
   * @param dest 本地目标文件
   */
  default void transferTo(File dest) {
    transferTo(dest, true);
  }

  /**
   * 从远程拷贝
   *
   * @param dest       本地目标文件
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  default void transferTo(File dest, boolean multiLevel) {
    transferTo(dest, null, multiLevel);
  }

  /**
   * 从远程拷贝
   *
   * @param src        本地目标文件
   * @param filter     过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   */
  default void transferTo(File src, SmbFileFilter filter, boolean multiLevel) {
    JsmbUtils.transferTo(connect(), src, filter, multiLevel);
  }

  /**
   * 列出文件
   *
   * @return 返回匹配的文件
   */
  default List<String> list() {
    return CatchUtils.tryThrow(() -> Arrays.asList(getSource().list()));
  }

  /**
   * 列出文件
   *
   * @param filter 过滤
   * @return 返回匹配的文件
   */
  default List<String> list(SmbFilenameFilter filter) {
    return CatchUtils.tryThrow(() -> Arrays.asList(getSource().list(filter)));
  }

  /**
   * 列出文件
   *
   * @param filter 过滤
   * @return 返回匹配的文件
   */
  default List<JsmbFile> listFiles(SmbFilenameFilter filter) {
    return listFiles(filter, false);
  }

  /**
   * 列出文件
   *
   * @param filter     过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   * @return 返回匹配的文件
   */
  default List<JsmbFile> listFiles(SmbFilenameFilter filter, boolean multiLevel) {
    return CatchUtils.tryThrow(() ->
        Stream.of(getSource().listFiles(filter))
            .map(f -> createSub(getJsmb(), f))
            .flatMap(jf -> multiLevel && jf.isDirectory()
                ? Stream.concat(Stream.of(jf), listFiles(filter, multiLevel).stream()) : Stream.of(jf))
            .collect(Collectors.toList()));
  }

  /**
   * 列出文件
   *
   * @return 返回匹配的文件
   */
  default List<JsmbFile> listFiles() {
    return listFiles(f -> true);
  }

  /**
   * 列出文件
   *
   * @param filter 过滤
   * @return 返回匹配的文件
   */
  default List<JsmbFile> listFiles(SmbFileFilter filter) {
    return listFiles(filter, false);
  }

  /**
   * 列出文件
   *
   * @param filter     过滤
   * @param multiLevel 是否拷贝多个层级的文件和目录
   * @return 返回匹配的文件
   */
  default List<JsmbFile> listFiles(SmbFileFilter filter, boolean multiLevel) {
    return CatchUtils.tryThrow(() ->
        Stream.of(getSource().listFiles(filter))
            .map(f -> createSub(getJsmb(), f))
            .flatMap(jf -> multiLevel && jf.isDirectory()
                ? Stream.concat(Stream.of(jf), listFiles(filter, multiLevel).stream()) : Stream.of(jf))
            .collect(Collectors.toList()));
  }

  @Override
  default SmbResourceLocator getLocator() {
    return getSource().getLocator();
  }

  @Override
  default CIFSContext getContext() {
    return getSource().getContext();
  }

  @Override
  default String getName() {
    return getSource().getName();
  }

  @Override
  default int getType() {
    return CatchUtils.tryThrow(() -> getSource().getType());
  }

  @Override
  default boolean exists() {
    return CatchUtils.tryThrow(() -> getSource().exists());
  }

  @Override
  default SmbResource resolve(String name) {
    return CatchUtils.tryThrow(() -> getSource().resolve(name));
  }

  @Override
  default long fileIndex() {
    return CatchUtils.tryThrow(() -> getSource().fileIndex());
  }

  @Override
  default int getAttributes() {
    return CatchUtils.tryThrow(() -> getSource().getAttributes());
  }

  @Override
  default boolean isHidden() {
    return CatchUtils.tryThrow(() -> getSource().isHidden());
  }

  @Override
  default boolean isFile() {
    return CatchUtils.tryThrow(() -> getSource().isFile());
  }

  @Override
  default boolean isDirectory() {
    return CatchUtils.tryThrow(() -> getSource().isDirectory());
  }

  @Override
  default boolean canWrite() {
    return CatchUtils.tryThrow(() -> getSource().canWrite());
  }

  @Override
  default boolean canRead() {
    return CatchUtils.tryThrow(() -> getSource().canRead());
  }

  @Override
  default void setReadWrite() {
    CatchUtils.tryThrow(() -> getSource().setReadWrite());
  }

  @Override
  default void setReadOnly() {
    CatchUtils.tryThrow(() -> getSource().setReadOnly());
  }

  @Override
  default void setAttributes(int attrs) {
    CatchUtils.tryThrow(() -> getSource().setAttributes(attrs));
  }

  @Override
  default void setFileTimes(long createTime, long lastModified, long lastAccess) {
    CatchUtils.tryThrow(() -> getSource().setFileTimes(createTime, lastModified, lastAccess));
  }

  @Override
  default void setLastAccess(long time) {
    CatchUtils.tryThrow(() -> getSource().setLastAccess(time));
  }

  @Override
  default void setLastModified(long time) {
    CatchUtils.tryThrow(() -> getSource().setLastModified(time));
  }

  @Override
  default void setCreateTime(long time) {
    CatchUtils.tryThrow(() -> getSource().setCreateTime(time));
  }

  @Override
  default long lastAccess() {
    return CatchUtils.tryThrow(() -> getSource().lastAccess());
  }

  @Override
  default long lastModified() {
    return CatchUtils.tryThrow(() -> getSource().lastModified());
  }

  @Override
  default long createTime() {
    return CatchUtils.tryThrow(() -> getSource().createTime());
  }

  @Override
  default void createNewFile() {
    CatchUtils.tryThrow(() -> getSource().createNewFile());
  }

  @Override
  default void mkdirs() {
    CatchUtils.tryThrow(() -> getSource().mkdirs());
  }

  @Override
  default void mkdir() {
    CatchUtils.tryThrow(() -> getSource().mkdir());
  }

  @Override
  default long getDiskFreeSpace() {
    return CatchUtils.tryThrow(() -> getSource().getDiskFreeSpace());
  }

  @Override
  default long length() {
    return CatchUtils.tryThrow(() -> getSource().length());
  }

  @Override
  default void delete() {
    CatchUtils.tryThrow(() -> getSource().delete());
  }

  @Override
  default void copyTo(SmbResource dest) {
    CatchUtils.tryThrow(() -> getSource().copyTo(dest));
  }

  @Override
  default void renameTo(SmbResource dest) {
    CatchUtils.tryThrow(() -> getSource().renameTo(dest));
  }

  @Override
  default void renameTo(SmbResource dest, boolean replace) {
    CatchUtils.tryThrow(() -> getSource().renameTo(dest, replace));
  }

  @Override
  default SmbWatchHandle watch(int filter, boolean recursive) {
    return CatchUtils.tryThrow(() -> getSource().watch(filter, recursive));
  }

  @Override
  default SID getOwnerGroup() {
    return CatchUtils.tryThrow(() -> getSource().getOwnerGroup());
  }

  @Override
  default SID getOwnerGroup(boolean resolve) {
    return CatchUtils.tryThrow(() -> getSource().getOwnerGroup(resolve));
  }

  @Override
  default SID getOwnerUser() {
    return CatchUtils.tryThrow(() -> getSource().getOwnerUser());
  }

  @Override
  default SID getOwnerUser(boolean resolve) {
    return CatchUtils.tryThrow(() -> getSource().getOwnerUser(resolve));
  }

  @Override
  default ACE[] getSecurity() {
    return CatchUtils.tryThrow(() -> getSource().getSecurity());
  }

  @Override
  default ACE[] getSecurity(boolean resolveSids) {
    return CatchUtils.tryThrow(() -> getSource().getSecurity(resolveSids));
  }

  @Override
  default ACE[] getShareSecurity(boolean resolveSids) {
    return CatchUtils.tryThrow(() -> getSource().getShareSecurity(resolveSids));
  }

  @Override
  default SmbRandomAccess openRandomAccess(String mode, int sharing) {
    return CatchUtils.tryThrow(() -> getSource().openRandomAccess(mode, sharing));
  }

  @Override
  default SmbRandomAccess openRandomAccess(String mode) {
    return CatchUtils.tryThrow(() -> getSource().openRandomAccess(mode));
  }

  @Override
  default OutputStream openOutputStream(boolean append, int openFlags, int access, int sharing) {
    return CatchUtils.tryThrow(() -> getSource().openOutputStream(append, openFlags, access, sharing));
  }

  @Override
  default OutputStream openOutputStream(boolean append, int sharing) {
    return CatchUtils.tryThrow(() -> getSource().openOutputStream(append, sharing));
  }

  @Override
  default OutputStream openOutputStream(boolean append) {
    return CatchUtils.tryThrow(() -> getSource().openOutputStream(append));
  }

  @Override
  default OutputStream openOutputStream() {
    return CatchUtils.tryThrow(() -> getSource().openOutputStream());
  }

  @Override
  default InputStream openInputStream(int flags, int access, int sharing) {
    return CatchUtils.tryThrow(() -> getSource().openInputStream(flags, access, sharing));
  }

  @Override
  default InputStream openInputStream(int sharing) {
    return CatchUtils.tryThrow(() -> getSource().openInputStream(sharing));
  }

  @Override
  default InputStream openInputStream() {
    return CatchUtils.tryThrow(() -> getSource().openInputStream());
  }

  @Override
  default void close() {
    CatchUtils.tryThrow(() -> getSource().close());
  }

  @Override
  default CloseableIterator<SmbResource> children() {
    return CatchUtils.tryThrow(() -> getSource().children());
  }

  @Override
  default CloseableIterator<SmbResource> children(String wildcard) {
    return CatchUtils.tryThrow(() -> getSource().children(wildcard));
  }

  @Override
  default CloseableIterator<SmbResource> children(ResourceNameFilter filter) {
    return CatchUtils.tryThrow(() -> getSource().children(filter));
  }

  @Override
  default CloseableIterator<SmbResource> children(ResourceFilter filter) {
    return CatchUtils.tryThrow(() -> getSource().children(filter));
  }


  class JsmbFileImpl implements JsmbFile {

    private final Jsmb jsmb;
    private final SmbFile source;

    public JsmbFileImpl(Jsmb jsmb, SmbFile source) {
      this.jsmb = jsmb;
      this.source = source;
    }

    @Override
    public Jsmb getJsmb() {
      return jsmb;
    }

    @Override
    public SmbFile getSource() {
      return source;
    }
  }
}
