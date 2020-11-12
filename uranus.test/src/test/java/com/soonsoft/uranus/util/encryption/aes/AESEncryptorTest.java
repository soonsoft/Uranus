package com.soonsoft.uranus.util.encryption.aes;

import org.junit.Assert;
import org.junit.Test;

public class AESEncryptorTest {

    @Test
    public void test_AES() {
        String str = "123456中文字符English";
        String key = "1234567812345678";
        String vectorKey = "abcdefghijklmnop";
        
        byte[] ecbEncode = AESEncryptor.encrypt(str.getBytes(), key.getBytes());
        byte[] ecbDecode = AESEncryptor.decrypt(ecbEncode, key.getBytes());

        Assert.assertTrue(str.equals(new String(ecbDecode)));

        byte[] cbcEncode = AESEncryptor.encrypt(str.getBytes(), key.getBytes(), vectorKey.getBytes());
        byte[] cbcDecode = AESEncryptor.decrypt(cbcEncode, key.getBytes(), vectorKey.getBytes());

        Assert.assertTrue(str.equals(new String(cbcDecode)));
    }
    
}
