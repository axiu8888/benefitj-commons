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

import org.ini4j.spi.IniBuilder;
import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

public class Ini extends BasicProfile implements Persistable, Configurable {

  public static Ini from(File file) {
    return new Ini(file);
  }

  public static Ini from(URL url) {
    return new Ini(url);
  }

  private static final long serialVersionUID = -6029486578113700585L;

  private Config _config;
  private URL _url;
  private File _file;

  public Ini() {
    _config = Config.getGlobal();
  }

  public Ini(Reader input) {
    this();
    load(input);
  }

  public Ini(InputStream input) {
    this();
    load(input);
  }

  public Ini(URL input) {
    this();
    load(input);
  }

  public Ini(File input) {
    this();
    _file = input;
    load();
  }

  @Override
  public Config getConfig() {
    return _config;
  }

  @Override
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
  public void load() {
    if (_file == null) {
      throw new IniException("文件不存在!");
    }
    load(_file);
  }

  @Override
  public void load(InputStream input) {
    load(new InputStreamReader(input, getConfig().getFileEncoding()));
  }

  @Override
  public void load(Reader input) {
    IniParser.newInstance(getConfig()).parse(input, newBuilder());
  }

  @Override
  public void load(File input) {
    try {
      load(input.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new IniException(e);
    }
  }

  @Override
  public void load(URL input) {
    if (input == null) {
      throw new IniException("URL不存在");
    }
    this._url = input;
    IniParser.newInstance(getConfig()).parse(input, newBuilder());
  }

  @Override
  public void store() {
    if (_file == null) {
      throw new IniException("无法发现文件");
    }
    store(_file);
  }

  @Override
  public void store(OutputStream output) {
    store(new OutputStreamWriter(output, getConfig().getFileEncoding()));
  }

  @Override
  public void store(Writer output) {
    store(IniFormatter.newInstance(output, getConfig()));
  }

  @Override
  public void store(File output) {
    try (final OutputStream stream = Files.newOutputStream(output.toPath());) {
      store(stream);
    } catch (IOException e) {
      throw new IniException(e);
    }
  }

  protected IniHandler newBuilder() {
    return IniBuilder.newInstance(this);
  }

  @Override
  protected void store(IniHandler formatter, Profile.Section section) {
    if (getConfig().isEmptySection() || (!section.isEmpty())) {
      super.store(formatter, section);
    }
  }

  @Override
  protected void store(IniHandler formatter, Profile.Section section, String option, int index) {
    if (getConfig().isMultiOption() || (index == (section.length(option) - 1))) {
      super.store(formatter, section, option, index);
    }
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
}
