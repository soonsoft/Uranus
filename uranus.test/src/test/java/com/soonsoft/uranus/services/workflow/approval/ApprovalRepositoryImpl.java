package com.soonsoft.uranus.services.workflow.approval;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalPartialItem;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;

public class ApprovalRepositoryImpl implements IApprovalRepository {

    private ApprovalRecord currentRecord;

    @Override
    public ApprovalRecord getApprovalRecord(String recordCode) {
        return currentRecord;
    }

    @Override
    public List<StateMachinePartialItem> getPartialItems(StateMachineFlowNode compositeNode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
        this.currentRecord = record;
        System.out.println("\n");
        showRecordState(record);
        if(record.getStatus() == ApprovalStatus.Checking) {
            System.out.println("\t |-> [Start Checking]");
        }
    }

    @Override
    public void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords, List<ApprovalPartialItem> partialItems) {
        if(record.currentFlowState().findFromNode() instanceof StateMachineCompositeNode) {
            showPartialItems(record, partialItems);
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

    private void showPartialItems(ApprovalRecord record, List<ApprovalPartialItem> partialItems) {
        System.out.print(
            StringUtils.format("[{0} - {1}]: {2} > ", 
                record.getApprovalType(), record.getRecordCode(),
                record.getPreviousNodeCode())
        );
        for(ApprovalPartialItem item : partialItems) {
            System.out.print(
                StringUtils.format("{0}.{1}; ", item.getItemCode(), item.getStateCode())
            );
        }
        System.out.print("\n");
    }
    
}
