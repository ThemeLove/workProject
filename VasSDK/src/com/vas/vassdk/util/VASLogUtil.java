
package com.vas.vassdk.util;

import com.vas.vassdk.VasSDKConfig;

import android.util.Log;

/**
 * 日志工具
 */
public class VASLogUtil {

    private final static String LOG_TAG = "VasSDK";

    public static void d(String msg) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void i(String msg) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.i(LOG_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.e(LOG_TAG, msg);
        }
    }

    public static void e(String msg, Throwable throwable) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.e(LOG_TAG, msg, throwable);
        }
    }

    public static void d(String tag, String msg) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true")) {
            Log.e(tag, msg);
        }
    }

}
