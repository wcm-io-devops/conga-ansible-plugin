## About CONGA Ansible Plugin

wcm.io DevOps CONGA Plugin for [Ansible][ansible].

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.conga.plugins/io.wcm.devops.conga.plugins.ansible/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.conga.plugins/io.wcm.devops.conga.plugins.ansible)


### Documentation

* [Usage][usage]
* [CONGA Extensions][extensions]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

This plugin extends [CONGA][conga] with:

* Ability to read [Ansible Inventory][ansible-inventory] files.
* Ability to read encrypted [Ansible Vault][ansible-vault] files.

_**Please note:** You need to install the [Java Cryptography Extension (JCE) Unlimited Strength policy files][jce-policy] from Oracle, because Ansible uses 256 bit keys to handle encryption and decryption of the vault files._


### Acknowledgments

This plugins uses (and embeds) code from the following projects:

* [ansible-inventory-java][ansible-inventory-java] developed by Andrea Scarpino
* [JavaAnsibleVault][ansible-vault-java] developed by Wedjaa


### Further Resources

* [wcm.io CONGA training material with exercises](http://training.wcm.io/conga/)
* [adaptTo() 2015 Talk: CONGA - Configuration generation for Sling and AEM](https://adapt.to/2015/en/schedule/conga---configuration-generation-for-sling-and-aem.html)
* [adaptTo() 2017 Talk: Automate AEM Deployment with Ansible and wcm.io CONGA](https://adapt.to/2017/en/schedule/automate-aem-deployment-with-ansible-and-wcm-io-conga.html)



[usage]: usage.html
[extensions]: extensions.html
[apidocs]: conga-ansible-plugin/apidocs/
[changelog]: changes-report.html
[conga]: http://devops.wcm.io/conga/
[ansible]: https://www.ansible.com/
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-inventory-java]: https://github.com/ilpianista/ansible-inventory-java
[ansible-vault]: https://docs.ansible.com/ansible/latest/vault.html
[ansible-vault-java]: https://github.com/Wedjaa/JavaAnsibleVault
[jce-policy]: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
