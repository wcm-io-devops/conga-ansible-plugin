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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class AnsibleInventoryValueProviderPluginTest {

  @Mock
  private Logger logger;

  private ValueProviderContext context;
  private ValueProviderPlugin underTest;

  @Before
  public void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    context = new ValueProviderContext()
        .pluginManager(pluginManager)
        .logger(logger);
    underTest = pluginManager.get(AnsibleInventoryValueProviderPlugin.NAME, ValueProviderPlugin.class);
  }

  @Test(expected = GeneratorException.class)
  public void testNoConfig() {
    underTest.resolve("var1", context);
  }

  @Test(expected = GeneratorException.class)
  public void testInvalidFile() {
    context.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/nonexisting-file")));
    underTest.resolve("var1", context);
  }

  @Test
  public void testFile() {
    context.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleInventoryValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleInventoryValueProviderPlugin.PARAM_FILE, "src/test/resources/inventory-sample/inventory-ini-style")));

    assertEquals(ImmutableList.of("host-01", "host-02", "host-03"), underTest.resolve("test-group", context));
    assertEquals(ImmutableList.of("host-01"), underTest.resolve("aem-author", context));
    assertEquals(ImmutableList.of("host-02", "host-03"), underTest.resolve("aem-publish", context));
  }

}
