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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

@ExtendWith(MockitoExtension.class)
public class AnsibleInventoryValueProviderPluginTest {

  @Mock
  private Logger logger;

  private PluginContextOptions pluginContextOptions;
  private ValueProviderGlobalContext globalContext;
  private ValueProviderContext context;
  private ValueProviderPlugin underTest;

  @BeforeEach
  public void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .logger(logger);
    globalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    context = new ValueProviderContext()
        .valueProviderGlobalContext(globalContext)
        .valueProviderName(AnsibleInventoryValueProviderPlugin.NAME);
    underTest = pluginManager.get(AnsibleInventoryValueProviderPlugin.NAME, ValueProviderPlugin.class);
  }

  @Test
  public void testNoConfig() {
    assertThrows(GeneratorException.class, () -> {
      underTest.resolve("var1", context);
    });
  }

  @Test
  public void testInvalidFile() {
    pluginContextOptions.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/nonexisting-file")));
    assertThrows(GeneratorException.class, () -> {
      underTest.resolve("var1", context);
    });
  }

  @Test
  public void testInventoryIniStyle() {
    pluginContextOptions.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/inventory-sample/inventory-ini-style")));

    assertEquals(ImmutableList.of("host-01", "host-02", "host-03"), underTest.resolve("test-group", context));
    assertEquals(ImmutableList.of("host-01"), underTest.resolve("aem-author", context));
    assertEquals(ImmutableList.of("host-02", "host-03"), underTest.resolve("aem-publish", context));
  }

  @Test
  public void testInventoryIniStyle_PyhtonScript() {
    pluginContextOptions.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/inventory-sample/inventory-ini-style.py")));

    assertEquals(ImmutableList.of("host-01", "host-02", "host-03"), underTest.resolve("test-group", context));
    assertEquals(ImmutableList.of("host-01"), underTest.resolve("aem-author", context));
    assertEquals(ImmutableList.of("host-02", "host-03"), underTest.resolve("aem-publish", context));
  }

  @Test
  public void testInventoryJsonStyle() {
    pluginContextOptions.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/inventory-sample/inventory-json-style.json")));

    assertEquals(ImmutableList.of("host-01", "host-02", "host-03"), underTest.resolve("test-group", context));
    assertEquals(ImmutableList.of("host-01"), underTest.resolve("aem-author", context));
    assertEquals(ImmutableList.of("host-02", "host-03"), underTest.resolve("aem-publish", context));
  }

  @Test
  public void testInventoryJsonStyleMeta() {
    pluginContextOptions.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/inventory-sample/inventory-json-style-meta.json")));

    assertEquals(ImmutableList.of("192.168.100.1"), underTest.resolve("tag_Name_dev_aem", context));
    // use jsonpath query
    assertEquals(ImmutableList.of("127.0.100.1"), underTest.resolve("$._meta.hostvars..[?(@.ec2_tag_Name=='dev_aem')].ec2_private_ip_address", context));
  }

}
