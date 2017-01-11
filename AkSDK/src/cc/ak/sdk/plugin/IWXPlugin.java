package cc.ak.sdk.plugin;

import cc.ak.sdk.AkSDKConfig;
import android.app.Activity;


//微信插件
public interface IWXPlugin {
	
	public static final int PLUGIN_TYPE = AkSDKConfig.PLUGIN_TYPE_WXAPI;
	
	public void entry(Activity activity);
	
}