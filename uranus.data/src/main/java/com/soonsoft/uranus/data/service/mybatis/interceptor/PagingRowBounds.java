package com.soonsoft.uranus.data.service.mybatis.interceptor;

import org.apache.ibatis.session.RowBounds;

public class PagingRowBounds extends RowBounds {

    private int total;

    public PagingRowBounds(int offset, int limit) {
        super(offset, limit);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
}