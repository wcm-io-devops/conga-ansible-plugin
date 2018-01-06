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
package io.wcm.devops.conga.plugins.ansible.urlfile;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import io.wcm.devops.conga.generator.UrlFileManager;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;


public class AnsibleVaultUrlFilePluginTest {

  private UrlFilePluginContext context;
  private UrlFileManager urlFileManager;

  @Before
  public void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    context = new UrlFilePluginContext()
        .pluginManager(pluginManager);
    urlFileManager = new UrlFileManager(pluginManager, context);
    context.urlFileManager(urlFileManager);

    System.setProperty(AnsibleVaultPassword.SYSTEM_PROPERTY_PASSWORD_FILE, "src/test/resources/vault-sample/passwordFile");
  }

  @Test
  public void testGetFile() throws Exception {
    try (InputStream is = urlFileManager.getFile("ansible-vault:classpath:/vault-sample/test-encrypted.yml")) {
      byte[] output = IOUtils.toByteArray(is);
      byte[] expected = FileUtils.readFileToByteArray(new File("src/test/resources/vault-sample/test-unencrypted.yml"));
      assertArrayEquals(expected, output);
    }
  }

  @Test(expected = IOException.class)
  public void testInvalidFile() throws Exception {
    urlFileManager.getFile("ansible-vault:classpath:/vault-sample/nonexisting-file");
  }

}
