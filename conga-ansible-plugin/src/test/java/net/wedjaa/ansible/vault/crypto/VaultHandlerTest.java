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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S5783")
class VaultHandlerTest {

  private final static String TEST_STRING = "This is a test";
  private final static String TEST_PASSWORD = "password";
  private final static String TEST_WRONG_PASSWORD = "not_this_one";
  private final static String WRONG_PASS_EX = "HMAC Digest doesn't match - possibly it's the wrong password.";
  private final static String DECODED_VAULT = "!net.wedjaa.ansible.vault.ProvisioningInfo\n"
      + "apiClientId: The provisioner ClientId\n"
      + "apiPassword: The secret password\n"
      + "apiUser: Secret User\n";

  private final static Logger logger = LoggerFactory.getLogger(VaultHandlerTest.class);

  @Test
  void testByteArrayValidVault() throws Exception {
    logger.info("Testing Byte Array decryption - Valid Password");

    byte[] encryptedTest = VaultHandler.encrypt(TEST_STRING.getBytes(), TEST_PASSWORD);
    logger.debug("Encrypted vault:\n{}", new String(encryptedTest));
    byte[] decryptedTest = VaultHandler.decrypt(encryptedTest, TEST_PASSWORD);
    logger.debug("Decrypted vault:\n{}", new String(decryptedTest));
    assertEquals(TEST_STRING, new String(decryptedTest));
  }

  @Test
  void testByteArrayValidVault_CarriageReturns() throws Exception {
    logger.info("Testing Byte Array decryption - Valid Password");

    byte[] encryptedTest = VaultHandler.encrypt(TEST_STRING.getBytes(), TEST_PASSWORD);
    logger.debug("Encrypted vault:\n{}", new String(encryptedTest));

    // replace \n with \r\n to simulate new lines on windows file systems
    String encryptedString = new String(encryptedTest, StandardCharsets.UTF_8);
    encryptedString = StringUtils.replace(encryptedString, "\n", "\r\n");
    encryptedTest = encryptedString.getBytes(StandardCharsets.UTF_8);

    byte[] decryptedTest = VaultHandler.decrypt(encryptedTest, TEST_PASSWORD);
    logger.debug("Decrypted vault:\n{}", new String(decryptedTest));
    assertEquals(TEST_STRING, new String(decryptedTest));
  }

  @Test
  void testByteArrayInvalidVault() {
    logger.info("Testing Byte Array decryption - Invalid Password");
    try {
      byte[] encryptedTest = VaultHandler.encrypt(TEST_STRING.getBytes(), TEST_PASSWORD);
      logger.debug("Encrypted vault:\n{}", new String(encryptedTest));
      byte[] decryptedTest = VaultHandler.decrypt(encryptedTest, TEST_WRONG_PASSWORD);
      logger.debug("Decrypted vault:\n{}", new String(decryptedTest));
      fail("Should not be able to decrypt text with the wrong password");
    }
    catch (Exception ex) {
      assertEquals(WRONG_PASS_EX, ex.getMessage());
    }
  }

  @Test
  void testStreamValidVault() throws Exception {
    logger.info("Testing decoding vault Stream - Valid password ");

    ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
    InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("vault-sample-wedjaa/test-vault.yml");
    VaultHandler.decrypt(encodedStream, decodedStream, TEST_PASSWORD);
    String decoded = new String(decodedStream.toByteArray());
    assertEquals(DECODED_VAULT, decoded);
  }

  @Test
  void testStreamInvalidVault() {
    logger.info("Testing decoding vault Stream - Invalid password ");
    try {
      ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
      InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("vault-sample-wedjaa/test-vault.yml");
      VaultHandler.decrypt(encodedStream, decodedStream, TEST_WRONG_PASSWORD);
      new String(decodedStream.toByteArray());
      fail("Should not be able to decrypt text with the wrong password");
    }
    catch (Exception ex) {
      assertEquals(WRONG_PASS_EX, ex.getMessage());
    }
  }

}
