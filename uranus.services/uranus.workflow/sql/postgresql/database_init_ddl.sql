
-- 工作流
CREATE TABLE flow_process
(
    id uuid NOT NULL,
    process_code varchar(100) NOT NULL,
    process_name varchar(200) NULL,
    process_type varchar(50) NULL,
    remark varchar(500) NULL,
    status smallint default 1 NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(user_name)
);
COMMENT ON TABLE flow_process IS '工作流';
COMMENT ON COLUMN flow_process.process_code IS '工作流编码';
COMMENT ON COLUMN flow_process.process_name IS '工作流名称';
COMMENT ON COLUMN flow_process.process_type IS '工作流类型';
COMMENT ON COLUMN flow_process.remark IS '备注';
COMMENT ON COLUMN flow_process.status IS '状态，1 - 有效，0 - 无效';
COMMENT ON COLUMN flow_process.create_user IS '创建用户';
COMMENT ON COLUMN flow_process.create_time IS '创建时间';
COMMENT ON COLUMN flow_process.update_user IS '更新用户';
COMMENT ON COLUMN flow_process.update_time IS '更新时间';

-- 工作流节点
CREATE TABLE flow_node
(
    id uuid NOT NULL,
    process_id uuid NOT NULL,
    process_code varchar(100) NOT NULL,
    node_code varchar(100) NOT NULL,
    node_name varchar(100) NULL,
    remark varchar(500) NULL,
    node_type smallint NOT NULL,
    status smallint default 1 NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id)
);
COMMENT ON TABLE flow_node IS '工作流节点';
COMMENT ON COLUMN flow_node.process_id IS '所属工作流';
COMMENT ON COLUMN flow_node.process_code IS '工作流编码';
COMMENT ON COLUMN flow_node.node_code IS '工作流节点编码';
COMMENT ON COLUMN flow_node.node_name IS '工作流节点名称';
COMMENT ON COLUMN flow_node.remark IS '工作流节点备注';
COMMENT ON COLUMN flow_node.node_type IS '工作流节点类型，1 - 起始节点，2 - 普通节点，3 - 终结节点';
COMMENT ON COLUMN flow_node.status IS '状态，1 - 有效，0 - 无效';
COMMENT ON COLUMN flow_node.create_user IS '创建用户';
COMMENT ON COLUMN flow_node.create_time IS '创建时间';
COMMENT ON COLUMN flow_node.update_user IS '更新用户';
COMMENT ON COLUMN flow_node.update_time IS '更新时间';

-- 工作流动作
CREATE TABLE flow_action
(
    id uuid NOT NULL,
    process_id uuid NOT NULL,
    action_name varchar(50) NULL,
    action_value varchar(100) NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id)
);
COMMENT ON TABLE flow_action IS '工作流动作';
COMMENT ON COLUMN flow_action.process_id IS '所属工作流';
COMMENT ON COLUMN flow_action.action_name IS '动作名称（如：通过、拒绝）';
COMMENT ON COLUMN flow_action.action_value IS '动作值';
COMMENT ON COLUMN flow_action.create_user IS '创建用户';
COMMENT ON COLUMN flow_action.create_time IS '创建时间';
COMMENT ON COLUMN flow_action.update_user IS '更新用户';
COMMENT ON COLUMN flow_action.update_time IS '更新时间';

-- 工作流流向
CREATE TABLE flow_stream
(
    id uuid NOT NULL,
    process_id uuid NOT NULL,
    node_id uuid NOT NULL,
    node_code varchar(100) NOT NULL,
    action_id uuid NOT NULL,
    action_expression varchar(1000) NULL,
    next_node_id uuid NOT NULL,
    next_node_code varchar(100) NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id)
);
COMMENT ON TABLE flow_stream IS '工作流流向';
COMMENT ON COLUMN flow_stream.process_id IS '所属工作流';
COMMENT ON COLUMN flow_stream.node_id IS '节点ID';
COMMENT ON COLUMN flow_stream.node_code IS '节点编号';
COMMENT ON COLUMN flow_stream.action_id IS '动作ID';
COMMENT ON COLUMN flow_stream.action_expression IS '动作表达式，当同一个动作下有多个分支时，通过表达式辅助判断';
COMMENT ON COLUMN flow_stream.next_node_id IS '对应流向的下一个节点ID';
COMMENT ON COLUMN flow_stream.next_node_code IS '对应流向的下一个节点编号';
COMMENT ON COLUMN flow_stream.create_user IS '创建用户';
COMMENT ON COLUMN flow_stream.create_time IS '创建时间';
COMMENT ON COLUMN flow_stream.update_user IS '更新用户';
COMMENT ON COLUMN flow_stream.update_time IS '更新时间';

-- 工作流任务
CREATE TABLE flow_task
(
    id uuid NOT NULL,
    target_id varchar(100) NOT NULL,
    target_type varchar(50) NOT NULL,
    process_id uuid NOT NULL,
    current_node_id uuid NOT NULL,
    current_history_id uuid NOT NULL,
    finish_status smallint NOT NULL,
    taks_status smallint NOT NULL,
    create_user varchar(50) NOT NULL,
    create_time timestamp NOT NULL,
    update_user varchar(50) NOT NULL,
    update_time timestamp NOT NULL,
    PRIMARY KEY(id)
);
COMMENT ON TABLE flow_task IS '工作流任务';
COMMENT ON COLUMN flow_task.target_id IS '工作流关联的业务对象ID';
COMMENT ON COLUMN flow_task.target_type IS '工作流关联的业务标识';
COMMENT ON COLUMN flow_task.process_id IS '工作流ID';
COMMENT ON COLUMN flow_task.current_node_id IS '当前停留在哪个节点上';
COMMENT ON COLUMN flow_task.current_history_id IS '上一个流转的历史纪录';
COMMENT ON COLUMN flow_task.finish_status IS '是否结束';
COMMENT ON COLUMN flow_task.taks_status IS '任务状态，1 - 有效，2 - 失效';
COMMENT ON COLUMN flow_task.create_user IS '创建用户';
COMMENT ON COLUMN flow_task.create_time IS '创建时间';
COMMENT ON COLUMN flow_task.update_user IS '更新用户';
COMMENT ON COLUMN flow_task.update_time IS '更新时间';

-- 工作流历史
CREATE TABLE flow_history
(
    id uuid NOT NULL,
    task_id uuid NOT NULL,
    target_id varchar(100) NOT NULL,
    process_id uuid NOT NULL,
    node_id uuid NOT NULL,
    action_id uuid NOT NULL,
    stream_id uuid NOT NULL,
    remark varchar(1000) NULL,
    operator varchar(50) NULL,
    operate_time timestamp NULL
    PRIMARY KEY(id)
);
COMMENT ON TABLE flow_history IS '工作流历史';
COMMENT ON COLUMN flow_history.task_id IS '任务ID';
COMMENT ON COLUMN flow_history.target_id IS '业务对象ID';
COMMENT ON COLUMN flow_history.process_id IS '工作流ID';
COMMENT ON COLUMN flow_history.node_id IS '在哪个节点';
COMMENT ON COLUMN flow_history.action_id IS '做了什么操作';
COMMENT ON COLUMN flow_history.stream_id IS '当时的流向';
COMMENT ON COLUMN flow_history.remark IS '流程备注信息';
COMMENT ON COLUMN flow_history.operator IS '操作人';
COMMENT ON COLUMN flow_history.operate_time IS '操作时间';
