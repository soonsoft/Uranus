package com.soonsoft.uranus.data.service;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.IDatabaseAccess;

public abstract class BaseDatabaseAccess<T> implements IDatabaseAccess<T>  {

    private T template;

    public T getTemplate() {
        return template;
    }

    public void setTemplate(T template) {
        this.template = template;
    }

    protected void ensureTemplate() {
        Guard.notNull(this.template, "the field template is required.");
    }

    protected T ensureGetTemplate() {
        ensureTemplate();
        return this.template;
    }
    
}
