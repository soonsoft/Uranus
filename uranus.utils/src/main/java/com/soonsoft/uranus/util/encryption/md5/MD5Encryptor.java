package com.soonsoft.uranus.util.encryption.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public abstract class MD5Encryptor {

    private static final String MD5_ALGORITHM = "MD5";

    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return StringUtils.Empty;
        }

        return encode(data, 0, data.length);
    }

    public static String encode(byte[] data, int offset, int len) {
        if (data == null || data.length == 0) {
            return StringUtils.Empty;
        }

        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            md.update(data, offset, len);
            digest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("md5 encode occur error.", e);
        }

        if(digest != null) {
            int b;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < digest.length; i++) {
                b = digest[i];
                if (b < 0)
                    b += 256;
                if (b < 16)
                    sb.append(0);
                sb.append(Integer.toHexString(b));
            }
            return sb.toString();
        }

        return StringUtils.Empty;
    }

}
