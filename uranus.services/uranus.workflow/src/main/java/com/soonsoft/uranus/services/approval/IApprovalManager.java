package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalManager<TApprovalQuery> {

    /** 提交审核 */
    ApprovalRecord submit(ApprovalCreateParameter parameter);

    /** 修改后再次提交审核 */
    ApprovalRecord resubmit(ApprovalParameter parameter);

    /** 撤回（撤回后对应的审核单作废） */
    ApprovalRecord revoke(ApprovalParameter parameter);

    /** 审核操作 */
    ApprovalRecord check(ApprovalCheckParameter parameter);

    /**
     * 审核操作 - 批准
     * @param recordCode 审核单编码
     * @param parameter 审核参数
     * @return 审核单信息
     */
    default ApprovalRecord approve(String recordCode, ApprovalCheckParameter parameter) {
        Guard.notNull(parameter, "the argument parameter is required.");
        parameter.setActionCode(ApprovalStateCode.Approve);
        return check(parameter);
    }

    /**
     * 审核操作 - 拒绝
     * @param recordCode 审核单编码
     * @param parameter 审核参数
     * @return 审核单信息
     */
    default ApprovalRecord deny(String recordCode, ApprovalCheckParameter parameter) {
        Guard.notNull(parameter, "the argument parameter is required.");
        parameter.setActionCode(ApprovalStateCode.Deny);
        return check(parameter);
    }

    /**
     * 取消审核
     * @param parameter 审核参数信息
     */
    void cancel(ApprovalParameter parameter);

    /**
     * 获取审批记录
     * @param recordCode 审批记录编码
     * @return 审批记录信息
     */
    ApprovalRecord getApprovalRecord(String recordCode);
    /** 返回查询对象 */
    TApprovalQuery query();

        
    /** 编制审核记录 */
    // void prepare();

    interface ApprovalStateCode {

        public static final String Approve = "Approve";
        public static final String Deny = "Deny";

    }

}
