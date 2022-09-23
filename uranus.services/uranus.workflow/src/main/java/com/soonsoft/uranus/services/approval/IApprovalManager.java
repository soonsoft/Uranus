package com.soonsoft.uranus.services.approval;

public interface IApprovalManager {
    
    // 编制单据
    void prepare();

    // 审核
    void check();

    // 批准
    void approve();

    // 拒绝
    void deny();

}
