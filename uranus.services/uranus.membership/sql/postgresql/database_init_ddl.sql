-- 用户表
CREATE TABLE auth_user
(
    user_id uuid PRIMARY KEY NOT NULL,
    user_name varchar(100) NOT NULL,
    nick_name varchar(200) NULL,
    cell_phone varchar(20) NULL,
    status smallint default 1 NOT NULL,
    create_time timestamp NOT NULL,
    UNIQUE(user_name)
);
COMMENT ON TABLE auth_user IS '用户表';
COMMENT ON COLUMN auth_user.user_name IS '用户名';
COMMENT ON COLUMN auth_user.nick_name IS '昵称';
COMMENT ON COLUMN auth_user.cell_phone IS '手机号码';
COMMENT ON COLUMN auth_user.status IS '状态 - 0: 无效, 1: 有效';

-- 用户密码表
CREATE TABLE auth_password
(
    user_id uuid PRIMARY KEY NOT NULL,
    password_value varchar(2048) NOT NULL,
    password_salt varchar(1024) NULL,
    password_changed_time timestamp NULL,
    create_time timestamp NULL
);
COMMENT ON TABLE auth_password IS '用户密码表';
COMMENT ON COLUMN auth_password.password_value IS '密码';
COMMENT ON COLUMN auth_password.password_salt IS '盐值';
COMMENT ON COLUMN auth_password.password_changed_time IS '修改时间';

-- 角色表
CREATE TABLE auth_role
(
    role_id uuid PRIMARY KEY NOT NULL,
    role_name varchar(100) NOT NULL,
    description varchar(200) NULL,
    status smallint default 1 NOT NULL,
    UNIQUE(role_name)
);
COMMENT ON TABLE auth_role IS '角色表';
COMMENT ON COLUMN auth_role.role_name IS '角色名称';
COMMENT ON COLUMN auth_role.status IS '状态 - 0: 无效, 1: 有效';


-- 用户组
CREATE TABLE auth_group
(
    group_id uuid NOT NULL,
    group_name varchar(100) NOT NULL,
    description varchar(200) NULL,
    status smallint default 1 NOT NULL,
    UNIQUE(group_name)
);
COMMENT ON TABLE auth_group IS '用户组';
COMMENT ON COLUMN auth_group.group_name IS '用户组名称';
COMMENT ON COLUMN auth_group.status IS '状态 - 0: 无效, 1: 有效';

-- 角色和用户关系表
CREATE TABLE auth_users_in_roles
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    PRIMARY KEY(user_id, role_id)
);
--CREATE INDEX idx_user_id ON auth_users_in_roles USING HASH (user_id);
--CREATE INDEX idx_role_id ON auth_users_in_roles USING HASH (role_id);
COMMENT ON TABLE auth_users_in_roles IS '角色和用户关系表';

-- 用户组和角色关系表
CREATE TABLE auth_roles_in_groups
(
    role_id uuid NOT NULL,
    group_id uuid NOT NULL,
    PRIMARY KEY(role_id, group_id)
);
COMMENT ON TABLE auth_roles_in_groups IS '用户组和角色关系表';

-- 功能表
CREATE TABLE sys_function
(
    function_id uuid PRIMARY KEY NOT NULL,
    function_name varchar(200) NOT NULL,
    parent_id uuid NULL,
    description varchar(200) NULL,
    type varchar(20) NOT NULL,
    status smallint default 1 NOT NULL,
    sort_value int default 0 NOT NULL
);
COMMENT ON TABLE sys_function IS '功能表';
COMMENT ON COLUMN sys_function.function_name IS '功能名称';
COMMENT ON COLUMN sys_function.type IS '功能类型 - menu: 菜单, action: 操作';
COMMENT ON COLUMN sys_function.status IS '状态 - 0: 无效, 1: 有效';

-- 菜单表
CREATE TABLE sys_menu
(
    function_id uuid PRIMARY KEY NOT NULL,
    menu_key varchar(100) NULL,
    url varchar(2048) NULL,
    icon varchar(2048) NULL,
    background varchar(50) NULL,
    theme_info varchar(200) NULL,
    tile_style varchar(20) NULL
);
COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.menu_key IS '菜单Key，用于和程序逻辑关联';
COMMENT ON COLUMN sys_menu.url IS '菜单地址';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.background IS '菜单图标背景颜色';
COMMENT ON COLUMN sys_menu.theme_info IS '菜单对应功能的主题信息';
COMMENT ON COLUMN sys_menu.tile_style IS '菜单是否展示为动态磁贴 - small: 小, medium: 中, wide: 宽, large: 大';

-- 角色和功能关系表
CREATE TABLE auth_roles_in_functions
(
    role_id uuid NOT NULL,
    function_id uuid NOT NULL,
    PRIMARY KEY(role_id, function_id)
);
COMMENT ON TABLE auth_roles_in_functions IS '角色和功能关系表';
