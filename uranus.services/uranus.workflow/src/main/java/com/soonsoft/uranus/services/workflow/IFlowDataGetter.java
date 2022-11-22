package com.soonsoft.uranus.services.workflow;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public interface IFlowDataGetter {

    Object getData(FlowActionParameter parameter);
    
}
