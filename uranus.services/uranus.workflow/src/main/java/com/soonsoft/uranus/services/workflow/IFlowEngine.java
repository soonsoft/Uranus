package com.soonsoft.uranus.services.workflow;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowState;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public interface IFlowEngine<TFlowState extends FlowState, TFlowQuery> {

    /**
     * 流程启动
     * @param parameter 流程启动参数
     */
    void start(FlowActionParameter parameter);
    
    /**
     * 流程流转
     * @param nodeCode 操作节点编号
     * @param stateCode 操作对应的状态编号
     * @param parameter 流程操作相关参数
     * @return 操作后的状态信息
     */
    TFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter);

    /**
     * 退回到上一个节点
     * @param nodeCode 操作节点编号
     * @param parameter 流程操作相关操作
     * @return 操作后的状态信息
     */
    TFlowState back(String nodeCode, FlowActionParameter parameter);

    /**
     * 取消流程，Pending | Started 状态下都可以取消。
     * @param nodeCode 操作节点编号
     * @param parameter 流程取消相关参数
     */
    void cancel(String nodeCode, FlowActionParameter parameter);

    /**
     * 返回流程当前的状态
     * @return 返回 FlowStatus 枚举
     */
    FlowStatus getStatus();

    /**
     * 判断流程是否为已开始状态
     * @return FlowStatus.Started 为 true 否则为 false
     */
    boolean isStarted();

    /**
     * 判断流程是否已经取消
     * @return FlowStatus.Canceled 为 true 否则为 false
     */
    boolean isCanceled();

    /**
     * 判断流程是否已经完成
     * @return FlowStatus.Finished 为 true 否则为 false
     */
    boolean isFinished();

    /**
     * 返回流程的状态信息
     * @return
     */
    TFlowState currentState();

    /**
     * 返回流程查询对象
     * @return
     */
    TFlowQuery query();

}
