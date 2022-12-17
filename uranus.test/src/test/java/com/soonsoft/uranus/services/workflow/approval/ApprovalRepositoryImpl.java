package com.soonsoft.uranus.services.workflow.approval;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
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
    public void create(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        this.currentRecord = record;
        showRecordState(record);
    }

    @Override
    public void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords) {
        showRecordState(record);
    }

    @Override
    public void saveCancelState(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        showRecordState(record);
    }

    private void showRecordState(ApprovalRecord record) {
        System.out.println(
            StringUtils.format(
                "[{0} - {1}]: {2}.{3} > {4}", 
                record.getApprovalType(), record.getRecordCode(),
                record.getFlowState().getNodeCode(), record.getFlowState().getStateCode(),
                record.getFlowState().getToNodeCode()
            )
        );
    }
    
}
