<img src="https://wcm.io/images/favicon-16@2x.png"/> CONGA Plugin for Ansible
======
[![Build](https://github.com/wcm-io-devops/conga-ansible-plugin/workflows/Build/badge.svg?branch=develop)](https://github.com/wcm-io-devops/conga-ansible-plugin/actions?query=workflow%3ABuild+branch%3Adevelop)
[![Maven Central](https://img.shields.io/maven-central/v/io.wcm.devops.conga.plugins/io.wcm.devops.conga.plugins.ansible)](https://repo1.maven.org/maven2/io/wcm/devops/conga/plugins/io.wcm.devops.conga.plugins.ansible)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=wcm-io-devops_conga-ansible-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=wcm-io-devops_conga-ansible-plugin)

Documentation: https://devops.wcm.io/conga/plugins/ansible/<br/>
Issues: https://github.com/wcm-io-devops/conga-ansible-plugin/issues<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://github.com/wcm-io-devops/conga-ansible-plugin/actions<br/>
Commercial support: https://wcm.io/commercial-support.html


## Build from sources

If you want to build from sources make sure you have configured all [Maven Repositories](https://devops.wcm.io/maven.html) in your settings.xml.

See [Maven Settings](https://github.com/wcm-io-devops/conga-ansible-plugin/blob/develop/.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```


## Included Sources

This plugin includes sources from

* https://github.com/ilpianista/ansible-inventory-java (MIT license)
