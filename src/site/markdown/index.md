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

For reading Ansible Inventory files we are using the code developed by Andrea Scarpino provided on [GitHub][ansible-inventory-java].



[usage]: usage.html
[extensions]: extensions.html
[apidocs]: conga-ansible-plugin/apidocs/
[changelog]: changes-report.html
[conga]: http://devops.wcm.io/conga/
[ansible]: https://www.ansible.com/
[ansible-inventory]: http://docs.ansible.com/ansible/latest/intro_inventory.html
[ansible-inventory-java]: https://github.com/ilpianista/ansible-inventory-java
