package com.soonsoft.uranus.data.service.mate.loader;

import com.soonsoft.uranus.data.service.mate.TableInfo;

public interface ITableInfoLoader {

    TableInfo load(Class<?> entityClass);
    
}
