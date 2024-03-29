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
package net.wedjaa.ansible.vault.crypto.decoders;

import org.apache.commons.lang3.StringUtils;

import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES;
import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES256;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;

public final class CypherFactory {

  private CypherFactory() {
    // static methods only
  }

  public static CypherInterface getCypher(String cypherName) {
    if (StringUtils.equals(cypherName, CypherAES.CYPHER_ID)) {
      return new CypherAES();
    }

    if (StringUtils.equals(cypherName, CypherAES256.CYPHER_ID)) {
      return new CypherAES256();
    }

    return null;
  }

}
