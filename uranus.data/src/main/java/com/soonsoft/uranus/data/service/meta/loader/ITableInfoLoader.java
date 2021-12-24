package com.soonsoft.uranus.data.service.meta.loader;

import com.soonsoft.uranus.data.service.meta.TableInfo;

public interface ITableInfoLoader {

    TableInfo load(Class<?> entityClass);
    
}
