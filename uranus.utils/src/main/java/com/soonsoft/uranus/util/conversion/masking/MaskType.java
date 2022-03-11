package com.soonsoft.uranus.util.conversion.masking;

import com.soonsoft.uranus.core.functional.func.Func1;

public enum MaskType {

    ANYWAY(input -> input),
    ;

    private Func1<String, String> maskConverter;

    MaskType(Func1<String, String> converter) {
        this.maskConverter = converter;
    }

    public Func1<String, String> get() {
        return this.maskConverter;
    }
    
}
