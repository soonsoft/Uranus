package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

/* 
    继承 Serializable 的 lambda 表达式将支持序列化
        序列化：通过 lambda 中的writeReplace方法得到 SerializedLambda 对象；
        反序列化：通过 SerializedLambda 对象中的 readResolve 方法得到原始 lambda。
*/ 
@FunctionalInterface
public interface Func0<R> extends Serializable {
    
    R call();

}
