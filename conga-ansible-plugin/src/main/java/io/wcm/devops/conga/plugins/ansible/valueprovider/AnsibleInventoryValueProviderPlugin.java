/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import it.andreascarpino.ansible.inventory.type.AnsibleGroup;
import it.andreascarpino.ansible.inventory.type.AnsibleHost;
import it.andreascarpino.ansible.inventory.type.AnsibleInventory;
import it.andreascarpino.ansible.inventory.type.AnsibleVariable;
import it.andreascarpino.ansible.inventory.util.AnsibleInventoryReader;

/**
 * Gets host names from ansible inventory.
 */
public class AnsibleInventoryValueProviderPlugin implements ValueProviderPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "ansible-inventory";

  /**
   * Parameter: Path to inventory file
   */
  public static final String PARAM_FILE = "file";

  /**
   * Parameter: Primary inventory group
   */
  public static final String PARAM_GROUP = "group";

  private static final String INVENTORY_VARIABLE_CONGA_NODE = "conga_node";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object resolve(String variableName, ValueProviderContext context) {
    Map<String, Object> valueProviderConfig = context.getValueProviderConfig(NAME);
    String filePath = (String)valueProviderConfig.get(PARAM_FILE);
    String groupName = (String)valueProviderConfig.get(PARAM_GROUP);

    if (StringUtils.isBlank(filePath) || StringUtils.isBlank(groupName)) {
      context.getLogger().warn("No file or group parameter set for value provider '" + NAME + "'.");
      return null;
    }

    File file = new File(filePath);
    if (!(file.exists() && file.isFile())) {
      throw new GeneratorException("Ansible Inventory file does not exist: " + FileUtil.getCanonicalPath(file));
    }

    try {
      String inventoryContent = FileUtils.readFileToString(file, CharEncoding.UTF_8);
      AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryContent);
      Map<String, Set<String>> config = inventoryToConfig(inventory, groupName);
      if (config == null) {
        throw new GeneratorException("No group '" + groupName + "' in Ansible Inventory file: " + FileUtil.getCanonicalPath(file));
      }
      return config.get(variableName);
    }
    catch (IOException ex) {
      throw new GeneratorException("Error reading Ansible Inventory file: " + FileUtil.getCanonicalPath(file));
    }
  }

  private Map<String, Set<String>> inventoryToConfig(AnsibleInventory inventory, String groupName) {
    AnsibleGroup group = inventory.getGroup(groupName);
    if (group == null) {
      return null;
    }
    Map<String, Set<String>> config = new HashMap<>();
    for (AnsibleHost host : group.getHosts()) {
      String hostName = host.getName();
      AnsibleVariable congaNodeVariable = host.getVariable(INVENTORY_VARIABLE_CONGA_NODE);
      if (congaNodeVariable != null) {
        String congaNode = (String)congaNodeVariable.getValue();
        if (StringUtils.isNoneBlank(hostName, congaNode)) {
          Set<String> hostNames = config.get(congaNode);
          if (hostNames == null) {
            hostNames = new TreeSet<>();
          }
          hostNames.add(hostName);
          config.put(congaNode, hostNames);
        }
      }
    }
    return config;
  }

}
