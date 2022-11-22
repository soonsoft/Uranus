package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.functional.func.Func3;

/**
 * 复合流程节点
 * 用于描述：会签节点、或签节点、子流程节点
 */
public class StateMachineCompositeNode extends StateMachineFlowNode {

    private List<StateMachinePartialItem> partialItemList;

    private Func3<String, String, StateMachineCompositeNode, String> resolveStateCodeFn;

    public List<StateMachinePartialItem> getPartialItemList() {
        return partialItemList;
    }
    public void setPartialItemList(List<StateMachinePartialItem> partialItemList) {
        this.partialItemList = partialItemList;
    }
    public boolean addPartialItem(StateMachinePartialItem partialItem) {
        if(partialItem != null) {
            if(partialItemList == null) {
                partialItemList = new ArrayList<>();
            }
            return partialItemList.add(partialItem);
        }
        return false;
    }

    public Func3<String, String, StateMachineCompositeNode, String> getResolveStateCodeFn() {
        return resolveStateCodeFn;
    }
    public void setResolveStateCodeFn(Func3<String, String, StateMachineCompositeNode, String> resolveStateCodeFn) {
        this.resolveStateCodeFn = resolveStateCodeFn;
    }

    public String resolveStateCode(String stateCode, String partialItemCode) {
        if(getResolveStateCodeFn() != null) {
            return getResolveStateCodeFn().call(stateCode, partialItemCode, this);
        }
        return stateCode;
    }
    
}
