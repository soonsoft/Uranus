package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.functional.func.Func1;

/**
 * 复合流程节点
 * 用于描述：会签节点、或签节点、子流程节点
 */
public class StateMachineCompositeNode extends StateMachineFlowNode {

    private List<StateMachinePartialItem> partialItemList;

    private final Func1<StateMachineCompositeNode, String> resolveStateCodeFn;

    public StateMachineCompositeNode(Func1<StateMachineCompositeNode, String> resolveStateCodeFn) {
        this.resolveStateCodeFn = resolveStateCodeFn;
    }

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

    public Func1<StateMachineCompositeNode, String> getResolveStateCodeFn() {
        return resolveStateCodeFn;
    }

    public StateMachinePartialItem updatePartialItemState(String partialItemCode, String stateCode) {
        if(!CollectionUtils.isEmpty(partialItemList)) {
            for(StateMachinePartialItem item : partialItemList) {
                if(item.getItemCode().equals(partialItemCode)) {
                    item.setStateCode(stateCode);
                    return item;
                }
            }
        }
        return null;
    }

    public String resolveStateCode(String stateCode) {
        if(getResolveStateCodeFn() != null) {
            return getResolveStateCodeFn().call(this);
        }
        return stateCode;
    }
    
}
