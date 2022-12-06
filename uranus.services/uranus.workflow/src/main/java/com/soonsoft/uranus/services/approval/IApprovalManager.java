package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;

public interface IApprovalManager<TApprovalQuery> {

    /**
     * 提交审核
     * @param parameter 提交参数
     * @return 审核记录
     */
    ApprovalRecord submit(ApprovalCreateParameter parameter);

    /**
     * 修改后再次提交审核
     * @param parameter 提交参数
     * @return 审核记录
     */
    ApprovalRecord resubmit(ApprovalParameter parameter);

    /**
     * 撤回（当前节点未审批之前，前一个节点可以将流程倒回）
     * @param previousNodeCode 发起撤回操作的节点
     * @param parameter 撤回参数
     * @return 审核记录
     */
    ApprovalRecord revoke(String previousNodeCode, ApprovalParameter parameter);

    /**
     * 审核操作 - 批准 or 拒绝
     * @param parameter 审核参数
     * @return 审核记录
     */
    ApprovalRecord check(ApprovalCheckParameter parameter);

    /**
     * 审核操作 - 批准
     * @param recordCode 审核单编码
     * @param parameter 审核参数
     * @return 审核单信息
     */
    default ApprovalRecord approve(ApprovalCheckParameter parameter) {
        Guard.notNull(parameter, "the argument parameter is required.");
        parameter.setStateCode(ApprovalStateCode.Approved);
        return check(parameter);
    }

    /**
     * 审核操作 - 拒绝
     * @param recordCode 审核单编码
     * @param parameter 审核参数
     * @return 审核单信息
     */
    default ApprovalRecord deny(ApprovalCheckParameter parameter) {
        Guard.notNull(parameter, "the argument parameter is required.");
        parameter.setStateCode(ApprovalStateCode.Denied);
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

    /**
     * 返回查询对象
     * @return
     */
    TApprovalQuery query();

}
