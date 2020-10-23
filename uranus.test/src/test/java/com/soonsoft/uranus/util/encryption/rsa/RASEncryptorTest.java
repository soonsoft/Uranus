package com.soonsoft.uranus.util.encryption.rsa;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.junit.Test;
import org.junit.Assert;

public class RASEncryptorTest {

    private static String password = "123456";

    @Test
    public void test_RAS() throws UnsupportedEncodingException {
        KeyStore keyStore = KeyStore.create();

        System.out.println("公钥：" + Base64.getEncoder().encodeToString(keyStore.getPublicKey()));
        System.out.println("私钥：" + Base64.getEncoder().encodeToString(keyStore.getPrivateKey()));
    
        byte[] encryptData = RSAEncryptor.encryptByPublicKey(password.getBytes("utf-8"), keyStore.getPublicKey());
        byte[] decryptData = RSAEncryptor.decryptByPrivateKey(encryptData, keyStore.getPrivateKey());

        String decryptDataText = new String(decryptData, "utf-8");
        System.out.println("解密：" + decryptDataText);
        Assert.assertTrue(decryptDataText.equals(password));

        String newPassword = password + "78";
        encryptData = RSAEncryptor.encryptByPrivateKey(newPassword.getBytes("utf-8"), keyStore.getPrivateKey());
        decryptData = RSAEncryptor.decryptByPublicKey(encryptData, keyStore.getPublicKey());

        decryptDataText = new String(decryptData, "utf-8");
        System.out.println("解密：" + decryptDataText);
        Assert.assertTrue(decryptDataText.equals(newPassword));
    }
    
}
