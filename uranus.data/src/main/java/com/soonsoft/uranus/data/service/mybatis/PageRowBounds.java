package com.soonsoft.uranus.data.service.mybatis;

import org.apache.ibatis.session.RowBounds;

/**
 * PageRowBounds
 */
public class PageRowBounds extends RowBounds {

    private int total;

    public PageRowBounds(int offset, int limit) {
        super(offset, limit);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
}