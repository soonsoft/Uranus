package com.soonsoft.uranus.services.membership.constant;

public enum PasswordTypeEnum {
    NORMAL(1, "普通密码"),
    INITIAL(2, "初始密码"),
    ;
    
    public final int Value;
    public final String Remark;

    private PasswordTypeEnum(int value, String remark) {
        this.Value = value;
        this.Remark = remark;
    }

    public boolean eq(Integer value) {
        return value != null && value.intValue() == Value;
    }

    public static PasswordTypeEnum valueOf(int value) {
        for(PasswordTypeEnum item : values()) {
            if(item.Value == value) {
                return item;
            }
        }
        return NORMAL;
    }
}
