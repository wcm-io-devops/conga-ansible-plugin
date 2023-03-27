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
package net.wedjaa.ansible.vault;

import static net.wedjaa.ansible.vault.crypto.VaultHandler.CHAR_ENCODING;

import java.io.IOException;
import java.io.StringWriter;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TrustedTagInspector;

import net.wedjaa.ansible.vault.crypto.VaultHandler;

public class Manager {

  public Object getFromYaml(Class<?> objectClass, String yaml) {
    LoaderOptions options = new LoaderOptions();
    options.setTagInspector(new TrustedTagInspector());
    Yaml reader = new Yaml(new Constructor(objectClass, options));
    return reader.load(yaml);
  }

  public String writeToYaml(Object object) {
    try (StringWriter resultWriter = new StringWriter()) {
      Yaml writer = new Yaml();
      writer.dump(object, resultWriter);
      return resultWriter.getBuffer().toString();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Object getFromVault(Class<?> objectClass, String yaml, String password) throws IOException {
    byte[] clearYaml = VaultHandler.decrypt(yaml.getBytes(CHAR_ENCODING), password);
    return getFromYaml(objectClass, new String(clearYaml, CHAR_ENCODING));
  }

  public String writeToVault(Object object, String password) throws IOException {
    String clearYaml = writeToYaml(object);
    byte[] yamlVault = VaultHandler.encrypt(clearYaml.getBytes(CHAR_ENCODING), password);
    return new String(yamlVault, CHAR_ENCODING);
  }

}
