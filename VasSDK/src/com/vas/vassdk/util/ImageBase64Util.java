package com.vas.vassdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Base64;

/**
 * Base64转换图片工具
 */
public class ImageBase64Util {

    public static synchronized Bitmap decode(String s) {
        byte abyte0[];
        try {
            if(s == null) {
                return null;
            }
        }catch(Throwable throwable) {
            return null;
        }
        abyte0 = Base64.decode(s, 0);
        if(abyte0 == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
    }

    public static synchronized NinePatchDrawable toNinePathDrawable(Context context, String s, String s1) {
        try {
            if(context == null || isEmpty(s) || isEmpty(s1)) {
                return null;
            }
        }catch(Throwable throwable) {
            return null;
        }
        
        Bitmap bitmap = decode(s);
        if(bitmap == null) {
            return null;
        }
        byte[] abyte0 = Base64.decode(s1, 0);
        if(abyte0 == null) {
            return null;
        }
        NinePatch ninepatch = new NinePatch(bitmap, abyte0, null);
        return new NinePatchDrawable(context.getResources(), ninepatch);
    }
    
    
    private static boolean isEmpty(String text) {
        if (text == null || text.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
	
}