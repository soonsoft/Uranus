package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalBuilder {

    ApprovalRecord build(String approvalType);
    
}
