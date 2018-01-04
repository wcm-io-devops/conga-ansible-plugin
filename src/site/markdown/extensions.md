## CONGA - Extensions

The CONGA Ansible Plugin extends CONGA using its [extensibility model][conga-extensibility].


### Provided Plugins

| Plugin name         | Description
|---------------------|-------------
| `ansible-inventory` | Allows to read [Ansible Inventory][ansible-inventory] files.
| `ansible-vault`     | Allows to variables from encrypted [Ansible Vault][ansible-vault] files.


The plugin implementations only support static files. Ansible supports reading dynamic files (e.g. Python scripts) for inventories, password files etc. - but this CONGA plugin implementation only supports files that contain the result without executing them.

You can also define multiple value providers of the same plugin type. See [value provider configuration][conga-maven-plugin-valueprovider] for details.


### Reading Ansible Inventory files

Ansible Inventory files are expected in a special format as used by the Ansible roles for AEM deployment with CONGA.

Example inventory:

```
[test-group]
host-01 conga_node=aem-author
host-02 conga_node=aem-publish
host-03 conga_node=aem-publish
```

Using the value provider plugin for Ansible Inventory you can reference the list of host names for each group.

Example:

* `${ansible-inventory::aem-author}` = host-01
* `${ansible-inventory::aem-publish}` = host-02,host-03

Via configuration you need to tell the [CONGA Maven Plugin][conga-maven-plugin] (via the pom.xml or via system environment property) where to look up the Ansible Inventory file.

Example:

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <configuration>
    <valueProvider>
      ansible-inventory;file=${project.basedir}/inventory/inventory-example
    </valueProvider>
  </configuration>
</plugin>
```

### Reading variables from Ansible Vault files

_**Please note:** You need to install the [Java Cryptography Extension (JCE) Unlimited Strength policy files][jce-policy] from Oracle, because Ansible uses 256 bit keys to handle encryption and decryption of the vault files._

Ansible Vault files are YAML files encrypted with a password using [Ansible Vault][ansible-vault].

Example YAML file *before encryption*:

```yaml
pwd1: abc
```

Using the value provider plugin for Ansible Vault you can the variables defined in the encrypted file.

Example:

* `${ansible-vault::pwd1}` = abc

Via configuration you need to tell the [CONGA Maven Plugin][conga-maven-plugin] (via the pom.xml or via system environment property) where to look up the file containing the encrypted variables, and the password to decrypt the data.

Example:

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <configuration>
    <valueProvider>
      ansible-vault;file=${project.basedir}/vault/myvars;password=mypassword
    </valueProvider>
  </configuration>
</plugin>
```

You should not include the vault password in plain text as shown in this example, but read it from a maven property e.g. set via command line. Alternatively you can just omit the password parameter, then the plugin looks for a password file references via a `ANSIBLE_VAULT_PASSWORD_FILE` system environment variable. This is the variable is used by Ansible itself as well.


[conga-extensibility]: http://devops.wcm.io/conga/extensibility.html
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-vault]: https://docs.ansible.com/ansible/latest/vault.html
[conga-maven-plugin]: http://devops.wcm.io/conga/tooling/conga-maven-plugin/plugin-info.html
[conga-maven-plugin-valueprovider]: http://devops.wcm.io/conga/tooling/conga-maven-plugin/generate-mojo.html#valueProvider
[jce-policy]: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
