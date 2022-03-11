package com.soonsoft.uranus.util.conversion.formatting;

import com.soonsoft.uranus.core.functional.func.Func1;

public enum FormatType {

    NOTHING(input -> input),
    ;

    private Func1<String, String> formatter;

    FormatType(Func1<String, String> formatter) {
        this.formatter = formatter;
    }

    public Func1<String, String> get() {
        return this.formatter;
    }
    
}
