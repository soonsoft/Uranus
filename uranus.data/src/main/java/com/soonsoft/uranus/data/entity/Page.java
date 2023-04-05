package com.soonsoft.uranus.data.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 */
public class Page {

    private int pageIndex;

    private int pageSize;

    private int total;

    public Page() {
        this(1, 100);
        this.total = this.pageSize;
    }

    public Page(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.total = pageSize;
    }

    /**
     * @return the pageIndex
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * @param pageIndex the pageIndex to set
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 获取共有多少页
     */
    public int getPageCount() {
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 获取起始值
     * @return
     */
    public int offset() {
        int end = pageIndex * pageSize;
        return end - pageSize;
    }

    /**
     * 获取最大记录数
     * @return
     */
    public int limit() {
        return this.pageSize;
    }

    /**
     * 向前翻页
     */
    public boolean previous() {
        if(pageIndex == 1) {
            return false;
        }

        pageIndex--;
        return true;
    }
    
    /**
     * 向后翻页
     */
    public boolean next() {
        int pageCount = getPageCount();
        if(pageIndex == pageCount) {
            return false;
        }

        pageIndex++;
        return true;
    }

    /**
     * 获取页码按钮列表
     * @param size 生产页码按钮的数量
     */
    public int[] getNumbers(int size) {
        return getNumbers(this, size);
    }

    /**
     * 获取页码按钮列表，从1开始。
     * 最左边和最右边的分别是“第一页”和“最后一页”的页码，如果当前场景不适用，则用“0”填充。
     * @param page
     * @param size button count, since 5
     * @return size = 5, [0, 1, 2, 3, 4, 5, 0] or [1, 3, 4, 5, 6, 7, 19]
     */
    public static int[] getNumbers(Page page, int size) {
        if(page == null) {
            throw new IllegalArgumentException("the arguments page is null.");
        }
        if(size < 5) {
            size = 5;
        }
        
        int ex = (size - 1) / 2;
        int start = page.getPageIndex() - ex;
        start = start < 1 ? 1 : start;
        int end = start + size - 1;
        int pageCount = page.getPageCount();
        end = end > pageCount ? pageCount : end;

        int length = end - start + 1;
        if(length < size) {
            if((end - (size - 1)) > 0) {
                start = end - (size - 1);
            } else {
                start = 1;
            }
        }

        int[] numbers = new int[length];
        for(int i = 0; i < length; i++) {
            numbers[i] = start++;
        }
        return numbers;
    }

    /**
     * 列表分页，List<T> > List<T>[]
     * @param <T> 类型
     * @param list 原始数组
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T>[] pagingList(List<T> list, int pageSize) {
        if(list == null) {
            return null;
        }

        Page page = new Page(1, pageSize);
        page.setTotal(list.size());

        List<T>[] arr = new List[page.getPageCount()];
        
        for(int i = 0; i < arr.length; i++) {
            int fromIndex = page.offset();
            int toIndex = fromIndex + page.getPageSize();
            toIndex = toIndex > page.getTotal() ? page.getTotal() : toIndex;
            arr[i] = new ArrayList<>(list.subList(fromIndex, toIndex));
            if(!page.next()) {
                break;
            }
        }
        return arr;
    }
}