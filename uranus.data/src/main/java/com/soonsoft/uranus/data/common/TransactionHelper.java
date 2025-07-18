package com.soonsoft.uranus.data.common;

import com.soonsoft.uranus.core.functional.action.Action0;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class TransactionHelper {

    /**
     * 注册事务提交后事件处理函数，如果当前没有事务，则直接执行。
     * 注意：如果在处理函数中开启了另一个事务，则需要显示声明事务的传播方式为开启新事务，如果使用隐式传播方式，则会导致事务失效。
     * 应当用 @Transactional(propagation = Propagation.REQUIRES_NEW) 注解来声明。
     * @param action 事务提交后执行的处理函数
     * @throws IllegalArgumentException 如果 action 参数为 null
     */
    public static void executeAfterCommit(Action0 action) {
        if(action == null) {
            throw new IllegalArgumentException("the parameter action is required.");
        }

        if(TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isSynchronizationActive()) {
            if(isTransactionComitted()) {
                TransactionCompletedActionContainer container = ensureCompletedActionContainer();
                container.addCompletionAction(action);
            } else {
                TransactionSynchronizationManager.registerSynchronization(new TransactionCommitedAction(action));
            }
        } else {
            action.apply();
        }
    }

    private static boolean isTransactionComitted() {
        for(TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            if(synchronization instanceof TransactionCommitedAction) {
                return ((TransactionCommitedAction)synchronization).isCommited();
            }
        }
        return false;
    }

    private static TransactionCompletedActionContainer ensureCompletedActionContainer() {
        for(TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            if(synchronization instanceof TransactionCompletedActionContainer) {
                return (TransactionCompletedActionContainer) synchronization;
            }
        }
        TransactionCompletedActionContainer completedAction = new TransactionCompletedActionContainer();
        TransactionSynchronizationManager.registerSynchronization(completedAction);
        return completedAction;
    }

    public static class TransactionCommitedAction implements TransactionSynchronization {

        private Action0 action;
        private boolean commited = false;

        public TransactionCommitedAction(Action0 action) {
            this.action = action;
        }

        public boolean isCommited() {
            return commited;
        }

        @Override
        public void afterCommit() {
            this.commited = true;
            if(action != null) {
                action.apply();
            }
        }
    }

    public static class TransactionCompletedActionContainer implements TransactionSynchronization {

        private final List<Action0> completedActionList = new ArrayList<>();

        public TransactionCompletedActionContainer() {
        }

        public void addCompletionAction(Action0 action) {
            if(action != null) {
                completedActionList.add(action);
            }
        }

        @Override
        public void afterCompletion(int status) {
            if(TransactionSynchronization.STATUS_COMMITTED == status) {
                completedActionList.forEach(action -> action.apply());
            }
        }
    }

    
}
