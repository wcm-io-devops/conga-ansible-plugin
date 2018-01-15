## CONGA - Extensions

The CONGA Ansible Plugin extends CONGA using its [extensibility model][conga-extensibility].

The plugin implementations only support static files. Ansible supports reading dynamic files (e.g. Python scripts) for inventories, password files etc. - but this CONGA plugin implementation only supports files that contain the result without executing them.


### Provided Plugins

#### Value Provider Plugins

| Plugin name         | Description
|---------------------|-------------
| `ansible-vault`     | Allows to variables from encrypted [Ansible Vault][ansible-vault] files.
| `ansible-inventory` | Allows to read [Ansible Inventory][ansible-inventory] files.


You can also define multiple value providers of the same plugin type. See [value provider configuration][conga-maven-plugin-valueprovider] for details.

#### URL File plugins

| Plugin name         | Description
|---------------------|-------------
| `ansible-vault`     | Decrypts a file encrypted with [Ansible Vault][ansible-vault. Can be combined with other URL file plugins.


### Ansible Vault

_**Please note:** You need to install the [Java Cryptography Extension (JCE) Unlimited Strength policy files][jce-policy] from Oracle, because Ansible uses 256 bit keys to handle encryption and decryption of the vault files. If you are using Java 8u162 or higher they are already active by default._

Ansible Vault files are files encrypted with [Ansible Vault][ansible-vault]. Ansible Vault requires a vault password to be set in the environment to decrypt the files. You can set the vault password by either:

* Setting the password via the system property:<br/>
  `-Dansible.vault.password=mypassword`
* Setting the password via the system property pointing to a password file:<br/>
  `-Dansible.vault.password.file=/path/to/password-file`
* Setting the system environment variable `ANSIBLE_VAULT_PASSWORD_FILE` pointing to the vault password file. This variable is used by Ansible itself as well.


#### Reading variables from Ansible Vault files

If you have an Ansible Vault-encrypted file containing password or other parameters in YAML format, you can use the Ansible Vault value provider plugin to read them in CONGA.

Example YAML file *before encryption*:

```yaml
pwd1: abc
```

Using the value provider plugin for Ansible Vault you can the variables defined in the encrypted file.

Example:

* `${ansible-vault::pwd1}` = abc

Via configuration you need to tell the [CONGA Maven Plugin][conga-maven-plugin] (via the pom.xml or via system property) where to look up the file containing the encrypted variables, and the password to decrypt the data.

Example:

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <configuration>
    <valueProvider>
      ansible-vault;file=${project.basedir}/vault/myvars
    </valueProvider>
  </configuration>
</plugin>
```


#### Reading and decrypting Ansible Vault-encrypted files

The URL File plugin for Ansible Vault allows to access (and decrypt) Ansible Vault-encrpyted files anywhere in CONGA where a URL path to a file can be given - e.g. in file definitions for roles. This can be combined with other URL file plugins, e.g. getting an encrypted file from classpath, HTTP or other sources and then decrypting it.

Example:

```yaml
# Copy file from classpath and decrypt it
- file: mysample.txt
  url: ansible-vault:classpath:/sample.txt
```


### Ansible Inventory

#### Reading Ansible Inventory files

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

The implementation supports both the "INI-style" inventory format as shown above, as well as JSON-style inventories as [documented here][ansible-inventory-developing].


[conga-extensibility]: http://devops.wcm.io/conga/extensibility.html
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-inventory-developing]: http://docs.ansible.com/ansible/latest/dev_guide/developing_inventory.html
[ansible-vault]: https://docs.ansible.com/ansible/latest/vault.html
[conga-maven-plugin]: http://devops.wcm.io/conga/tooling/conga-maven-plugin/plugin-info.html
[conga-maven-plugin-valueprovider]: http://devops.wcm.io/conga/tooling/conga-maven-plugin/generate-mojo.html#valueProvider
[jce-policy]: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
