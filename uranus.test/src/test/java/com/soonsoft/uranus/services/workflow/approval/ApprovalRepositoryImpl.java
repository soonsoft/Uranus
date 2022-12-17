package com.soonsoft.uranus.services.workflow.approval;

import java.util.List;

import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public class ApprovalRepositoryImpl implements IApprovalRepository {

    private ApprovalRecord currentRecord;

    @Override
    public ApprovalRecord getApprovalRecord(String recordCode) {
        return currentRecord;
    }

    @Override
    public void create(ApprovalRecord record) {
        this.currentRecord = record;
    }

    @Override
    public void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveCancelState(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        // TODO Auto-generated method stub
        
    }
    
}
