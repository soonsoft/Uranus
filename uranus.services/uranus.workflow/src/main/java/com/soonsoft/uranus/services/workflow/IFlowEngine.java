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
     * @return 操作状态信息
     */
    TFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter);

    /**
     * 取消流程，Pending | Started 状态下都可以取消。
     * @param parameter 流程取消相关参数
     */
    void cancel(FlowActionParameter parameter);

    FlowStatus getStatus();

    boolean isStarted();

    boolean isCanceled();

    boolean isFinished();

    TFlowState currentState();

    TFlowQuery query();

}
