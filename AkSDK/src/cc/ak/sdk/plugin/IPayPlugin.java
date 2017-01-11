package cc.ak.sdk.plugin;

import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.bean.AkPayParam;

public interface IPayPlugin {
	
	public static final int PLUGIN_TYPE = AkSDKConfig.PLUGIN_TYPE_PAY;
	
	public void pay(AkPayParam param);
	
}
