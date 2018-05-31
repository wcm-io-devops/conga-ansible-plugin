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

import org.yaml.snakeyaml.TypeDescription;

import io.wcm.devops.conga.generator.spi.yaml.YamlConstructorPlugin;
import io.wcm.devops.conga.generator.spi.yaml.context.YamlConstructorContext;

/**
 * Register YAML constructor modification for accepting a !vault tag in YAML files.
 */
public class AnsibleYamlConstructorPlugin implements YamlConstructorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-vault";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void register(YamlConstructorContext context) {
    context.getYamlConstructor().addTypeDescription(new TypeDescription(YamlVaultValue.class, YamlVaultValueRepresent.VAULT_TAG));
  }

}
