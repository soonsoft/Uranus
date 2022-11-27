package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IForEach;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;

public interface IPartialItemBehavior extends IForEach<StateMachinePartialItem> {

    /**
     * 返回所有的 StateMachinePartialItem
     * @return 可能为 null
     */
    List<StateMachinePartialItem> getPartialItemList();

    /**
     * 更新部分项目中的状态（stateCode），并将status设置为 Completed
     * @param itemCode itemCode 可以是 compositeNode的itemCode也可以是parallelNode中的nodeCode
     * @param stateCode 状态码
     * @return 返回被更新的StateMachinePartialItem对象，如果没有匹配到则返回 null
     */
    default StateMachinePartialItem updatePartialItemState(String itemCode, String stateCode) {
        final List<StateMachinePartialItem> partialItemList = getPartialItemList();
        if(!CollectionUtils.isEmpty(partialItemList)) {
            for(StateMachinePartialItem item : partialItemList) {
                if(item.getItemCode().equals(itemCode)) {
                    item.setStateCode(stateCode);
                    item.setStatus(StateMachinePartialItemStatus.Completed);
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 批量设置item的状态，会更新 id, stateCode, status
     * @param allState 带有状态的 partialItemList
     */
    default void setAllState(List<StateMachinePartialItem> allState) {
        if(!CollectionUtils.isEmpty(allState)) {
            for(StateMachinePartialItem i : allState) {
                forEach((partialItem, index, behavior) -> {
                    if(i.getItemCode().equals(partialItem.getItemCode())) {
                        partialItem.setId(i.getId());
                        partialItem.setStateCode(i.getStateCode());
                        partialItem.setStatus(i.getStatus());
                        behavior.setBreak();
                    }
                });
            }
            
        }
    }

    /**
     * 循环处理
     * @param action 循环处理函数，可以通过 ForEachBehavior 实现break操作
     */
    default void forEach(Action3<StateMachinePartialItem, Integer, ForEachBehavior> action) {
        Guard.notNull(action, "the parameter action is required.");

        final List<StateMachinePartialItem> partialItemList = getPartialItemList();
        if(!CollectionUtils.isEmpty(partialItemList)) {
            ForEachBehavior behavior = new ForEachBehavior();
            int index = 0;
            for(StateMachinePartialItem item : partialItemList) {
                behavior.reset();

                action.apply(item, index, behavior);
                if(behavior.isBreak()) {
                    break;
                }

                index++;
            }
        }
    }

    default boolean allMatch(Predicate1<StateMachinePartialItem> predicate) {
        final boolean[] flag = new boolean[] { true };
        forEach((i, idx, b) -> {
            if(!predicate.test(i)) {
                flag[0] = false;
                b.setBreak();
            }
        });
        return flag[0];
    }

    default boolean anyMatch(Predicate1<StateMachinePartialItem> predicate) {
        final boolean[] flag = new boolean[] { false };
        forEach((i, idx, b) -> {
            if(predicate.test(i)) {
                flag[0] = true;
                b.setBreak();
            }
        });
        return flag[0];
    }
    
}
