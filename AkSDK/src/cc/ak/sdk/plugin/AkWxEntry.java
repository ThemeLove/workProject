
package cc.ak.sdk.plugin;

import android.app.Activity;

import cc.ak.sdk.factory.AkFactory;
import cc.ak.sdk.util.AKLogUtil;

public class AkWxEntry {

    private static AkWxEntry instance;
    private IWXPlugin wxPlugin;

    private AkWxEntry() {
    }

    public static AkWxEntry getInstance() {
        if (instance == null) {
            instance = new AkWxEntry();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        wxPlugin = (IWXPlugin) AkFactory.newPlugin(IWXPlugin.PLUGIN_TYPE);
        AKLogUtil.d("AKWxEntry init");
    }

    public void execute(Activity activity) {
        if (wxPlugin != null) {
            wxPlugin.entry(activity);
        } else {
            AKLogUtil.e("no instance for wxPlugin");
        }
    }

}
