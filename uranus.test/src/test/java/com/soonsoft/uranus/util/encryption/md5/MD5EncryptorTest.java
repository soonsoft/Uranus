package com.soonsoft.uranus.util.encryption.md5;

import org.junit.Test;
import org.junit.Assert;

public class MD5EncryptorTest {

    @Test
    public void test_encode() {
        byte[] data = "123456简单密码".getBytes();

        String encodeText1 = MD5Encryptor.encode(data);
        String encodeText2 = MD5Encryptor.encode(data, 0, data.length);

        System.out.println(encodeText1);

        Assert.assertTrue(encodeText1.equals(encodeText2));
    }
    
}
