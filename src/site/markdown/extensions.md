## CONGA - Extensions

The CONGA Ansible Plugin extends CONGA using its [extensibility model][conga-extensibility].


### Provided Plugins

| Plugin name         | Description
|---------------------|-------------
| `ansible-inventory` | Allows to read [Ansible Inventory][ansible-inventory] files.


### Reading Ansible Inventory files

Ansible Inventory files are expected in a special format as used by the Ansible roles for AEM deployment with CONGA.

Example inventory:

```
[test-group]
host-01 conga_node=aem-author
host-02 conga_node=aem-publish
host-03 conga_node=aem-publish
```

Using the value provider plugin for Ansible Inventory you can reference the list of host names for each `conga_node` value.

Example:

* `${ansible-inventory::aem-author}` = host-01
* `${ansible-inventory::aem-publish}` = host-02,host-03

Via configuration you need to tell the [CONGA Maven Plugin][conga-maven-plugin] (via the pom.xml or via system environment property) where to look up the Ansible Inventory file. This has to be a static inventory file (Ansible supports dynamic inventories, in this case you have to write it's output to a static file and pointing CONGA to this one).

Example:

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <configuration>
    <valueProvider>
      ansible-inventory;file=${project.basedir}/inventory/inventory-example;group=test-group
    </valueProvider>
  </configuration>
</plugin>
```



[conga-extensibility]: http://devops.wcm.io/conga/extensibility.html
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[conga-maven-plugin]: http://devops.wcm.io/conga/tooling/conga-maven-plugin/plugin-info.html
