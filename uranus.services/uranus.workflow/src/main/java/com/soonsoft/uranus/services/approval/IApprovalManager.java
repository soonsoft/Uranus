package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckResult;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public interface IApprovalManager<TApprovalQuery> {

    /** 提交审核 */
    ApprovalRecord submit(ApprovalCreateParameter parameter);

    /** 修改后再次提交审核 */
    ApprovalRecord resubmit();

    /** 撤回（撤回后对应的审核单作废） */
    ApprovalRecord revoke();

    /** 审核操作 */
    ApprovalCheckResult check(ApprovalCheckParameter parameter);

    default ApprovalCheckResult approve(String approvalRecord, FlowActionParameter parameter) {
        return approve(approvalRecord, null, parameter);
    }

    /** 批准 */
    default ApprovalCheckResult approve(String recordCode, String remark, FlowActionParameter parameter) {
        Guard.notEmpty(recordCode, "the argument recordCode is required.");

        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(recordCode);
        checkParameter.setRemark(remark);
        checkParameter.setActionCode(ActionType.Approve);
        if(parameter != null) {
            checkParameter.setOperator(parameter.getOperator());
            checkParameter.setOperatorName(parameter.getOperatorName());
            checkParameter.setOperateTime(parameter.getOperateTime());
        }
        return check(checkParameter);
    }

    /** 拒绝 */
    default ApprovalCheckResult deny(String recordCode, String remark, FlowActionParameter parameter) {
        Guard.notEmpty(recordCode, "the arguments recordCode is required.");
        Guard.notEmpty(remark, "the arguments remark is required.");

        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(recordCode);
        checkParameter.setRemark(remark);
        checkParameter.setActionCode(ActionType.Deny);
        if(parameter != null) {
            checkParameter.setOperator(parameter.getOperator());
            checkParameter.setOperatorName(parameter.getOperatorName());
            checkParameter.setOperateTime(parameter.getOperateTime());
        }
        return check(checkParameter);
    }

    /** 取消审核 */
    void cancel();

    /** 返回查询对象 */
    TApprovalQuery query();

        
    /** 编制审核记录 */
    // void prepare();

    interface ActionType {

        public static final String Submit = "Submit";
        public static final String Approve = "Approve";
        public static final String Deny = "Deny";

    }

}
