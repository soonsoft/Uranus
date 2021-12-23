package com.soonsoft.uranus.data.service.mate;

public enum IDType {
    
    NONE(0),
    AUTO(1),
    INPUT(2),
    ;


    private final int typeValue;

    private IDType(int value) {
        typeValue = value;
    }

    public int getTypeValue() {
        return typeValue;
    }
    
}
