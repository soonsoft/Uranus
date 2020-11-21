package com.soonsoft.uranus.util.encryption.aes;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AESEncryptor {

    private static final String AES_ALGORITHM = "AES";

    private static final String AES_ECB = "AES/ECB/PKCS5Padding";

    private static final String AES_CBC = "AES/CBC/PKCS5Padding";

    /*
     * Cipher 策略规则："算法/模式/填充模式"，默认128
     * 
     * AES/CBC/NoPadding (128) 
     * AES/CBC/PKCS5Padding (128) 
     * AES/ECB/NoPadding (128)
     * AES/ECB/PKCS5Padding (128) 
     * DES/CBC/NoPadding (56) 
     * DES/CBC/PKCS5Padding (56)
     * DES/ECB/NoPadding (56) 
     * DES/ECB/PKCS5Padding (56) 
     * DESede/CBC/NoPadding (168)
     * DESede/CBC/PKCS5Padding (168) 
     * DESede/ECB/NoPadding (168)
     * DESede/ECB/PKCS5Padding (168) 
     * RSA/ECB/PKCS1Padding (1024, 2048)
     * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
     * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
     * 
     */

    public static byte[] encrypt(byte[] data, String key) {
        return encrypt(data, toBytes(key));
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        return encodeECB(data, key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] encrypt(byte[] data, String key, String vectorKey) {
        return encrypt(data, toBytes(key), toBytes(vectorKey));
    }

    public static byte[] encrypt(byte[] data, byte[] key, byte[] vectorKey) {
        return encodeCBC(data, key, vectorKey, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] data, String key) {
        return decrypt(data, toBytes(key));
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        return encodeECB(data, key, Cipher.DECRYPT_MODE);
    }
    
    public static byte[] decrypt(byte[] data, String key, String vectorKey) {
        return decrypt(data, toBytes(key), toBytes(vectorKey));
    }

    public static byte[] decrypt(byte[] data, byte[] key, byte[] vectorKey) {
        return encodeCBC(data, key, vectorKey, Cipher.DECRYPT_MODE);
    }

    private static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length == 0;
    }

    private static void checkKey(byte[] key, String name, int multiple) {
        if(isEmpty(key)) {
            throw new IllegalArgumentException("the " + name + " is required.");
        }
        if(key.length % 8 > 0) {
            throw new IllegalArgumentException(name + " 长度必须是8的倍数");
        }
        if(key.length / 8 < multiple) {
            throw new IllegalArgumentException(name + " 长度必须大于 " + multiple * 8);
        }
    }

    private static byte[] toBytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] encodeECB(byte[] data, byte[] key, int mode) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(key, "key", 2);

        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(mode, new SecretKeySpec(key, AES_ALGORITHM));

            byte[] encryptData = cipher.doFinal(data);

            return encryptData;
        } catch(Exception e) {
            throw new IllegalStateException("decrypt by AES-ECB occur error.", e);
        }
    }

    private static byte[] encodeCBC(byte[] data, byte[] key, byte[] vectorKey, int mode) {
        if(isEmpty(data)) {
            return data;
        }

        checkKey(key, "key", 2);
        checkKey(vectorKey, "vectorKey", 2);
        if(vectorKey.length != 16) {
            throw new IllegalArgumentException("the parameter vectorKey length must be 16.");
        }

        try {
            Cipher cipher = Cipher.getInstance(AES_CBC);
            SecretKey secretKey = new SecretKeySpec(key, AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(vectorKey);

            cipher.init(mode, secretKey, iv);
            byte[] encryptData = cipher.doFinal(data);

            return encryptData;
        } catch(Exception e) {
            throw new IllegalStateException("decrypt by AES-CBC occur error.", e);
        }
    }
}
