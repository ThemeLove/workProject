
package cc.ak.sdk.factory;

import android.app.Activity;

import cc.ak.sdk.AkSDK;
import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.util.AKLogUtil;
import cc.ak.sdk.util.StringUtil;

import java.lang.reflect.Constructor;

public class AkFactory {

    /**
     * 实例化插件
     */
    public static Object newPlugin(int pluginType) {
        String pluginName = AkSDKConfig.getInstance().getPlugin(pluginType);
        if (StringUtil.isEmpty(pluginName)) {
            return null;
        }

        try {
            Class clazz = Class.forName(pluginName);
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {
                Activity.class
            });
            return constructor.newInstance(new Object[] {
                AkSDK.getInstance().getActivity()
            });
        } catch (Exception ex) {
            AKLogUtil.e("fail to new Plugin", ex);
        }
        return null;
    }

}
