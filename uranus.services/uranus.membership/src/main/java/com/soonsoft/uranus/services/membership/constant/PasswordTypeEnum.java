package com.soonsoft.uranus.services.membership.constant;

public enum PasswordTypeEnum {
    NORMAL(1, "普通密码"),
    INITIAL(2, "初始密码"),
    ;
    
    public final int Value;
    public final String Name;

    private PasswordTypeEnum(int value, String name) {
        this.Value = value;
        this.Name = name;
    }

    public boolean eq(Integer value) {
        return value != null && value.intValue() == Value;
    }
}
