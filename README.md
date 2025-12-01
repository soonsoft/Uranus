[![Badge](https://img.shields.io/badge/link-996.icu-%23FF4D5B.svg?style=flat-square)](https://996.icu/#/zh_CN)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

# Uranus
Uranus（乌拉诺斯）是一组基于Java构建的 Web 站点开发组件集。目标是当需要构建Java Web项目时可以专注于业务逻辑，无需为一些基础特性分神。

Uranus 主要是基于 SpringBoot 3.0 进行构建。

Uranus 主要为两种 Web 形态的应用提供支持。
- WebSite 后端应用，由后端路由驱动的Web应用程序
- WebApi 基于Web的RESTFUL风格的API服务应用

## 模块说明
- `uranus.app`：应用入口模块
  - `uranus.api`：形态1，Restful风格的Web API服务
  - `uranus.site`：形态2，传统后端路由的Web应用程序
- `uranus.core`：核心业务逻辑模块
- `uranus.data`: 数据库操作模块，包含了分页，ORM等基本的数据库操作封装
- `uranus.security`：安全相关模块
- `uranus.services`：服务层聚合模块，包含：
  - `uranus.dictionary`：字典服务模块。
  - `uranus.membership`：会员/用户管理模块，配合`uranus.security`使用
  - `uranus.workflow`：工作流模块。
- `uranus.test`：单元测试模块。
- `uranus.web`：Web 层模块，提供一些WebAPI的标准接口与实现

各模块均采用标准的 Maven 目录结构，`src/main/java` 存放主代码，`src/main/resources` 存放资源文件，`target` 目录为编译输出。

## 构建

### maven打包

``` bash
mvn clean package -Dmaven.test.skip=true
```

## 配置

### 模块与组件
``` Java
@EnableAutoConfiguration // 开启SpringBoot 自动配置
@SpringBootApplication // 声明SpringBoot应用
@ComponentScan(basePackages = { // 显示声明需要扫描的包名，提高启动速度
    "com.soonsoft.uranus.site.config",
    "com.soonsoft.uranus.site.controller",
    "com.soonsoft.uranus.site.service",
    "com.soonsoft.uranus.services"
})
@EnableDatabaseAccess // 启动 uranus.data 模块
@EnableSimpleSecurity // 启动 uranus.security 模块
@EnableMembership // 启动 uranus.membership 模块
@EnableDictionaryManager // 启动 uranus.dictionary 模块
public class SiteApplication {

    public static void main(String[] args) {
        onStop();
        run(SiteApplication.class, args);
    }

    private static void run(Class<?> clazz, String[] args) {
        Properties properties = System.getProperties();
        ApplicationHome applicationHome = new ApplicationHome(clazz);
        properties.setProperty("uranus.application-home", applicationHome.getDir().getAbsolutePath());

        SpringApplication.run(clazz, args);
    }

    private static void onStop() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               System.out.println("SiteApplication Shutdown.");
            }
        });
    }
    
}
```
