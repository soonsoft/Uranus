package com.soonsoft.uranus.services.approval.model;

public interface ApprovalStateCode {

    /** 已提交 - 由操作类型 [Submit] 或 [Resubmit] 得到 */
    String Checking = "Checking";
    /** 已批准 - 由操作类型 [Approve] 得到 */
    String Approved = "Approved";
    /** 已拒绝 - 由操作类型 [Deny] 得到 */
    String Denied = "Denied";
    /** 被撤回 - 由操作类型 [Revoke] 得到 */
    String Revoked = "Revoked";
    /** 下一步 - 由操作类型 [AutoFlow] 得到 */
    String Next = "Next";
    
}
