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

import java.io.*;
import java.net.URL;

public interface Persistable {

  File getFile();

  void setFile(File value);

  void load() throws IniException;

  void load(InputStream input) throws IniException;

  void load(Reader input) throws IniException;

  void load(File input) throws IniException;

  void load(URL input) throws IniException;

  void store() throws IniException;

  void store(OutputStream output) throws IniException;

  void store(Writer output) throws IniException;

  void store(File output) throws IniException;
}
