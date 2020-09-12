package com.soonsoft.uranus.util.identity;

import org.junit.Test;

/**
 * IDTest
 */
public class IDTest {

    @Test
    public void test_newID() {
        for(int i = 0; i < 100; i++) {
            System.out.println(ID.newID());
        }
    }

    @Test
    public void test_parseID() {
        System.out.println(ID.getGenerator().parse(17666590216556549L));
    }
    
}