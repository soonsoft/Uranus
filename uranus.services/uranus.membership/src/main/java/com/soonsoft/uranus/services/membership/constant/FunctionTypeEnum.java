package com.soonsoft.uranus.services.membership.constant;

public enum FunctionTypeEnum {

    MENU("menu", "菜单"),
    ACTION("action", "按钮"),
    ;
    
    public final String Value;
    public final String Remark;

    private FunctionTypeEnum(String value, String remark) {
        this.Value = value;
        this.Remark = remark;
    }

    public boolean eq(String value) {
        return value != null && value.equals(Value);
    }
    
}
