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

* Value Provider Plugin to read [Ansible Inventory][ansible-inventory] files.


### Acknowledgments

This plugins uses (and embeds) code from the following projects:

* [ansible-inventory-java][ansible-inventory-java] developed by Andrea Scarpino
* [JavaAnsibleVault][ansible-vault-java] developed by Wedjaa



[usage]: usage.html
[extensions]: extensions.html
[apidocs]: conga-ansible-plugin/apidocs/
[changelog]: changes-report.html
[conga]: http://devops.wcm.io/conga/
[ansible]: https://www.ansible.com/
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-inventory-java]: https://github.com/ilpianista/ansible-inventory-java
[ansible-vault-java]: https://github.com/Wedjaa/JavaAnsibleVault
