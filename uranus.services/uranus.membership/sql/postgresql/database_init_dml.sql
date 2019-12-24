--用户初始化
INSERT INTO auth_user VALUES('ac0c12aa-8f42-11e9-adcc-00163e1c3c68', 'admin', '系统管理员', null, 1, '2019-06-15 15:53:00');
INSERT INTO auth_password VALUES('ac0c12aa-8f42-11e9-adcc-00163e1c3c68', '{bcrypt}$2a$10$Vm9YJlM1UYTsPoqDl8a17eWHhCP1p2dGLzmk4zSvpkoRpOHJN4EH6', null, null, '2019-06-15 15:53:00');

--角色初始化
INSERT INTO auth_role VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '系统管理员', null, 1);
INSERT INTO auth_role VALUES('9fd642b8-8ded-11e9-a43c-00163e1c3c68', '用户', null, 1);

-- 菜单初始化
-- 三维地图
INSERT INTO sys_function VALUES('9aea9446-8b83-11e9-87ae-00163e1c3c68', '三维地图', null, null, 'menu', 1, 1);
INSERT INTO sys_menu VALUES('9aea9446-8b83-11e9-87ae-00163e1c3c68', null, '/index', '/content/icons/sys-setting.png', null, null, null);

-- 开关量信息
INSERT INTO sys_function VALUES('2da5b842-8b84-11e9-bbc8-00163e1c3c68', '开关量信息', null, null, 'menu', 1, 2);
INSERT INTO sys_menu VALUES('2da5b842-8b84-11e9-bbc8-00163e1c3c68', null, '/map/switch-info', '/content/icons/sys-setting.png', null, null, null);

-- 堆场堆垛信息
INSERT INTO sys_function VALUES('17a7d09c-8b85-11e9-8f15-00163e1c3c68', '堆场堆垛信息', null, null, 'menu', 1, 3);
INSERT INTO sys_menu VALUES('17a7d09c-8b85-11e9-8f15-00163e1c3c68', null, '/map/heap-info', '/content/icons/sys-setting.png', null, null, null);

-- 人员定位管理
INSERT INTO sys_function VALUES('f0feb8aa-8b86-11e9-b499-00163e1c3c68', '人员定位管理', null, null, 'menu', 1, 4);
INSERT INTO sys_menu VALUES('f0feb8aa-8b86-11e9-b499-00163e1c3c68', null, null, '/content/icons/sys-setting.png', null, null, null);

-- 人员基本信息管理
INSERT INTO sys_function VALUES('1d7d0440-8b87-11e9-b4f7-00163e1c3c68', '人员基本信息管理', 'f0feb8aa-8b86-11e9-b499-00163e1c3c68', null, 'menu', 1, 5);
INSERT INTO sys_menu VALUES('1d7d0440-8b87-11e9-b4f7-00163e1c3c68', null, '/coming-soon', '/content/icons/sys-setting.png', null, null, null);

-- 人员实时位置监控
INSERT INTO sys_function VALUES('36974a4e-8b87-11e9-8494-00163e1c3c68', '人员实时位置监控', 'f0feb8aa-8b86-11e9-b499-00163e1c3c68', null, 'menu', 1, 6);
INSERT INTO sys_menu VALUES('36974a4e-8b87-11e9-8494-00163e1c3c68', null, '/coming-soon', '/content/icons/sys-setting.png', null, null, null);

-- 人员历史轨迹管理
INSERT INTO sys_function VALUES('3fae6824-8b87-11e9-9cda-00163e1c3c68', '人员历史轨迹管理', 'f0feb8aa-8b86-11e9-b499-00163e1c3c68', null, 'menu', 1, 7);
INSERT INTO sys_menu VALUES('3fae6824-8b87-11e9-9cda-00163e1c3c68', null, '/coming-soon', '/content/icons/sys-setting.png', null, null, null);

-- 视频监控
INSERT INTO sys_function VALUES('4b1bf348-8b87-11e9-9fc4-00163e1c3c68', '视频监控', null, null, 'menu', 1, 8);
INSERT INTO sys_menu VALUES('4b1bf348-8b87-11e9-9fc4-00163e1c3c68', null, '/map/camera-info', '/content/icons/sys-setting.png', null, null, null);

-- 系统管理
INSERT INTO sys_function VALUES('54688f38-8b87-11e9-976e-00163e1c3c68', '系统管理', null, null, 'menu', 1, 9);
INSERT INTO sys_menu VALUES('54688f38-8b87-11e9-976e-00163e1c3c68', null, null, '/content/icons/sys-setting.png', null, null, null);

-- 用户管理
INSERT INTO sys_function VALUES('658d3f70-8b87-11e9-9bf1-00163e1c3c68', '用户管理', '54688f38-8b87-11e9-976e-00163e1c3c68', null, 'menu', 1, 10);
INSERT INTO sys_menu VALUES('658d3f70-8b87-11e9-9bf1-00163e1c3c68', null, '/coming-soon', '/content/icons/sys-setting.png', null, null, null);

-- 角色管理
INSERT INTO sys_function VALUES('6e58d114-8b87-11e9-b778-00163e1c3c68', '角色管理', '54688f38-8b87-11e9-976e-00163e1c3c68', null, 'menu', 1, 11);
INSERT INTO sys_menu VALUES('6e58d114-8b87-11e9-b778-00163e1c3c68', null, '/coming-soon', '/content/icons/sys-setting.png', null, null, null);

-- 模块管理
INSERT INTO sys_function VALUES('767defd2-8b87-11e9-99cb-00163e1c3c68', '模块管理', '54688f38-8b87-11e9-976e-00163e1c3c68', null, 'menu', 1, 12);
INSERT INTO sys_menu VALUES('767defd2-8b87-11e9-99cb-00163e1c3c68', null, '/settings/menus', '/content/icons/sys-setting.png', null, null, null);

--用户和角色绑定
INSERT INTO auth_users_in_roles VALUES('ac0c12aa-8f42-11e9-adcc-00163e1c3c68', '00da4ab8-8b88-11e9-ac93-00163e1c3c68');

-- 角色和菜单绑定
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '9aea9446-8b83-11e9-87ae-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '2da5b842-8b84-11e9-bbc8-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '17a7d09c-8b85-11e9-8f15-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', 'f0feb8aa-8b86-11e9-b499-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '1d7d0440-8b87-11e9-b4f7-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '36974a4e-8b87-11e9-8494-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '3fae6824-8b87-11e9-9cda-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '4b1bf348-8b87-11e9-9fc4-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '54688f38-8b87-11e9-976e-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '658d3f70-8b87-11e9-9bf1-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '6e58d114-8b87-11e9-b778-00163e1c3c68');
INSERT INTO auth_roles_in_functions VALUES('00da4ab8-8b88-11e9-ac93-00163e1c3c68', '767defd2-8b87-11e9-99cb-00163e1c3c68');

