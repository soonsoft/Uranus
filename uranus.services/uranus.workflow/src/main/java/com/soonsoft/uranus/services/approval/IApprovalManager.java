package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckResult;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalManager {

    /** 提交审核 */
    ApprovalRecord submit(ApprovalCreateParameter parameter);

    /** 修改后再次提交审核 */
    ApprovalRecord resubmit();

    /** 撤回（撤回后对应的审核单作废） */
    ApprovalRecord revoke();

    /** 审核操作 */
    ApprovalCheckResult check(ApprovalCheckParameter parameter);

    /** 取消审核 */
    void cancel();

        
    /** 编制审核记录 */
    // void prepare();

    // 批准
    // void approve();

    // 拒绝
    // void deny();

}
