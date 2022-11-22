package com.soonsoft.uranus.services.workflow;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public interface IFlowRepository<TFlowDefinition extends FlowDefinition<?>, TFlowState> {
    
    /**
     * 获取工作流程定义（不包含流程状态）
     * @param flowCode 流程编码
     * @return
     */
    TFlowDefinition getDefinition(String flowCode);

    /**
     * 获取当前的流程状态
     * @param parameter 查询流程状态所需参数
     * @return
     */
    TFlowState getCurrentState(Object parameter);

    /**
     * 创建工作流定义的实例，并激活该实例
     * @param definition 工作流定义（包含状态）
     * @param parameter 工作流操作参数
     */
    void create(TFlowDefinition definition, FlowActionParameter parameter);

    /**
     * 保存工作流状态
     * @param stateParam 最新的工作流状态
     * @param parameter 工作流操作参数
     */
    void saveState(TFlowState stateParam, FlowActionParameter parameter);

    //void saveCancelState(TFlowState stateParam, FlowActionParameter parameter);

}
