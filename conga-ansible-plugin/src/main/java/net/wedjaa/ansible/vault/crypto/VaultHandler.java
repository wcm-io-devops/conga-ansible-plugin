/*
 * Copyright 2016 - Fabio "MrWHO" Torchetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//CHECKSTYLE:OFF
package net.wedjaa.ansible.vault.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import net.wedjaa.ansible.vault.crypto.data.Util;
import net.wedjaa.ansible.vault.crypto.data.VaultInfo;
import net.wedjaa.ansible.vault.crypto.decoders.CypherFactory;
import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES256;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;

/**
 * Created by mrwho on 03/06/15.
 */
public class VaultHandler {

  public final static String DEFAULT_CYPHER = CypherAES256.CYPHER_ID;

  /**
   * Character encoding
   */
  public final static Charset CHAR_ENCODING = StandardCharsets.UTF_8;

  public static byte[] encrypt(byte[] cleartext, String password, String cypher) throws IOException {
    CypherInterface cypherInstance = CypherFactory.getCypher(cypher);
    byte[] vaultData = cypherInstance.encrypt(cleartext, password);
    String vaultDataString = new String(vaultData, CHAR_ENCODING);
    String vaultPackage = cypherInstance.infoLine() + "\n" + vaultDataString;
    return vaultPackage.getBytes(CHAR_ENCODING);
  }

  public static byte[] encrypt(byte[] cleartext, String password) throws IOException {
    return encrypt(cleartext, password, DEFAULT_CYPHER);
  }

  public static void encrypt(InputStream clearText, OutputStream cipherText, String password, String cypher) throws IOException {
    String clearTextValue = IOUtils.toString(clearText, CHAR_ENCODING);
    cipherText.write(encrypt(clearTextValue.getBytes(CHAR_ENCODING), password, cypher));
  }

  public static void encrypt(InputStream clearText, OutputStream cipherText, String password) throws IOException {
    encrypt(clearText, cipherText, password, DEFAULT_CYPHER);
  }

  public static void decrypt(InputStream encryptedVault, OutputStream decryptedVault, String password) throws IOException {
    String encryptedValue = IOUtils.toString(encryptedVault, CHAR_ENCODING);
    decryptedVault.write(decrypt(encryptedValue.getBytes(CHAR_ENCODING), password));
  }

  public static byte[] decrypt(byte[] encrypted, String password) throws IOException {
    byte[] encryptedProcessed = stripCarriageReturns(encrypted);

    VaultInfo vaultInfo = Util.getVaultInfo(encryptedProcessed);
    if (!vaultInfo.isEncryptedVault()) {
      throw new IOException("File is not an Ansible Encrypted Vault");
    }

    if (!vaultInfo.isValidVault()) {
      throw new IOException("The vault is not a format we can handle - check the cypher.");
    }

    byte[] encryptedData = Util.getVaultData(encryptedProcessed);

    return vaultInfo.getCypher().decrypt(encryptedData, password);
  }

  /**
   * Strip \r characters from encrypted data that may slip in on windows file systems.
   * @param data Data
   * @return Data without \r chars
   */
  private static byte[] stripCarriageReturns(byte[] data) {
    return ArrayUtils.removeAllOccurences(data, (byte)0xD);
  }

}
