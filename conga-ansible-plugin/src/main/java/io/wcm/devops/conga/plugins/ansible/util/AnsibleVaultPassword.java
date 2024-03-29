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

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Helper methods for Ansible Vault.
 */
public final class AnsibleVaultPassword {

  private AnsibleVaultPassword() {
    // static methods only
  }

  /**
   * System property with ansible vault password.
   */
  public static final String SYSTEM_PROPERTY_PASSWORD = "ansible.vault.password";

  /**
   * System property with path to ansible vault password file.
   */
  public static final String SYSTEM_PROPERTY_PASSWORD_FILE = "ansible.vault.password.file";

  /**
   * System environment variable containing the vault password.
   */
  public static final String ENVIRONMENT_VARIABLE_PASSWORD = "ANSIBLE_VAULT_PASSWORD";

  /**
   * System environment variable pointing to a file containing the vault password.
   */
  public static final String ENVIRONMENT_VARIABLE_PASSWORD_FILE = "ANSIBLE_VAULT_PASSWORD_FILE";

  /**
   * Get Ansible Vault password.
   * @return Ansible Vault password.
   * @throws io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPasswordMissing if no password is set
   */
  public static String get() {
    String password = getInternal();

    if (StringUtils.isBlank(password)) {
      throw new AnsibleVaultPasswordMissing("No Ansible Vault password set. Either specify " + SYSTEM_PROPERTY_PASSWORD + " "
          + "or " + SYSTEM_PROPERTY_PASSWORD_FILE + " system parameter, "
          + "or set the " + ENVIRONMENT_VARIABLE_PASSWORD + " or " + ENVIRONMENT_VARIABLE_PASSWORD_FILE + " system environment variable.");
    }

    return password;
  }

  /**
   * Checks if an Ansible Vault password is set.
   * @return true if password is present
   */
  public static boolean isPresent() {
    String password = getInternal();
    return StringUtils.isNotBlank(password);
  }

  /**
   * Get Ansible Vault password.
   * @return Ansible Vault password or null if it is not set
   */
  private static String getInternal() {
    String password = System.getProperty(SYSTEM_PROPERTY_PASSWORD);

    // fallback to environment variable
    if (StringUtils.isBlank(password)) {
      password = System.getenv(ENVIRONMENT_VARIABLE_PASSWORD);
    }

    // if not password given try to read from password file
    if (StringUtils.isBlank(password)) {
      password = getFromFile();
    }

    return password;
  }

  private static String getFromFile() {
    String passwordFilePath = System.getProperty(SYSTEM_PROPERTY_PASSWORD_FILE);

    if (StringUtils.isBlank(passwordFilePath)) {
      // if not password file given, try to read file path from the usual ansible environment variable
      passwordFilePath = System.getenv(ENVIRONMENT_VARIABLE_PASSWORD_FILE);
    }

    if (StringUtils.isBlank(passwordFilePath)) {
      return null;
    }

    File passwordFile = new File(passwordFilePath);
    if (!(passwordFile.exists() && passwordFile.isFile())) {
      throw new AnsibleVaultPasswordMissing("Ansible Vault password file does not exist: " + FileUtil.getCanonicalPath(passwordFile));
    }
    try {
      return FileScriptLoader.readFileToString(passwordFile).trim();
    }
    catch (IOException ex) {
      throw new AnsibleVaultPasswordMissing("Error reading Ansible Vault password file: " + FileUtil.getCanonicalPath(passwordFile), ex);
    }
  }

}
