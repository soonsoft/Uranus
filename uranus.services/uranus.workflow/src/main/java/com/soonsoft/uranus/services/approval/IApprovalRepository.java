package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalRepository {

    ApprovalRecord getApprovalRecord(String recordCode);

    void create(ApprovalRecord record);

    void saveChecking(ApprovalRecord record, ApprovalHistoryRecord historyRecord);
    
}
