package com.soonsoft.uranus.data.common;

import com.soonsoft.uranus.core.functional.action.Action0;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class TransactionHelper {

    public static void executeAfterCommit(Action0 action) {
        if(action == null) {
            throw new IllegalArgumentException("the parameter action is required.");
        }

        if(TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.apply();
                }
            });
            return;
        }

        action.apply();
    }

    
}
