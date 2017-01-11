
package cc.ak.sdk.util;

import android.annotation.SuppressLint;
import android.util.Base64;

public class InitEncodeUtil {

    //反转字符串
    public static String reverse1(String s) {
        int length = s.length();
        if (length <= 1)
            return s;
        String left = s.substring(0, length / 2);
        String right = s.substring(length / 2, length);
        return reverse1(right) + reverse1(left);
    }

    @SuppressLint("NewApi")
    public static String decodeResult(String result) {
        String key = "abcdefghijklmnABCDEFGHIJKLMNOPQRST123456!@#$%^&*()POIKJMNB"; // 密钥
        //        String result = "GkACFBUPA0pTSFxeWVxycnFmaWQmODkhLjVvdG0xNwoHIgFmZkUCR1YuVWd2LW9DchAiOQ9pNw==";
        //Base64解密
        String decodeBase64 = new String(Base64.decode(result.getBytes(), Base64.DEFAULT));
        //反转字符串
        String reverseKey = reverse1(key);

        //拼接解密后的字符串30次
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append(key + reverseKey);
        }
        int decodeLen = decodeBase64.length();
        int sbLen = sb.length();

        int len = decodeLen > sbLen ? decodeLen : sbLen;

        String subDecodeStr = "";
        int asciiDecode = 0;
        String linkStr = "";
        int asciiLink = 0;
        StringBuilder ssb = new StringBuilder();
        for (int i = 0; i < len; i++) {

            if (i < decodeLen) {
                subDecodeStr = decodeBase64.substring(i, i + 1);

                String stringToAscii = stringToAscii(subDecodeStr);
                asciiDecode = Integer.parseInt(stringToAscii);

                if (i < sbLen) {
                    linkStr = sb.substring(i, i + 1);
                    String stringToAsciiSb = stringToAscii(linkStr);
                    asciiLink = Integer.parseInt(stringToAsciiSb);
                }

                //异或
                int res = asciiDecode ^ asciiLink;
                String asciiToStr = asciiToString(String.valueOf(res));
                ssb.append(asciiToStr);

            } else {
                break;
            }

        }

        return ssb.toString();
    }

    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1)
            {
                sbu.append((int) chars[i]).append(",");
            }
            else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public static String asciiToString(String value)
    {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

}
