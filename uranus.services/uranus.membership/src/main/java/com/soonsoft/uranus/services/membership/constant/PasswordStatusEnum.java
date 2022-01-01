package com.soonsoft.uranus.services.membership.constant;

public enum PasswordStatusEnum {
    ENABLED(1, "有效"),
    DISABLED(0, "无效"),
    ;
    
    public final int Value;
    public final String Name;

    private PasswordStatusEnum(int value, String name) {
        this.Value = value;
        this.Name = name;
    }

    public boolean eq(Integer value) {
        return value != null && value.intValue() == Value;
    }
}
