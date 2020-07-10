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
package net.wedjaa.ansible.vault.crypto.data;

import static net.wedjaa.ansible.vault.crypto.VaultHandler.CHAR_ENCODING;

import java.util.Arrays;

public final class Util {

  private static final int DEFAULT_LINE_LENGTH = 80;

  final static char[] hexArray = "0123456789ABCDEF".toCharArray();
  final static String LINE_BREAK = "\n";

  private Util() {
    // static methods only
  }

  public static String join(String[] datalines) {
    return String.join("", Arrays.asList(datalines));
  }

  public static byte[] unhex(String hexed) {
    int dataLen = hexed.length();
    byte[] output = new byte[dataLen / 2];
    for (int charIdx = 0; charIdx < dataLen; charIdx += 2) {
      output[charIdx / 2] = (byte)((Character.digit(hexed.charAt(charIdx), 16) << 4)
          + Character.digit(hexed.charAt(charIdx + 1), 16));
    }
    return output;
  }

  public static String hexit(byte[] unhexed) {
    return hexit(unhexed, DEFAULT_LINE_LENGTH);
  }

  public static String hexit(byte[] unhexed, int lineLength) {
    StringBuilder result = new StringBuilder();
    int colIdx = 0;
    for (byte val : unhexed) {
      result.append(String.format("%02x", val));
      colIdx++;
      if (lineLength > 0 && colIdx >= lineLength / 2) {
        result.append(LINE_BREAK);
        colIdx = 0;
      }
    }

    return result.toString();
  }

  public static VaultInfo getVaultInfo(String vaultData) {
    String infoString = vaultData.substring(0, vaultData.indexOf(LINE_BREAK));
    return new VaultInfo(infoString);
  }

  public static VaultInfo getVaultInfo(byte[] vaultData) {
    return getVaultInfo(new String(vaultData, CHAR_ENCODING));
  }

  public static String cleanupData(String vaultData) {
    return vaultData.substring(vaultData.indexOf(LINE_BREAK) + 1);
  }

  public static byte[] getVaultData(String vaultData) {
    String rawData = join(cleanupData(vaultData).split(LINE_BREAK));
    return unhex(rawData);
  }

  public static byte[] getVaultData(byte[] vaultData) {
    String rawData = join(cleanupData(new String(vaultData, CHAR_ENCODING)).split(LINE_BREAK));
    return unhex(rawData);
  }

}
