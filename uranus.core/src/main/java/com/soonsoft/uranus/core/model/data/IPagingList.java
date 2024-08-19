package com.soonsoft.uranus.core.model.data;

import java.util.List;

public interface IPagingList<T> extends List<T> {

    Integer getPageSize();

    Integer getPageIndex();

    Integer getPageTotal();
    
}
