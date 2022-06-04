package com.soonsoft.uranus.core.common.lang;

import com.soonsoft.uranus.core.error.format.StringFormatException;

/**
 * 字符串函数工具集
 */
public abstract class StringUtils {

    private final static String FORMAT_OPEN = "{";
    private final static String FORMAT_CLOSE = "}";

    public static String Empty = "";

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

    public static String toHexString(byte[] input) {
        if(input == null || input.length == 0) {
            return Empty;
        }

        StringBuilder builder = new StringBuilder(input.length * 2);
        for(int i = 0; i < input.length; i++) {
            int val = input[i] & 0xFF;
            String hexVal = Integer.toHexString(val);
            if(hexVal.length() < 2) {
                builder.append("0");
            }
            builder.append(hexVal);
        }
        return builder.toString();
    }

    public static boolean startsWith(String str, String prefix) {
        return (str != null && prefix != null && str.length() >= prefix.length() && str.startsWith(prefix));
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
		return (str != null && prefix != null && str.length() >= prefix.length() &&
				str.regionMatches(true, 0, prefix, 0, prefix.length()));
	}

    public static boolean endsWith(String str, String suffix) {
        return (str != null && suffix != null && str.length() >= suffix.length() && str.endsWith(suffix));
    }

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		return (str != null && suffix != null && str.length() >= suffix.length() &&
				str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
	}

    /**
     * 字符串格式化函数
     * 占位符：
     *   %s 字符串
     *   %c 字符
     *   %b 布尔类型
     *   %d 整数（十进制）
     *   %x 整数（十六进制）
     *   %o 整数（八进制）
     *   %f 浮点
     *   %a 十六进制浮点
     *   %% 百分比
     * @param input 格式化模板
     * @param params 填充的参数
     * @return 格式化后的字符串
     */
    public static String format(final String input, Object... params) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return String.format(input, params);
    }

    /**
     * 简单的字符串格式化函数
     *   StringUtils.format("he{0}{1}o", "l", "l")
     * @param input 格式化模板
     * @param params 填充的参数
     * @return 格式化后的字符串
     */
    public static String format(final String input, String... parts) {
        if(isBlank(input)) {
            return input;
        }

        StringBuilder builder = new StringBuilder();
        int index = 0;
        for(;;) {
            int openIndex = input.indexOf(FORMAT_OPEN, index);
            int closeIndex = input.indexOf(FORMAT_CLOSE, index);

            // 没有占位符
            if(openIndex < 0 && closeIndex < 0) {
                builder.append(input.substring(index));
                break;
            }
            
            // 处理'}'符号输出
            if(closeIndex > -1 && (closeIndex < openIndex || openIndex == -1)) {
                if(input.charAt(closeIndex + 1) != FORMAT_CLOSE.charAt(0)) {
                    throw new StringFormatException("字符'}'， index:" + closeIndex + "， 标记符输出格式错误，应为}}");
                }
                builder.append(input.substring(index, closeIndex + 1));
                index = closeIndex + 2;
                continue;
            }

            // 填充普通字符
            builder.append(input.substring(index, openIndex));
            index = openIndex + 1;

            // 处理'{'符号输出
            if(input.charAt(index) == FORMAT_OPEN.charAt(0)) {
                builder.append(input.charAt(index));
                index += 1;
                continue;
            }

            if(closeIndex == -1) {
                throw new StringFormatException("缺少闭合标记，正确的占位符例子：{0}、{1}……");
            }

            int fillIndex = Integer.parseInt(input.substring(index, closeIndex));
            builder.append(parts[fillIndex]);
            index = closeIndex + 1;
        }

        return builder.toString();
    }

    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        return String.join(delimiter, elements);
    }

    public static String join(CharSequence delimiter, CharSequence... elements) {
        return String.join(delimiter, elements);

    }

    public static String toString(Object o) {
        return o == null ? null : o.toString();
    }
}