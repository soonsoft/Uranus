package com.soonsoft.uranus.util.file;

import org.junit.Assert;
import org.junit.Test;

public class FileToolsTest {

    @Test
    public void test_FileCreate() {

        String filename = "/Users/zhousong/aaa/bbb/ccc.txt";
        FileTools.createFile(filename);

        Assert.assertTrue(FileTools.exists(filename));

        FileTools.delete(filename);

        Assert.assertFalse(FileTools.exists(filename));

    }
    
}
