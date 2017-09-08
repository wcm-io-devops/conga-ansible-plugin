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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<String, List<String>> content = getInventoryContent(context);
    return content.get(variableName);
  }

  private Map<String, List<String>> getInventoryContent(ValueProviderContext context) {

    // try to get from cache
    Map<String, List<String>> content = context.getValueProviderCache(NAME);
    if (content != null) {
      return content;
    }

    // read from inventory file
    Map<String, Object> valueProviderConfig = context.getValueProviderConfig(NAME);
    String filePath = (String)valueProviderConfig.get(PARAM_FILE);
    String groupName = (String)valueProviderConfig.get(PARAM_GROUP);

    if (StringUtils.isAnyBlank(filePath, groupName)) {
      throw new GeneratorException("Config parameters '" + PARAM_FILE + "' or '" + PARAM_GROUP + "' missing for value provider '" + NAME + "'.");
    }

    File file = new File(filePath);
    if (!(file.exists() && file.isFile())) {
      throw new GeneratorException("Ansible Inventory file does not exist: " + FileUtil.getCanonicalPath(file));
    }

    try {
      String inventoryContent = FileUtils.readFileToString(file, CharEncoding.UTF_8);
      AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryContent);
      content = inventoryToConfig(inventory, groupName);
      if (content == null) {
        throw new GeneratorException("No group '" + groupName + "' in Ansible Inventory file: " + FileUtil.getCanonicalPath(file));
      }

      // put to cache
      context.setValueProviderCache(NAME, content);
      return content;
    }
    catch (IOException ex) {
      throw new GeneratorException("Error reading Ansible Inventory file: " + FileUtil.getCanonicalPath(file));
    }
  }

  private Map<String, List<String>> inventoryToConfig(AnsibleInventory inventory, String groupName) {
    AnsibleGroup group = inventory.getGroup(groupName);
    if (group == null) {
      return null;
    }
    Map<String, List<String>> config = new HashMap<>();
    for (AnsibleHost host : group.getHosts()) {
      String hostName = host.getName();
      AnsibleVariable congaNodeVariable = host.getVariable(INVENTORY_VARIABLE_CONGA_NODE);
      if (congaNodeVariable != null) {
        String congaNode = (String)congaNodeVariable.getValue();
        if (StringUtils.isNoneBlank(hostName, congaNode)) {
          List<String> hostNames = config.get(congaNode);
          if (hostNames == null) {
            hostNames = new ArrayList<>();
          }
          if (!hostNames.contains(hostName)) {
            hostNames.add(hostName);
          }
          config.put(congaNode, hostNames);
        }
      }
    }
    return config;
  }

}
