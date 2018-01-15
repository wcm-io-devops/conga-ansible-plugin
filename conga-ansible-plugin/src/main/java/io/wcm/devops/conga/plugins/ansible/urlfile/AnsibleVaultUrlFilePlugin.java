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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;
import net.wedjaa.ansible.vault.crypto.VaultHandler;

/**
 * Decrypts a file encrypted with Ansible Vault. Can be combined with other URL file plugins.
 */
public class AnsibleVaultUrlFilePlugin implements UrlFilePlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-vault";

  private static final String PREFIX = NAME + ":";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String url, UrlFilePluginContext context) {
    return StringUtils.startsWith(url, PREFIX);
  }

  @Override
  public String getFileName(String url, UrlFilePluginContext context) throws IOException {
    String innerUrl = getInnerUrl(url);
    return context.getUrlFileManager().getFileName(innerUrl);
  }

  @Override
  public InputStream getFile(String url, UrlFilePluginContext context) throws IOException {
    String innerUrl = getInnerUrl(url);
    try (InputStream is = context.getUrlFileManager().getFile(innerUrl)) {
      byte[] encryptedData = IOUtils.toByteArray(is);
      byte[] decryptedData = decryptFile(encryptedData);
      return new ByteArrayInputStream(decryptedData);
    }
    catch (IOException ex) {
      throw new IOException("Unable do decrypt file: " + innerUrl, ex);
    }
  }

  private byte[] decryptFile(byte[] encrpytedData) throws IOException {
    String password = AnsibleVaultPassword.get();
    return VaultHandler.decrypt(encrpytedData, password);
  }

  private String getInnerUrl(String url) {
    return StringUtils.substringAfter(url, PREFIX);
  }

}
