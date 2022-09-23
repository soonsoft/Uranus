package com.soonsoft.uranus.services.workflow.engine.linear;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.linear.model.FlowActionState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

/**
 * 线性流程引擎（单路径）
 */
public class LinearFlowEngine<TFlowQuery> 
                extends BaseFlowEngine<
                    FlowDefinition<FlowNode<FlowActionState>>, 
                    IFlowRepository<FlowActionState>,
                    TFlowQuery
                > {

    public LinearFlowEngine(FlowDefinition<FlowNode<FlowActionState>> definition) {
        super(definition);
    }

    public LinearFlowEngine(FlowDefinition<FlowNode<FlowActionState>> definition, TFlowQuery query) {
        super(definition, query);
    }

    @Override
    public void start(FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StateMachineFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cancel(FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StateMachineFlowState currentState() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
