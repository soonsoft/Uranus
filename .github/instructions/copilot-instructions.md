---
applyTo: '**'
---
# Uranus 仓库 — Copilot 使用说明（基础版）

**目的**：为自动化代码补全、代码生成与变更建议提供必要的项目上下文、约束与质量门（build/tests/lint）要求，帮助 AI 在对仓库进行修改时保持安全、高质量与可审计的变更。

**注意**：本文件为基础指导，遇到设计性或架构性决策（新增核心模块、大规模重构、关键依赖升级、数据库模式变更等）时，应主动向维护者询问并在 Pull Request 中明确说明变更风险与回滚方案。

## 项目概览（快速参考）
- 类型：多模块 Maven Java 项目
- 主要模块：`uranus.app`（包含 `uranus.api`, `uranus.site`）、`uranus.configuration`、`uranus.core`、`uranus.security`、`uranus.services`（含 `uranus.dictionary`, `uranus.membership`, `uranus.workflow`）、`uranus.web`、`uranus.test` 等
- 配置：YAML（`application*.yml` 多环境）
- 数据库：PostgreSQL（见模块下的 `sql/postgresql/`）

## 编辑与修改原则（必须遵守）
- 最小改动原则：每次提交只解决一个问题或实现一个小特性，避免一次性大改动。
- 保持风格一致：不要无必要地更改代码风格或格式；遵循已有代码风格（基于现有导入顺序、注释习惯、日志框架）。
- 新增依赖需谨慎：任何新依赖必须在 PR 中说明用途、体积与许可证，优先使用轻量、常用且成熟的库。
- 不要提交凭据或秘密：所有密钥、密码、凭证必须使用环境变量或外部配置，不写入源码或示例配置中。

## 开发指南
- 遵循 SOLID 原则与常见设计模式，确保代码易于维护与扩展。
- 使用依赖注入（如 Spring Framework）管理组件依赖，避免硬编码依赖，依赖注入统一使用构造函数注入。
- 代码注释应简洁明了，复杂逻辑需提供足够的上下文说明。

### 基本编码规范
- 命名规范：类名使用 PascalCase，方法与变量使用 camelCase，常量使用 UPPER_SNAKE_CASE。
- 日志记录：使用统一的日志框架（如 SLF4J），避免在生产代码中使用 `System.out.println`。
- 异常处理：优先使用自定义异常类，避免捕获通用异常（如 `Exception` 或 `Throwable`），确保异常信息有助于调试。

### 代码结构
- 每个模块应有清晰的职责划分，避免模块间的紧耦。
- 公共代码应放在 `uranus.core`模块，避免重复实现。
  - 通常与语言特性相关的工具代码放到`com.soonsoft.uranus.core.common`包下。
  - 软件通用解决方案放到`com.soonsoft.uranus.core.utils`包下。
- 通用的数据库操作API应放在`uranus.data`模块中。
- 通用的安全功能应放在`uranus.security`模块中。
- 业务相关功能应放在各自的服务模块中，如`uranus.services`下。
  - `uranus.membership`模块是`uranus.security`的配套数据库实现，负责用户注册、登录、权限管理等功能。
  - `uranus.dictionary`模块提供数据字典功能。
  - `uranus.workflow`模块提供工作流引擎功能。

### Controller 开发规范
- RESTful API 设计：遵循 RESTful 设计原则，使用合适的 HTTP 方法（GET、POST、PUT、DELETE 等）。
- 路径命名：使用有意义且一致的路径命名，避免使用缩写。
- 请求与响应：使用 DTO（数据传输对象）封装请求与响应数据，避免直接暴露实体类。
- 输入验证：在 Controller 层进行基本的输入验证，确保请求数据的完整性与合法性。

### 模块开发规范
- 模块职责单一：每个模块应有明确的职责，避免模块间的功能重叠。
- 依赖管理：模块间依赖应清晰，避免循环依赖，使用 Maven 的依赖管理功能控制版本一致性。
- 配置管理：模块配置应集中管理，避免硬编码配置参数。
- 模块中不包含Controller层，Controller层统一放在`uranus.api`模块与`uranus.site`模块中。
- 模块代码结构
  - `com.soonsoft.uranus.services.[module_name].config`：模块配置类，提供 Bean 定义与初始化。
  - `com.soonsoft.uranus.services.[module_name].dao`：数据访问对象，封装数据库操作。
  - `com.soonsoft.uranus.services.[module_name].models`：领域模型，模块与外界交互的核心实体类，是逻辑实现的基础。
  - `com.soonsoft.uranus.services.[module_name].po`：数据库持久化对象，映射数据库表结构。
  - `com.soonsoft.uranus.services.[module_name].service`：服务实现类，包含具体的业务逻辑。
- 每个模块都应该有一个对应的异常类，放在`com.soonsoft.uranus.services.[module_name].exceptions`包下，用于封装模块特有的异常情况。
  - 异常类应继承自`com.soonsoft.uranus.core.error.ApplicationException`，并提供有意义的错误码与消息。

### 摸块开发示例
- 数据访问对象（DAO）示例：
``` Java
public class AuthRoleDAO extends MybatisBaseDAO<AuthRole> implements IMapperID {
    // 需要自定义的数据库操作方法
}
```
  - 继承自 `com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO<T>`，其中 `T` 为对应的持久化对象（PO）类型。
    - `MybatisBaseDAO<T>` 已经实现了常用的 CRUD 方法，如 `getByPrimary`、`insert`、`insertSelective`、`update`、`updateSelective`、`delete` 等，优先使用这些方法完成常见的数据操作。
    - 使用`com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess` 类完成自定义查询或是一些特殊的数据变更操作，在构建功能时查找合适的方法进行调用。
  - 实现 `com.soonsoft.uranus.services.membership.dao.IMapperID` 接口以标识 MyBatis 映射器。
  - 实现查询方式需要自定义SQL语句，SQL语句放在 `resources/sql/mapper/` 目录下的对应 XML 文件中。
    - 通常一个模块对应一个XML文件，文件命名为 `模块名-Mapper.xml`，例如 `MembershipMapper.xml`。由于每个模块都只有一个文件，所以需要确保文件中的Mapper ID是唯一的，不能重复。
    - SQL 语句的 ID 命名应与 DAO 方法名保持一致，便于查找与维护。
  - 分页查询时，使用 `com.soonsoft.uranus.core.model.data.Page` 类封装分页参数。
    - 查询结果使用 `com.soonsoft.uranus.core.model.data.PagingList<T>` 类封装，`T` 为结果实体类型。
    - 分页查询示例方法：
``` Java
public PagingList<AuthUser> findUsersByRoleId(Long roleId, int pageIndex, int pageSize) {
    Map<String, Object> params = new HashMap<>();
    params.put("roleId", roleId);

    Page page = new Page(pageIndex, pageSize);

    List<AuthUser> list = this.getDatabaseAccess().select("findUsersByRoleId", params, page);
    return new PagingList<>(list, page);
}
```

- 分离业务逻辑与数据访问逻辑，在服务类中调用 DAO 进行数据操作
- 服务类方法确保可以脱离数据库进行单元测试
- 服务类示例：
``` Java
@Service
public class AuthRoleService {
    private AuthRoleDAO authRoleDAO;
    
    public AuthRoleService(AuthRoleDAO authRoleDAO) {
        this.authRoleDAO = authRoleDAO;
    }

    public AuthRole getRoleById(Long roleId) {
        return authRoleDAO.getByPrimary(roleId);
    }

    public void createRole(AuthRole role) {
        authRoleDAO.insert(role);
    }
}
```

## 测试与验证
- 新增功能必须包含单元测试（统一放到 `uranus.test`工程中），优先使用项目中现有测试框架（通常为 JUnit + Mockito）。
- 在提交前，本地应能通过：`mvn -q -DskipTests=false test`（或模块级 `mvn test`）；CI 会在 PR 中再次运行完整构建。
- 对于数据库变更或集成行为，提供可重复的集成测试或在 PR 描述中标明需要的运行前置条件（例如本地 Postgres）。

## 提交与 PR 指南
- 提交信息推荐使用简洁的英文或中英混合，包含“动词-目的”的简短描述，例如：`feat(membership): add user registration endpoint` 或 `fix(core): handle null pointer in XService`。
- PR 描述应包含：变更动机、实现要点、回归测试说明、对外接口变更（如有）以及风险与兼容性说明。

## 构建/运行（快速参考）
- 构建整个工程：在仓库根目录运行 `mvn clean package -DskipTests=false`（注：根据网络与机器性能，可能较慢）。
- 运行单模块测试：进入模块目录并运行 `mvn test`。
- 查找可执行入口：各模块的 `src/main/java` 中包含具体启动或控制器类，参照模块 README 或 `pom.xml` 的 `<mainClass>`（如存在）。

## 当 AI 需要询问人类维护者的情形（必须提问）
- 需要修改数据库表结构或运行数据库迁移脚本时
- 引入新框架或关键依赖（例如切换到不同的 JSON 序列化库、引入消息队列等）
- 涉及安全、加密或认证策略修改时
- 需要对外兼容性变更或 API contract 修改时

## 附加建议（可选但推荐）
- 每当实现新功能，补充或更新 `doc/` 下的文档（例如 `doc/project-instructions.md`）。
- 如果改动影响运行或部署，提供简短的“如何在本地验证”步骤。

## 安全与合规
- 不要将任何外部网络请求凭证、API Key、私钥或密码直接写入仓库。
- 若需使用第三方服务，记录服务名称与用途，并在 PR 中说明数据流与安全措施。

## 质量门（最终检查清单，PR 作者须自查）
1. 本地或 CI 构建成功（Build PASS）
2. 单元测试通过（Tests PASS）
3. 未引入安全凭据或敏感信息
4. 新增依赖已在 PR 中声明并评估许可/安全性

---