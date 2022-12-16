package com.soonsoft.uranus.services.approval;

import java.util.List;

import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalRepository {

    ApprovalRecord getApprovalRecord(String recordCode);

    void create(ApprovalRecord record, ApprovalHistoryRecord historyRecord);

    void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords);

    void saveCancelState(ApprovalRecord record, ApprovalHistoryRecord historyRecord);
    
}
