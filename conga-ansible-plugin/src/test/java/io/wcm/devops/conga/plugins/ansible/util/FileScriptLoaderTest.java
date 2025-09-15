/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.devops.conga.plugins.ansible.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Test;

class FileScriptLoaderTest {

  private static final String EXPECTED_CONTENT = "[test-group]\n"
      + "host-01 conga_node=aem-author\n"
      + "host-02 conga_node=aem-publish\n"
      + "host-03 conga_node=aem-publish\n"
      + "\n"
      + "[aem-author]\n"
      + "host-01\n"
      + "\n"
      + "[aem-publish]\n"
      + "host-02\n"
      + "host-03";

  @Test
  void testFile() throws Exception {
    String content = FileScriptLoader.readFileToString(new File("src/test/resources/inventory-sample/inventory-ini-style"));
    assertEquals(EXPECTED_CONTENT, cleanup(content));
  }

  @Test
  void testPythonScript() throws Exception {
    String content = FileScriptLoader.readFileToString(new File("src/test/resources/inventory-sample/inventory-ini-style.py"));
    assertEquals(EXPECTED_CONTENT, cleanup(content));
  }

  private String cleanup(String content) {
    return Strings.CS.remove(StringUtils.trim(content), "\r");
  }

}
