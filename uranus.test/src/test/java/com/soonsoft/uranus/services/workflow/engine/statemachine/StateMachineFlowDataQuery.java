package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.ArrayList;
import java.util.List;

public class StateMachineFlowDataQuery {

    List<String> queryAllCodes() {
        return new ArrayList<String>() {
            {
                add("code1");
                add("code2");
            }
        };
    }
    
}
