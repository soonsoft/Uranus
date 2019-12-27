package com.soonsoft.uranus.core.common.lang;

/**
 * StringUtils
 */
public abstract class StringUtils {

    public static boolean isEmpty(CharSequence input) {
        return input == null || input.length() == 0;
    }

    public static boolean isBlank(CharSequence input) {
        int len;
        if(input == null || (len = input.length()) == 0) {
            return true;
        }

        for(int i = 0; i < len; i++) {
            if(!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String trim(String input) {
        return input == null ? null : input.trim();
    }

    public static boolean equals(CharSequence str1, CharSequence str2) {
        if(str1 == null && str2 == null) {
            return true;
        }

        if(str1 != null && str2 != null) {
            return str1.equals(str2);
        }

        return false;
    }

    public static String format(String input, String... parts) {
        return input;
    }
}