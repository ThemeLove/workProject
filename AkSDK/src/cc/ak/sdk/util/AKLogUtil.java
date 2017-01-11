
package cc.ak.sdk.util;

import android.util.Log;

import cc.ak.sdk.AkSDKConfig;

/**
 * 日志工具
 */
public class AKLogUtil {

    private final static String LOG_TAG = "AkSDK";

    public static void d(String msg) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void i(String msg) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.i(LOG_TAG, msg);
        }
    }

    public static void e(String msg) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.e(LOG_TAG, msg);
        }
    }

    public static void e(String msg, Throwable throwable) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.e(LOG_TAG, msg, throwable);
        }
    }

    public static void d(String tag, String msg) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if ("true".equalsIgnoreCase(AkSDKConfig.AK_DEBUG)) {
            Log.e(tag, msg);
        }
    }

}
