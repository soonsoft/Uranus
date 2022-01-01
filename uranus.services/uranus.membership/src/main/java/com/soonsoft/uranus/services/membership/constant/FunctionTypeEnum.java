package com.soonsoft.uranus.services.membership.constant;

public enum FunctionTypeEnum {

    MENU("menu", "菜单"),
    ACTION("action", "无效"),
    ;
    
    public final String Value;
    public final String Name;

    private FunctionTypeEnum(String value, String name) {
        this.Value = value;
        this.Name = name;
    }

    public boolean eq(String value) {
        return value != null && value.equals(Value);
    }
    
}
