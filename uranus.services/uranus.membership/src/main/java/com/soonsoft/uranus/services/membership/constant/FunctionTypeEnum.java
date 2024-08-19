package com.soonsoft.uranus.services.membership.constant;

public enum FunctionTypeEnum {

    MENU("menu", "菜单"),
    ACTION("action", "按钮"),
    WEB_API("web-api", "Web接口"),
    PAGE("page", "页面"),
    FILE("file", "文件"),
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

    public static FunctionTypeEnum byValue(String value) {
        for(FunctionTypeEnum item : values()) {
            if(item.Value.equals(value)) {
                return item;
            }
        }
        return ACTION;
    }
    
}
