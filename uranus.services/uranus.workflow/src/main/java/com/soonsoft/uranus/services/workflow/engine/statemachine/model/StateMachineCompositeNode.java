package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.exception.FlowException;

/**
 * 复合流程节点
 * 用于描述：会签节点、或签节点、(子流程节点)
 */
public class StateMachineCompositeNode extends StateMachineFlowNode implements IPartialItemBehavior {

    private List<StateMachinePartialItem> partialItemList;

    private final transient Func1<StateMachineCompositeNode, String> resolveStateCodeFn;

    public StateMachineCompositeNode(Func1<StateMachineCompositeNode, String> resolveStateCodeFn) {
        this.resolveStateCodeFn = resolveStateCodeFn;
    }

    @Override
    public void setNodeType(StateMachineFlowNodeType nodeType) {
        if(nodeType == StateMachineFlowNodeType.BeginNode || nodeType == StateMachineFlowNodeType.EndNode) {
            throw new FlowException("the StateMachineCompositeNode cannot be begin node or end node.");
        }
        super.setNodeType(nodeType);
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

    public String resolveAllState(String allCode) {
        return resolveState(allCode, null);
    }

    public String resolveAnyState(String anyCode) {
        return resolveState(null, anyCode);
    }

    public String resolveState(String allCode, String anyCode) {
        if(!CollectionUtils.isEmpty(partialItemList)) {
            boolean allFlag = true;
            for(StateMachinePartialItem item : partialItemList) {
                if(item.getStatus() == StateMachinePartialItemStatus.Pending) {
                    allFlag = false;
                    continue;
                }
                if(!StringUtils.isEmpty(anyCode) && anyCode.equals(item.getStateCode())) {
                    return anyCode;
                }
                if(!StringUtils.isEmpty(allCode) && !allCode.equals(item.getStateCode())) {
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
            return resolveStateCodeFn.call(this);
        }
        // 如果没有设置 resolveStateCodeFn，则参数就是确定的stateCode
        return stateCode;
    }

    @Override
    public StateMachineCompositeNode copy() {
        StateMachineCompositeNode copyCompositeNode = new StateMachineCompositeNode(this.getResolveStateCodeFn());
        copy(this, copyCompositeNode);
        return copyCompositeNode;
    }

    public static void copy(StateMachineCompositeNode source, StateMachineCompositeNode dist) {
        if(source == null || dist == null) {
            return;
        }
        StateMachineFlowNode.copy(source, dist);
        if(source.getPartialItemList() != null) {
            for(StateMachinePartialItem partialItem : source.getPartialItemList()) {
                dist.addPartialItem(partialItem.copy());
            }
        }
    }

    protected Func1<StateMachineCompositeNode, String> getResolveStateCodeFn() {
        return resolveStateCodeFn;
    }
    
}
