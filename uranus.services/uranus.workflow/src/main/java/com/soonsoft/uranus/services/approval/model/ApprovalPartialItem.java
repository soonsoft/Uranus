package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;

public class ApprovalPartialItem extends StateMachinePartialItem {

    /** 所属的复合节点 (CompositionNode) 编码 */
    private String nodeCode;

    /** 复合节点操作编码，多次流转到同一个复合节点时用于对应数据 */
    private String compositionActionCode;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getCompositionActionCode() {
        return compositionActionCode;
    }

    public void setCompositionActionCode(String compositionActionCode) {
        this.compositionActionCode = compositionActionCode;
    }
    
}
