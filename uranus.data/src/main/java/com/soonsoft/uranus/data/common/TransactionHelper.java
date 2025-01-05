package com.soonsoft.uranus.data.common;

import com.soonsoft.uranus.core.functional.action.Action0;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class TransactionHelper {

    public static void executeAfterCommit(Action0 action) {
        if(action == null) {
            throw new IllegalArgumentException("the parameter action is required.");
        }

        if(TransactionSynchronizationManager.isActualTransactionActive()) {
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
        if(TransactionSynchronizationManager.isSynchronizationActive()) {
            for(TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
                if(synchronization instanceof TransactionCommitedAction) {
                    return ((TransactionCommitedAction)synchronization).isCommited();
                }
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
