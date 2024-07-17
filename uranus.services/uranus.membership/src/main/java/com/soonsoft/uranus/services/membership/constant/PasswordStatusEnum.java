package com.soonsoft.uranus.services.membership.constant;

public enum PasswordStatusEnum {
    ENABLED(1, "有效"),
    DISABLED(0, "无效"),
    ;
    
    public final int Value;
    public final String Remark;

    private PasswordStatusEnum(int value, String remark) {
        this.Value = value;
        this.Remark = remark;
    }

    public boolean eq(Integer value) {
        return value != null && value.intValue() == Value;
    }

    public static PasswordStatusEnum valueOf(int value) {
        for(PasswordStatusEnum item : values()) {
            if(item.Value == value) {
                return item;
            }
        }
        return DISABLED;
    }
}
