package com.soonsoft.uranus.services.approval.model;

public enum ApprovalActionType {
    
    /** 提交 */
    Submit,
    /** 再次提交 */
    Resubmit,
    /** 审核 */
    Check,
    /** 系统自动流转 */
    AutoFlow,
    /** 撤回 */
    Revoke,
    /** 取消 */
    Cancel,
    ;

}
