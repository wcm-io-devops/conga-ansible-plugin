/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Andrea Scarpino <me@andreascarpino.it>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
//CHECKSTYLE:OFF
package it.andreascarpino.ansible.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.andreascarpino.ansible.inventory.type.AnsibleGroup;
import it.andreascarpino.ansible.inventory.type.AnsibleHost;
import it.andreascarpino.ansible.inventory.type.AnsibleInventory;
import it.andreascarpino.ansible.inventory.type.AnsibleVariable;
import it.andreascarpino.ansible.inventory.util.AnsibleInventoryReader;

/**
 * @author Andrea Scarpino
 */
class AnsibleInventoryReaderTest {

  @Test
  void testReadSimple() {
    String inventoryText = "[group1]\nhost1 var1=value1\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(1, inventory.getGroups().size());
    AnsibleGroup group = inventory.getGroups().iterator().next();
    assertEquals("group1", group.getName());
    assertEquals(1, group.getHosts().size());

    AnsibleHost host = group.getHosts().iterator().next();
    assertEquals("host1", host.getName());
    assertEquals(1, host.getVariables().size());

    AnsibleVariable variable = host.getVariables().iterator().next();
    assertEquals("var1", variable.getName());
    assertEquals("value1", variable.getValue());

    inventoryText = "[group1]\nhost1 var1=value1 var2=value2 var3=value3\nhost2\nhost3 var1=value1";

    inventory = AnsibleInventoryReader.read(inventoryText);
    group = inventory.getGroups().iterator().next();

    assertEquals(1, inventory.getGroups().size());
    assertEquals(3, group.getHosts().size());

    for (AnsibleHost h : group.getHosts()) {
      switch (h.getName()) {
      case "host1":
          assertEquals(3, h.getVariables().size());
        break;
      case "host2":
          assertEquals(0, h.getVariables().size());
        break;
      case "host3":
          assertEquals(1, h.getVariables().size());
        break;
      }
    }

    inventoryText = "host1 var1=value1";

    inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(0, inventory.getGroups().size());
    assertEquals(1, inventory.getHosts().size());
    assertEquals(1, inventory.getHosts().iterator().next().getVariables().size());
  }

  @Test
  void testReadNoGroup() {
    final String inventoryText = "host1 var1=value1\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(0, inventory.getGroups().size());
  }

  @Test
  void testReadSkipComments() {
    final String inventoryText = ";I'm a comment\nhost1 var1=value1\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(0, inventory.getGroups().size());
  }

  @Test
  void testReadNoHosts() {
    final String inventoryText = "[group1]\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(1, inventory.getGroups().size());
    assertEquals(0, inventory.getGroups().iterator().next().getHosts().size());
  }

  @Test
  void testReadSubgroup() {
    final String inventoryText = "[subgroup1]\nhost1\n[subgroup2]\nhost2\n[group1:children]\nsubgroup1\nsubgroup2\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(3, inventory.getGroups().size());

    for (AnsibleGroup group : inventory.getGroups()) {
      if (group.getName().equals("group1")) {
        assertEquals(2, group.getSubgroups().size());
      }
    }
  }

  @Test
  void testReadGroupVars() {
    final String inventoryText = "[subgroup1]\nhost1\n[subgroup2]\nhost2\n[group1:children]\nsubgroup1\nsubgroup2\n[group1:vars]\nvar1=value1\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(3, inventory.getGroups().size());

    for (AnsibleGroup group : inventory.getGroups()) {
      if (group.getName().equals("group1")) {
        assertEquals("var1", group.getSubgroups().iterator().next().getHosts().iterator().next()
            .getVariables().iterator().next().getName());
        assertEquals("value1", group.getSubgroups().iterator().next().getHosts().iterator().next()
            .getVariables().iterator().next().getValue());
      }
    }
  }

  @Test
  void testReadAnsibleExample() {
    final String inventoryText = "[atlanta]\nhost1\nhost2\n\n[raleigh]\nhost2\nhost3\n\n[southeast:children]\n"
        + "atlanta\nraleigh\n\n[southeast:vars]\nsome_server=foo.southeast.example.com\nhalon_system_timeout=30"
        + "\nself_destruct_countdown=60\nescape_pods=2\n\n[usa:children]\nsoutheast\nnortheast\nsouthwest\n"
        + "northwest\n";

    AnsibleInventory inventory = AnsibleInventoryReader.read(inventoryText);

    assertEquals(4, inventory.getGroups().size());

    for (AnsibleGroup group : inventory.getGroups()) {
      if (group.getName().equals("southeast")) {
        assertEquals(4,
            group.getSubgroups().iterator().next().getHosts().iterator().next().getVariables().size());
      }
    }
  }

}
