
package cc.ak.sdk.plugin;

import cc.ak.sdk.AkSDK;
import cc.ak.sdk.bean.AkPayParam;
import cc.ak.sdk.factory.AkFactory;
import cc.ak.sdk.util.AKLogUtil;

public class AkPay {

    private static AkPay instance;
    private IPayPlugin payPlugin;

    private AkPay() {
    }

    public static AkPay getInstance() {
        if (instance == null) {
            instance = new AkPay();
        }
        return instance;
    }

    public void init() {
        payPlugin = (IPayPlugin) AkFactory.newPlugin(IPayPlugin.PLUGIN_TYPE);
        AKLogUtil.d("AKPay init");
    }

    public void pay(final AkPayParam param) {
        if (payPlugin != null) {
            AkSDK.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    payPlugin.pay(param);
                }
            });
        } else {
            AKLogUtil.e("no instance for payplugin");
        }
    }

}
