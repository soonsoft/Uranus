CREATE TABLE dictionary_group
(
    id uuid NOT NULL,
    dict_group_code varchar(100) NOT NULL,
    dict_group_name varchar(200) NULL,
    dict_group_type varchar(50) NULL,
    remark varchar(500) NULL,
    data_status smallint default 1 NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(dict_group_code)
);
COMMENT ON TABLE dictionary_group IS '字典分组';
COMMENT ON COLUMN dictionary_group.dict_group_code IS '字典分组编码';
COMMENT ON COLUMN dictionary_group.dict_group_name IS '字典分组名称';
COMMENT ON COLUMN dictionary_group.dict_group_type IS '字典分组类型';
COMMENT ON COLUMN dictionary_group.remark IS '备注';
COMMENT ON COLUMN dictionary_group.data_status IS '数据状态，1 - 有效，2 - 审核中，3 - 历史，4 - 禁用，99 - 草稿';
COMMENT ON COLUMN dictionary_group.create_user IS '创建用户';
COMMENT ON COLUMN dictionary_group.create_time IS '创建时间';
COMMENT ON COLUMN dictionary_group.update_user IS '更新用户';
COMMENT ON COLUMN dictionary_group.update_time IS '更新时间';

CREATE TABLE dictionary_item
(
    id uuid NOT NULL,
    dict_group_id uuid NOT NULL,
    dict_group_code varchar(100) NOT NULL,
    dict_item_code varchar(100) NOT NULL,
    dict_item_name varchar(200) NULL,
    dict_item_value varchar(500) NULL,
    parent_item_id uuid NULL,
    parent_item_code varchar(100) NULL,
    remark varchar(500) NULL,
    data_status smallint default 1 NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(dict_group_code, dict_item_code)
);

COMMENT ON TABLE dictionary_item IS '字典项';
COMMENT ON COLUMN dictionary_item.dict_group_id IS '所属字典分组ID';
COMMENT ON COLUMN dictionary_item.dict_group_code IS '所属字典分组编码';
COMMENT ON COLUMN dictionary_item.dict_item_code IS '字典项编码';
COMMENT ON COLUMN dictionary_item.dict_item_name IS '字典项名称';
COMMENT ON COLUMN dictionary_item.dict_item_value IS '字典项值';
COMMENT ON COLUMN dictionary_item.parent_item_id IS '父级字典项ID';
COMMENT ON COLUMN dictionary_item.parent_item_code IS '父级字典项编码';
COMMENT ON COLUMN dictionary_item.remark IS '备注';
COMMENT ON COLUMN dictionary_item.data_status IS '数据状态，1 - 有效，2 - 审核中，3 - 历史，4 - 禁用，99 - 草稿';
COMMENT ON COLUMN dictionary_item.create_user IS '创建用户';
COMMENT ON COLUMN dictionary_item.create_time IS '创建时间';
COMMENT ON COLUMN dictionary_item.update_user IS '更新用户';
COMMENT ON COLUMN dictionary_item.update_time IS '更新时间';
