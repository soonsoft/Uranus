package com.soonsoft.uranus.data.service.meta;

import java.util.List;

public class PrimaryKey {

    private final boolean composite;
    private final List<ColumnInfo> keys;

    public PrimaryKey(List<ColumnInfo> keys) {
        this.keys = keys;
        if(this.keys.size() > 1) {
            composite = true;
        } else {
            composite = false;
        }
    }

    public boolean isComposite() {
        return composite;
    }

    public List<ColumnInfo> getKeys() {
        return keys;
    }

    public ColumnInfo getSingleKey() {
        return keys.isEmpty() ? null : keys.get(0);
    }
    
}
