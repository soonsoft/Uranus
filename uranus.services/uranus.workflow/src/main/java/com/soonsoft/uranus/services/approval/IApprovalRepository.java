package com.soonsoft.uranus.services.approval;

import java.util.List;

import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;

public interface IApprovalRepository {

    /**
     * 根据审核单编号获取审核纪录
     * <p>
     *   注意：ApprovalRecord 中持有了 StateMachineFlowState 对象，该对象持有了当前审核节点的状态信心<br>
     * </p>
     * <ul>
     *   <li>nodeCode: 上一个审核节点</li>
     *   <li>stateCode: 上一个状态</li>
     *   <li>toNodeCode： 当前节点</li>
     * </ul>
     * @param recordCode 审核单编号
     * @return 审核纪录
     */
    ApprovalRecord getApprovalRecord(String recordCode);

    /**
     * 会签与或签审节点获取该节点下所有 “部分项” 的状态
     * @param recordCode 审核记录编号
     * @param nodeCode 节点编号
     * @param currentNodeMark 当前节点标记
     * @return 节点下 “部分项” 相关的历史纪录列表
     */
    List<ApprovalHistoryRecord> getPartialApprovalHistoryRecords(String recordCode, String nodeCode, String currentNodeMark);

    /**
     * 创建审核单
     * @param record 审核纪录
     * @param historyRecord 审核历史纪录
     */
    void create(ApprovalRecord record, ApprovalHistoryRecord historyRecord);

    /**
     * 保存审核数据
     * @param record 审核纪录
     * @param historyRecords 审核历史纪录（如遇到并行节点或分支节点时，审核纪录可能是多条）
     * @param partialItems 会签、或签时会有的部分结果
     */
    void saveActionState(ApprovalRecord record, List<ApprovalHistoryRecord> historyRecords);

    /**
     * 保存取消审核纪录
     * @param record 审核纪录
     * @param historyRecord 审核历史纪录
     */
    void saveCancelState(ApprovalRecord record, ApprovalHistoryRecord historyRecord);
    
}
