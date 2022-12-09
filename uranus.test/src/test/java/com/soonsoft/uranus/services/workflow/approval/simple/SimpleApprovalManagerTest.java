package com.soonsoft.uranus.services.workflow.approval.simple;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.services.approval.ApprovalManagerFactory;
import com.soonsoft.uranus.services.approval.IApprovalManager;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalData;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.workflow.approval.ApprovalRepositoryImpl;

public class SimpleApprovalManagerTest {

    @Test
    public void test_normalApprove() {
        ApprovalManagerFactory<SimpleApprovalQuery> factory = new ApprovalManagerFactory<>();
        SimpleApprovalQuery query = new SimpleApprovalQuery();
        IApprovalRepository repository = new ApprovalRepositoryImpl();
        IApprovalManager<SimpleApprovalQuery> manager = 
            factory.createManager(
                query, repository, 
                factory.definitionContainer()
                    .definition("开户审核", "CreateAccount", "开户", true)
                        .begin()
                        .next("KYC审核", "KYC审核")
                        .next("合规审核", "合规审核")
                        .next("RO复核", "RO复核")
                        .end()
                    .definition("信息变更", "EditAccountInfo", "KYC信息更新", false)
                        .begin()
                        .next("KYC复核", "KYC复核")
                        .end()
            );

        ApprovalCreateParameter submitParam = new ApprovalCreateParameter();
        submitParam.setSource("LP");
        submitParam.setApprovalType("开户审核");
        submitParam.setOperator("制单人");
        submitParam.setBusinessCode("Account");
        submitParam.setEntityCode("LP0032");
        ApprovalData approvalData = new ApprovalData();
        approvalData.setTargetId(1);
        approvalData.setTargetType("AccountIdentity");
        submitParam.addApprovalData(approvalData);
        ApprovalRecord record = manager.submit(submitParam);

        Assert.assertNotNull(record);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));
    }
    
}
