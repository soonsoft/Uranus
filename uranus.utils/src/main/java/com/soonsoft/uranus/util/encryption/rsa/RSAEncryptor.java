package com.soonsoft.uranus.util.encryption.rsa;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * RSAEncryptor
 */
public abstract class RSAEncryptor {

    private static String RSA_ALGORITHM = "RSA";

    /**
     * 公钥加密
     * 
     * @param data 待加密的数据
     * @param publicKey 公钥
     * @return 加密后的数据（用私钥可以解密）
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(publicKey, "publicKey");

        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //初始化公钥,根据给定的编码密钥创建一个新的 X509EncodedKeySpec。
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey);
            
            // 生产公钥
            PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec);
            
            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(data);
        } catch(Exception e) {
            throw new IllegalStateException("encrypt by publicKey occur error.", e);
        }
    }

    /**
     * 公钥解密
     * 
     * @param data 待解密的数据（私钥加密的数据）
     * @param publicKey 公钥
     * @return 解密后的数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(publicKey, "publicKey");

        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //初始化公钥,密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            
            //产生公钥
            PublicKey key = keyFactory.generatePublic(x509KeySpec);
            
            //数据解密
            Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            return cipher.doFinal(data);
        } catch(Exception e) {
            throw new IllegalStateException("decrypt by publicKey occur error.", e);
        }
    }

    /**
     * 私钥加密
     * 
     * @param data 待加密的数据
     * @param privateKey 私钥
     * @return 加密后的数据（用公钥可以解密）
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(privateKey, "privateKey");

        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            
            //生成私钥
            PrivateKey key = keyFactory.generatePrivate(pkcs8KeySpec);

            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            return cipher.doFinal(data);
        } catch(Exception e) {
            throw new IllegalStateException("encrypt by privateKey occur error.", e);
        }
    }

    /**
     * 私钥解密
     * @param data 待解密的数据（公钥加密的数据）
     * @param privateKey 私钥
     * @return 解密后的数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(privateKey, "privateKey");

        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

            //生成私钥
            PrivateKey key = keyFactory.generatePrivate(pkcs8KeySpec);

            //数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(data);
        } catch(Exception e) {
            throw new IllegalStateException("decrypt by privateKey occur error.", e);
        }
    }

    private static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length == 0;
    }

    private static void checkKey(byte[] key, String name) {
        if(isEmpty(key)) {
            throw new IllegalArgumentException("the " + name + " is required.");
        }
    }
    
}