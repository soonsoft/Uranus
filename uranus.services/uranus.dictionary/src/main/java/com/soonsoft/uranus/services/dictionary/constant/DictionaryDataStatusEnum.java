package com.soonsoft.uranus.services.dictionary.constant;

public enum DictionaryDataStatusEnum {
    
    Enabled(1, "有效"),
    Approving(2, "审批中"),
    History(3, "历史数据"),
    Disabled(4, "禁用"),
    Draft(99, "草稿"),
    ;
    
    public final int Value;
    public final String Remark;

    private DictionaryDataStatusEnum(int value, String remark) {
        this.Value = value;
        this.Remark = remark;
    }

    public boolean eq(Integer value) {
        return value != null && value.intValue() == Value;
    }

    public static DictionaryDataStatusEnum valueOf(int value) {
        for(DictionaryDataStatusEnum item : values()) {
            if(item.Value == value) {
                return item;
            }
        }
        return Disabled;
    }
}
