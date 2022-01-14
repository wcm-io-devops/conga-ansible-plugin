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
package io.wcm.devops.conga.plugins.ansible.valueencryption;

import static net.wedjaa.ansible.vault.crypto.VaultHandler.CHAR_ENCODING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import io.wcm.devops.conga.generator.spi.ValueEncryptionPlugin;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueEncryptionContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;
import net.wedjaa.ansible.vault.crypto.VaultHandler;

class AnsibleValueEncryptionPluginTest {

  @Mock
  private Logger logger;

  private PluginContextOptions pluginContextOptions;
  private ValueEncryptionContext context;
  private ValueEncryptionPlugin underTest;

  private static final String SAMPLE_VALUE = "myValue1";

  @BeforeEach
  void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .logger(logger);
    context = new ValueEncryptionContext()
        .pluginContextOptions(pluginContextOptions);
    underTest = pluginManager.get(AnsibleValueEncryptionPlugin.NAME, ValueEncryptionPlugin.class);

    System.setProperty(AnsibleVaultPassword.SYSTEM_PROPERTY_PASSWORD_FILE, "src/test/resources/vault-sample/passwordFile");
  }

  @Test
  void testEnabled() {
    assertTrue(underTest.isEnabled());
  }

  @Test
  void testEncrypt() throws IOException {
    Object encrypted = underTest.encrypt("xyz", SAMPLE_VALUE, context);
    assertNotEquals(SAMPLE_VALUE, encrypted.toString());

    assertTrue(encrypted instanceof YamlVaultValue);

    // decrypt again and validate
    String decrypted = new String(VaultHandler.decrypt(encrypted.toString().getBytes(CHAR_ENCODING),
        AnsibleVaultPassword.get()), CHAR_ENCODING);
    assertEquals(SAMPLE_VALUE, decrypted);
  }

}
