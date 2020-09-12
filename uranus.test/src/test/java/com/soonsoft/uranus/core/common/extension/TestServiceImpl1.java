package com.soonsoft.uranus.core.common.extension;

public class TestServiceImpl1 implements ITestService {

    @Override
    public String action() {
        return this.getClass().getSimpleName();
    }
}