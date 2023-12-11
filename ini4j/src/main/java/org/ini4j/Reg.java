/*
 * Copyright 2005,2009 Ivan SZKIBA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ini4j;

import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;
import org.ini4j.spi.RegBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

public class Reg extends BasicRegistry implements Registry, Persistable, Configurable {
  private static final long serialVersionUID = -1485602876922985912L;
  protected static final String DEFAULT_SUFFIX = ".reg";
  protected static final String TMP_PREFIX = "reg-";
  private static final int STDERR_BUFF_SIZE = 8192;
  private static final String PROP_OS_NAME = "os.name";
  private static final boolean WINDOWS = Config.getSystemProperty(PROP_OS_NAME, "Unknown").startsWith("Windows");
  private static final char CR = '\r';
  private static final char LF = '\n';
  private Config _config;
  private File _file;

  public Reg() {
    Config cfg = Config.getGlobal().clone();

    cfg.setEscape(false);
    cfg.setGlobalSection(false);
    cfg.setEmptyOption(true);
    cfg.setMultiOption(true);
    cfg.setStrictOperator(true);
    cfg.setEmptySection(true);
    cfg.setPathSeparator(KEY_SEPARATOR);
    cfg.setFileEncoding(FILE_ENCODING);
    cfg.setLineSeparator(LINE_SEPARATOR);
    _config = cfg;
  }

  public Reg(String registryKey) throws IniException {
    this();
    read(registryKey);
  }

  public Reg(File input) throws IniException {
    this();
    _file = input;
    load();
  }

  public Reg(URL input) throws IniException {
    this();
    load(input);
  }

  public Reg(InputStream input) throws IniException {
    this();
    load(input);
  }

  public Reg(Reader input) throws IniException {
    this();
    load(input);
  }

  public static boolean isWindows() {
    return WINDOWS;
  }

  @Override
  public Config getConfig() {
    return _config;
  }

  public void setConfig(Config value) {
    _config = value;
  }

  @Override
  public File getFile() {
    return _file;
  }

  @Override
  public void setFile(File value) {
    _file = value;
  }

  @Override
  public void load() throws IniException {
    if (_file == null) {
      throw new IniException("文件找不到");
    }
    load(_file);
  }

  @Override
  public void load(InputStream input) throws IniException {
    load(new InputStreamReader(input, getConfig().getFileEncoding()));
  }

  @Override
  public void load(URL input) throws IniException {
    try {
      load(new InputStreamReader(input.openStream(), getConfig().getFileEncoding()));
    } catch (IOException e) {
      throw new IniException(e);
    }
  }

  @Override
  public void load(Reader input) throws IniException {
    int newline = 2;
    StringBuilder buff = new StringBuilder();
    try {
      for (int c = input.read(); c != -1; c = input.read()) {
        if (c == LF) {
          newline--;
          if (newline == 0) {
            break;
          }
        } else if ((c != CR) && (newline != 1)) {
          buff.append((char) c);
        }
      }
    } catch (IOException e) {
      throw new IniException(e);
    }

    if (buff.length() == 0) {
      throw new IniException("Missing version header");
    }

    if (!buff.toString().equals(getVersion())) {
      throw new IniException("Unsupported version: " + buff.toString());
    }

    IniParser.newInstance(getConfig()).parse(input, newBuilder());
  }

  @Override
  public void load(File input) throws IniException {
    try {
      load(input.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new IniException(e);
    }
  }

  public void read(String registryKey) throws IniException {
    File tmp = createTempFile();

    try {
      regExport(registryKey, tmp);
      load(tmp);
    } finally {
      tmp.delete();
    }
  }

  @Override
  public void store() throws IniException {
    if (_file == null) {
      throw new IniException("文件找不到");
    }
    store(_file);
  }

  @Override
  public void store(OutputStream output) throws IniException {
    store(new OutputStreamWriter(output, getConfig().getFileEncoding()));
  }

  @Override
  public void store(Writer output) throws IniException {
    try {
      output.write(getVersion());
      output.write(getConfig().getLineSeparator());
      output.write(getConfig().getLineSeparator());
      store(IniFormatter.newInstance(output, getConfig()));
    } catch (IOException e) {
      throw new IniException(e);
    }
  }

  @Override
  public void store(File output) throws IniException {
    try (OutputStream stream = Files.newOutputStream(output.toPath());) {
      store(stream);
    } catch (IOException e) {
      throw new IniException(e);
    }
  }

  public void write() throws IniException {
    File tmp = createTempFile();

    try {
      store(tmp);
      regImport(tmp);
    } finally {
      tmp.delete();
    }
  }

  protected IniHandler newBuilder() {
    return RegBuilder.newInstance(this);
  }

  @Override
  boolean isTreeMode() {
    return getConfig().isTree();
  }

  @Override
  char getPathSeparator() {
    return getConfig().getPathSeparator();
  }

  @Override
  boolean isPropertyFirstUpper() {
    return getConfig().isPropertyFirstUpper();
  }

  void exec(String[] args) throws IniException {
    try {
      Process proc = Runtime.getRuntime().exec(args);
      int status = proc.waitFor();

      if (status != 0) {
        Reader in = new InputStreamReader(proc.getErrorStream());
        char[] buff = new char[STDERR_BUFF_SIZE];
        int n = in.read(buff);

        in.close();
        throw new IniException(new String(buff, 0, n).trim());
      }
    } catch (Exception x) {
      throw new IniException(x);
    }
  }

  private File createTempFile() throws IniException {
    try {
      File ret = File.createTempFile(TMP_PREFIX, DEFAULT_SUFFIX);
      ret.deleteOnExit();
      return ret;
    } catch (IOException e) {
      throw new IniException(e);
    }
  }

  private void regExport(String registryKey, File file) throws IniException {
    requireWindows();
    exec(new String[]{"cmd", "/c", "reg", "export", registryKey, file.getAbsolutePath()});
  }

  private void regImport(File file) throws IniException {
    requireWindows();
    exec(new String[]{"cmd", "/c", "reg", "import", file.getAbsolutePath()});
  }

  private void requireWindows() {
    if (!WINDOWS) {
      throw new UnsupportedOperationException("Unsupported operating system or runtime environment");
    }
  }
}
