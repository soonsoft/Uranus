package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.functional.func.Func1;

/**
 * 复合流程节点
 * 用于描述：会签节点、或签节点、(子流程节点)
 */
public class StateMachineCompositeNode extends StateMachineFlowNode implements IPartialItemBehavior {

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

    public String resolveState(String allCode, String anyCode) {
        if(!CollectionUtils.isEmpty(partialItemList)) {
            boolean allFlag = true;
            for(StateMachinePartialItem item : partialItemList) {
                if(item.getStatus() == StateMachinePartialItemStatus.Pending) {
                    allFlag = false;
                    continue;
                }
                if(anyCode.equals(item.getStateCode())) {
                    return anyCode;
                }

                if(!allCode.equals(item.getStateCode())) {
                    allFlag = false;
                    break;
                }
            }
            if(allFlag) {
                return allCode;
            }
        }
        return null;
    }

    public String resolveStateCode(String stateCode) {
        if(resolveStateCodeFn != null) {
            String resolveStateCode = resolveStateCodeFn.call(this);
            // 一旦返回确定的StateCode，就将剩余没有处理的节点全部取消掉
            if(resolveStateCode != null) {
                forEach((item, index, behavior) -> {
                    if(item.getStatus() == StateMachinePartialItemStatus.Pending) {
                        item.setStatus(StateMachinePartialItemStatus.Terminated);
                    }
                });
            }
            return resolveStateCode;
        }
        return stateCode;
    }
    
}
