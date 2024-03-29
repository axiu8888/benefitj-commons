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
package org.ini4j.spi;

import org.ini4j.Ini4jCase;
import org.junit.Before;
import org.junit.Test;

public class RegEscapeToolTest extends Ini4jCase {
  protected RegEscapeTool instance;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    instance = RegEscapeTool.getInstance();
  }

  @Test
  public void testHexadecimal() {
    assertEquals(0, instance.hexadecimal(null).length());
    assertEquals(0, instance.hexadecimal("").length());
  }

  @Test
  public void testSingleton() throws Exception {
    assertEquals(RegEscapeTool.class, RegEscapeTool.getInstance().getClass());
  }
}
