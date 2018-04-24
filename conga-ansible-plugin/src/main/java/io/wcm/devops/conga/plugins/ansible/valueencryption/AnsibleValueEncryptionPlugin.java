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

import java.io.IOException;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueEncryptionPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueEncryptionContext;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;
import net.wedjaa.ansible.vault.crypto.VaultHandler;

/**
 * Encrypts single values with Ansible Vault.
 */
public class AnsibleValueEncryptionPlugin implements ValueEncryptionPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-encryption";

  /**
   * Prefix that is prepended to the encrypted value.
   */
  public static final String ENCRYPTION_PREFIX = "!vault\n";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isEnabled() {
    return AnsibleVaultPassword.isPresent();
  }

  @Override
  public Object encrypt(String parameterName, Object value, ValueEncryptionContext context) {

    // get vault password
    String password = AnsibleVaultPassword.get();

    // convert value to string and encrypt it
    try {
      byte[] encryptedData = VaultHandler.encrypt(value.toString().getBytes(CHAR_ENCODING), password);
      return ENCRYPTION_PREFIX + new String(encryptedData, CHAR_ENCODING);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to encrypt sensitive configuration value for parameter " + parameterName, ex);
    }
  }

}
