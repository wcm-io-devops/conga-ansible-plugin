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
package io.wcm.devops.conga.plugins.ansible.valueprovider;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;
import io.wcm.devops.conga.plugins.ansible.util.FileScriptLoader;
import net.wedjaa.ansible.vault.Manager;

/**
 * Gets variables from Ansible Vault-encrypted YAML files.
 */
public class AnsibleVaultValueProviderPlugin implements ValueProviderPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-vault";

  /**
   * Parameter: Path to inventory file
   */
  public static final String PARAM_FILE = "file";

  /**
   * Parameter: Vault password
   */
  public static final String PARAM_PASSWORD = "password";

  /**
   * Parameter: Vault password file.
   */
  public static final String PARAM_PASSWORD_FILE = "passwordFile";

  /**
   * System environment variable pointing to a file containing the vault password.
   */
  public static final String ENVIRONMENT_VARIABLE_PASSWORD_FILE = "ANSIBLE_VAULT_PASSWORD_FILE";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object resolve(String variableName, ValueProviderContext context) {
    Map<String, Object> vars = getVaultContent(context);
    return vars.get(variableName);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getVaultContent(ValueProviderContext context) {

    // try to get from cache
    Map<String, Object> content = (Map<String, Object>)context.getValueProviderCache();
    if (content != null) {
      return content;
    }

    // get vault password
    String password = AnsibleVaultPassword.get();

    // read from inventory file
    String filePath = (String)context.getValueProviderConfig(PARAM_FILE);

    if (StringUtils.isBlank(filePath)) {
      throw new GeneratorException("Config parameters '" + PARAM_FILE + "' missing for value provider '" + NAME + "'.");
    }

    File file = new File(filePath);
    if (!(file.exists() && file.isFile())) {
      throw new GeneratorException("Ansible Vault file does not exist: " + FileUtil.getCanonicalPath(file));
    }

    try {
      String encryptedContent = FileScriptLoader.readFileToString(file);
      content = (Map<String, Object>)new Manager().getFromVault(Map.class, encryptedContent, password);

      // put to cache
      context.setValueProviderCache(content);
      return content;
    }
    catch (IOException ex) {
      throw new GeneratorException("Error reading Ansible Vault file: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

}
