package com.soonsoft.uranus.data.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soonsoft.uranus.core.model.data.PagingList;

import org.junit.Assert;
import org.junit.Test;

public class PagingListTest {

    @Test
    public void test_paginglist() {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");

        PagingList<String> pagingList = new PagingList<>(list, 100);
        Assert.assertTrue(pagingList.toString().equals(list.toString()));

        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            String originalListJson = jsonMapper.writeValueAsString(list);
            String pagingListJson = jsonMapper.writeValueAsString(pagingList);

            Assert.assertTrue(originalListJson.equals(pagingListJson));
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
}
