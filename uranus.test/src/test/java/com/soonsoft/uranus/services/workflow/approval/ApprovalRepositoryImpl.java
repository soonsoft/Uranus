package com.soonsoft.uranus.services.workflow.approval;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;

public class ApprovalRepositoryImpl implements IApprovalRepository {

    private ApprovalRecord currentRecord;
    private List<ApprovalHistoryRecord> historyRecordList;

    @Override
    public ApprovalRecord getApprovalRecord(String recordCode) {
        return currentRecord;
    }

    @Override
    public List<ApprovalHistoryRecord> getPartialApprovalHistoryRecords(String recordCode, String nodeCode,
            String currentNodeMark) {
        return 
            historyRecordList.stream()
                .filter(historyRecord -> currentNodeMark.equals(historyRecord.getCurrentNodeMark()))
                .toList();
    }

    @Override
    public void create(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        this.currentRecord = record;
        this.historyRecordList = new ArrayList<>();
        saveHistoryRecord(historyRecord);

        System.out.println("\n");
        showRecordState(record);
        if(record.getStatus() == ApprovalStatus.Checking) {
            System.out.println("\t |-> [Start Checking]");
        }
    }

    @Override
    public void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords) {
        for(ApprovalHistoryRecord historyRecord : historyRecords) {
            saveHistoryRecord(historyRecord);
        }

        if(record.getFlowState().findFromNode() instanceof StateMachineCompositeNode) {
            showPartialItems(record, historyRecords);
        } else {
            showRecordState(record);
        }
        if(record.getStatus() == ApprovalStatus.Completed) {
            System.out.println("\t |-> [Completed]");
        }
    }

    @Override
    public void saveCancelState(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        showRecordState(record);
        if(record.getStatus() == ApprovalStatus.Canceled) {
            System.out.println("\t |-> [Canceled]");
        }
    }

    private void saveHistoryRecord(ApprovalHistoryRecord historyRecord) {
        historyRecord.setId(historyRecordList.size() + 1);
        historyRecordList.add(historyRecord);
    }

    private void showRecordState(ApprovalRecord record) {
        System.out.println(
            StringUtils.format(
                "[{0} - {1}]: {2}.{3} > {4}", 
                record.getApprovalType(), record.getRecordCode(),
                record.getPreviousNodeCode(), record.getPreviousStateCode(),
                record.getCurrentNodeCode()
            )
        );
    }

    private void showPartialItems(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords) {
        System.out.print(
            StringUtils.format("[{0} - {1}]: {2} > ", 
                record.getApprovalType(), record.getRecordCode(),
                record.getPreviousNodeCode())
        );
        for(ApprovalHistoryRecord item : historyRecords) {
            if(!StringUtils.isEmpty(item.getCurrentNodeMark())) {
                System.out.print(
                    StringUtils.format("{0}.{1}; ", item.getItemCode(), item.getStateCode())
                );
            }
        }
        System.out.print("\n");
    }
    
}
