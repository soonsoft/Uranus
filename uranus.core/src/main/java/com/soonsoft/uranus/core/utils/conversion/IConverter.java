package com.soonsoft.uranus.core.utils.conversion;

import com.soonsoft.uranus.core.functional.func.Func1;

public interface IConverter<InType, OutType> {

    Func1<InType, OutType> get();
    
}
