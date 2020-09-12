package com.soonsoft.uranus.core.common.extension;


public class TestServiceImpl2 implements ITestService {

    @Override
    public String action() {
        return this.getClass().getSimpleName();
    }
}