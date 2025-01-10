## About CONGA Ansible Plugin

wcm.io DevOps CONGA Plugin for [Ansible][ansible].

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm.devops.conga.plugins/io.wcm.devops.conga.plugins.ansible)](https://repo1.maven.org/maven2/io/wcm/devops/conga/plugins/io.wcm.devops.conga.plugins.ansible)


### Documentation

* [Usage][usage]
* [CONGA Extensions][extensions]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

This plugin extends [CONGA][conga] with:

* Ability to read encrypted [Ansible Vault][ansible-vault] files.
* Ability to read [Ansible Inventory][ansible-inventory] files.
* Ability to encrypt values in Model YAML files.

_**Please note:** You need to install the [Java Cryptography Extension (JCE) Unlimited Strength policy files][jce-policy] from Oracle, because Ansible uses 256 bit keys to handle encryption and decryption of the vault files. If you are using Java 8u162 or higher they are already active by default._


### Acknowledgments

This plugins uses (and embeds) code from the following projects:

* [JavaAnsibleVault][ansible-vault-java] developed by Wedjaa
* [ansible-inventory-java][ansible-inventory-java] developed by Andrea Scarpino


### Further Resources

* [wcm.io CONGA training material with exercises](https://training.wcm.io/conga/)
* [adaptTo() 2015 Talk: CONGA - Configuration generation for Sling and AEM][adaptto-talk-2015-conga]
* [adaptTo() 2017 Talk: Automate AEM Deployment with Ansible and wcm.io CONGA][adaptto-talk-2017-aem-ansible]
* [adaptTo() 2018 Talk: Maven Archetypes for AEM][adaptto-talk-2018-aem-archetypes]
* [wcm.io Ansible Automation for AEM][aem-ansible]


[usage]: usage.html
[extensions]: extensions.html
[apidocs]: conga-ansible-plugin/apidocs/
[changelog]: changes.html
[conga]: https://devops.wcm.io/conga/
[ansible]: https://www.ansible.com/
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-inventory-java]: https://github.com/ilpianista/ansible-inventory-java
[ansible-vault]: https://docs.ansible.com/ansible/latest/vault.html
[ansible-vault-java]: https://github.com/Wedjaa/JavaAnsibleVault
[jce-policy]: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
[adaptto-talk-2015-conga]: https://adapt.to/2015/en/schedule/conga---configuration-generation-for-sling-and-aem.html
[adaptto-talk-2017-aem-ansible]: https://adapt.to/2017/en/schedule/automate-aem-deployment-with-ansible-and-wcm-io-conga.html
[adaptto-talk-2018-aem-archetypes]: https://adapt.to/2018/en/schedule/maven-archetypes-for-aem.html
[aem-ansible]: https://devops.wcm.io/ansible-aem/
