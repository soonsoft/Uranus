[![Badge](https://img.shields.io/badge/link-996.icu-%23FF4D5B.svg?style=flat-square)](https://996.icu/#/zh_CN)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

# Uranus
Uranus（乌拉诺斯）是一组基于Java构建的Web站点开发组件集。目标是当需要构建Java Web项目时可以专注于业务逻辑，无需为一些基础特性分神。

# maven打包

cmd
> mvn clean package -Dmaven.test.skip=true

windows powershell
> $mvnArgs1 ="mvn clean package -Dmaven.test.skip=true".replace('-D','`-D')<br />Invoke-Expression $mvnArgs1
