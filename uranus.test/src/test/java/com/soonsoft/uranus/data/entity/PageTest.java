package com.soonsoft.uranus.data.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * PageTest
 */
public class PageTest {

    @Test
    public void test_offsetAndLimit() {
        Page page = new Page();
        page.setTotal(9988);

        Assert.assertTrue(page.offset() == 0);
        Assert.assertTrue(page.limit() == page.getPageSize());

        page.next();
        Assert.assertTrue(page.offset() == 100);

        page.next();
        Assert.assertTrue(page.offset() == 200);

        page.previous();
        page.previous();
        Assert.assertTrue(page.offset() == 0);
        Assert.assertTrue(!page.previous());

    }

    @Test
    public void test_getNumbers() {
        Page page = new Page(1, 30);
        page.setTotal(100);

        int[] numbers = page.getNumbers(5);
        Assert.assertTrue(numbers.length == 4);

        page.setTotal(9999);
        page.setPageIndex(4);
        numbers = page.getNumbers(5);
        Assert.assertTrue(numbers.length == 5);
        Assert.assertTrue(numbers[0] == 2);
        Assert.assertTrue(numbers[numbers.length - 1] == 6);
    }

    @Test
    public void test_pagingList() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            list.add(Integer.valueOf(i));
        }

        List<Integer>[] result = Page.pagingList(list, 5);
        Assert.assertTrue(result.length == 20);

    }

}