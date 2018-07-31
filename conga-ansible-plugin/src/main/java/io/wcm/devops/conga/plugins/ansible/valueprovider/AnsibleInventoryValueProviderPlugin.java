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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.plugins.ansible.util.FileScriptLoader;
import it.andreascarpino.ansible.inventory.type.AnsibleGroup;
import it.andreascarpino.ansible.inventory.type.AnsibleInventory;
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

  private final JsonParser jsonParser = new JsonParser();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object resolve(String variableName, ValueProviderContext context) {
    InventoryContent content = getInventoryContent(context);
    return resolveVariableOrJsonPath(content, variableName);
  }

  private Object resolveVariableOrJsonPath(InventoryContent content, String variableName) {
    if (StringUtils.startsWith(variableName, "$")) {
      if (content.getJsonpathReadContext() != null) {
        return content.getJsonpathReadContext().read(variableName, List.class);
      }
      else {
        return null;
      }
    }
    return content.getMap().get(variableName);
  }

  private InventoryContent getInventoryContent(ValueProviderContext context) {

    // try to get from cache
    InventoryContent content = (InventoryContent)context.getValueProviderCache();
    if (content != null) {
      return content;
    }

    // read from inventory file
    String filePath = (String)context.getValueProviderConfig(PARAM_FILE);

    if (StringUtils.isBlank(filePath)) {
      throw new GeneratorException("Config parameters '" + PARAM_FILE + "' missing for value provider '" + NAME + "'.");
    }

    File file = new File(filePath);
    if (!(file.exists() && file.isFile())) {
      throw new GeneratorException("Ansible Inventory file does not exist: " + FileUtil.getCanonicalPath(file));
    }

    try {
      String inventoryContent = FileScriptLoader.readFileToString(file);

      // try to read as JSON
      content = tryReadJsonStyle(inventoryContent, file, context);

      // try to read INI style
      if (content == null) {
        content = tryReadInitStyle(inventoryContent);
      }

      // fallback to empty map
      if (content == null) {
        content = new InventoryContent(ImmutableMap.of());
      }

      // put to cache
      context.setValueProviderCache(content);
      return content;
    }
    catch (IOException ex) {
      throw new GeneratorException("Error reading Ansible Inventory file: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

  private InventoryContent tryReadJsonStyle(String inventoryContent, File file, ValueProviderContext context) {
    try {
      JsonElement root = jsonParser.parse(inventoryContent);
      if (root instanceof JsonObject) {
        ReadContext jsonpathReadContext = JsonPath.parse(inventoryContent);
        return new InventoryContent(jsonToConfig((JsonObject)root), jsonpathReadContext);
      }
    }
    catch (JsonSyntaxException ex) {
      context.getLogger().debug("Failed to parse Ansible inventory " + FileUtil.getCanonicalPath(file) + " as JSON string "
          + "- assuming not JSON style.", ex);
    }
    return null;
  }

  private Map<String, List<String>> jsonToConfig(JsonObject root) {
    Map<String, List<String>> content = new HashMap<>();
    for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
      if (!StringUtils.equals(entry.getKey(), "_meta")) {
        JsonArray hostNamesArray = null;
        if (entry.getValue() instanceof JsonArray) {
          hostNamesArray = (JsonArray)entry.getValue();
        }
        else if (entry.getValue() instanceof JsonObject) {
          JsonObject entryObject = (JsonObject)entry.getValue();
          JsonElement hostsElement = entryObject.get("hosts");
          if (hostsElement instanceof JsonArray) {
            hostNamesArray = (JsonArray)hostsElement;
          }
        }

        if (hostNamesArray != null) {
          List<String> hostNames = new ArrayList<>();
          for (JsonElement item : hostNamesArray) {
            hostNames.add(item.getAsString());
          }
          content.put(entry.getKey(), hostNames);
        }
      }
    }
    return content;
  }

  private InventoryContent tryReadInitStyle(String inventoryContent) {
    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryContent);
    return new InventoryContent(inventoryToConfig(inventory));
  }

  /**
   * Builds a map with group name as key, and a list of associated host names as value.
   * @param inventory Ansible inventory
   * @return Group/hostname map
   */
  private Map<String, List<String>> inventoryToConfig(AnsibleInventory inventory) {
    Map<String, List<String>> config = new HashMap<>();
    for (AnsibleGroup group : inventory.getGroups()) {
      config.put(group.getName(), group.getHosts().stream()
          .map(host -> host.getName())
          .collect(Collectors.toList()));
    }
    return config;
  }

  private static class InventoryContent {

    private final Map<String, List<String>> map;
    private final ReadContext jsonpathReadContext;

    InventoryContent(Map<String, List<String>> content) {
      this(content, null);
    }

    InventoryContent(Map<String, List<String>> content, ReadContext jsonpathReadContext) {
      this.map = content;
      this.jsonpathReadContext = jsonpathReadContext;
    }

    public Map<String, List<String>> getMap() {
      return this.map;
    }

    public ReadContext getJsonpathReadContext() {
      return this.jsonpathReadContext;
    }

  }

}
