package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

public abstract class BaseMembershipServiceFactory<T> implements FactoryBean<T> {

    private ApplicationContext applicationContext;

    private String databaseAccessBeanName;

    private IDatabaseAccess<?> membershipDatabaseAccess;

    public BaseMembershipServiceFactory(IDatabaseAccess<?> membershipDatabaseAccess) {
        this.membershipDatabaseAccess = membershipDatabaseAccess;
    }

    public BaseMembershipServiceFactory(ApplicationContext context, String dbaBeanName) {
        this.applicationContext = context;
        this.databaseAccessBeanName = dbaBeanName;
    }

    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    protected IDatabaseAccess<?> getDatabaseAccess() {
        if(membershipDatabaseAccess != null) {
            return membershipDatabaseAccess;
        }
        return (IDatabaseAccess<?>) applicationContext.getBean(databaseAccessBeanName);
    }

}
