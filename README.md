<img src="https://wcm.io/images/favicon-16@2x.png"/> CONGA Plugin for Ansible
======
[![Build Status](https://travis-ci.com/wcm-io-devops/conga-ansible-plugin.png?branch=develop)](https://travis-ci.com/wcm-io-devops/conga-ansible-plugin)
[![Code Coverage](https://codecov.io/gh/wcm-io-devops/conga-ansible-plugin/branch/develop/graph/badge.svg)](https://codecov.io/gh/wcm-io-devops/conga-ansible-plugin)

Documentation: https://devops.wcm.io/conga/plugins/ansible/<br/>
Issues: https://wcm-io.atlassian.net/projects/WDCONGA<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://travis-ci.org/conga-ansible-plugin<br/>
Commercial support: https://wcm.io/commercial-support.html


## Build from sources

If you want to build from sources make sure you have configured all [Maven Repositories](https://devops.wcm.io/maven.html) in your settings.xml.

See [Travis Maven settings.xml](https://github.com/conga-ansible-plugin/blob/master/.travis.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```


## Included Sources

This plugin includes sources from

* https://github.com/ilpianista/ansible-inventory-java (MIT license)
