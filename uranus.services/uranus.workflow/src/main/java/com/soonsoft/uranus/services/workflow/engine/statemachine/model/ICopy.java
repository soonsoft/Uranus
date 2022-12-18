package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public interface ICopy<T> extends Cloneable {

    T copy();

}
