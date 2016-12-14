package com.vas.vassdk.factory;

import java.lang.reflect.Constructor;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.util.VASLogUtil;

import android.app.Activity;
import android.text.TextUtils;

public class VasFacktory
{

    /**
     * 实例化插件
     */
    public static Object newPlugin(int pluginType) {
        String pluginName = VasSDKConfig.getInstance().getPlugin(pluginType);
        if (TextUtils.isEmpty(pluginName)) {
            return null;
        }

        try {
            Class clazz = Class.forName(pluginName);
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {
                Activity.class
            });
            return constructor.newInstance(new Object[] {
                VasSDK.getInstance().getActivity()
            });
        } catch (Exception ex) {
            VASLogUtil.e("fail to new Plugin", ex);
        }
        return null;
    }
    
}
