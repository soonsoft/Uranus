package com.soonsoft.uranus.services.approval.simple;

import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

final class ApprovalRecordHolder extends FlowActionParameter implements IPartialItemCode  {

    private String itemCode;
        private ApprovalRecord record;
        private ApprovalHistoryRecord historyRecord;

        public ApprovalRecordHolder(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
            this(record, historyRecord, null);
        }

        public ApprovalRecordHolder(ApprovalRecord record, ApprovalHistoryRecord historyRecord, String itemCode) {
            this.record = record;
            this.historyRecord = historyRecord;
            this.itemCode = itemCode;

            this.setOperator(historyRecord.getOperator());
            this.setOperatorName(historyRecord.getOperatorName());
            this.setOperateTime(historyRecord.getOperateTime());
        }

        public ApprovalRecord getRecord() {
            return record;
        }

        public ApprovalHistoryRecord getHistoryRecord() {
            return historyRecord;
        }

        @Override
        public String getItemCode() {
            return itemCode;
        }
    
}
