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
package io.wcm.devops.conga.plugins.ansible.valueprovider;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;

@RunWith(MockitoJUnitRunner.class)
public class AnsibleVaultValueProviderPluginTest {

  @Mock
  private Logger logger;

  private ValueProviderGlobalContext globalContext;
  private ValueProviderContext context;
  private ValueProviderPlugin underTest;

  @Before
  public void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    globalContext = new ValueProviderGlobalContext()
        .pluginManager(pluginManager)
        .logger(logger);
    context = new ValueProviderContext()
        .valueProviderGlobalContext(globalContext)
        .valueProviderName(AnsibleVaultValueProviderPlugin.NAME);
    underTest = pluginManager.get(AnsibleVaultValueProviderPlugin.NAME, ValueProviderPlugin.class);

    System.setProperty(AnsibleVaultPassword.SYSTEM_PROPERTY_PASSWORD_FILE, "src/test/resources/vault-sample/passwordFile");
  }

  @Test(expected = GeneratorException.class)
  public void testNoConfig() {
    underTest.resolve("var1", context);
  }

  @Test(expected = GeneratorException.class)
  public void testInvalidFile() {
    globalContext.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleVaultValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleVaultValueProviderPlugin.PARAM_FILE, "src/test/resources/nonexisting-file")));
    underTest.resolve("var1", context);
  }

  @Test
  public void testWithPassword() {
    globalContext.valueProviderConfig(ImmutableMap.<String, Map<String, Object>>of(AnsibleVaultValueProviderPlugin.NAME,
        ImmutableMap.<String, Object>of(AnsibleVaultValueProviderPlugin.PARAM_FILE, "src/test/resources/vault-sample/test-encrypted.yml")));

    assertEquals("abc", underTest.resolve("pwd1", context));
    assertEquals(ImmutableMap.of("pwd2", "def", "pwd3", "ghi"), underTest.resolve("group1", context));
  }

}
