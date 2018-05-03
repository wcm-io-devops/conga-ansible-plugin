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

import io.wcm.devops.conga.generator.spi.yaml.YamlRepresentPlugin;
import io.wcm.devops.conga.generator.spi.yaml.context.YamlRepresentContext;

/**
 * Register YAML represent for rendering a !vault encrypted value.
 */
public class AnsibleYamlRepresentPlugin implements YamlRepresentPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-yaml-represent";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void register(YamlRepresentContext context) {
    context.getYamlRepresenter().registerRepresent(YamlVaultValue.class, new YamlVaultValueRepresent());
  }

}
