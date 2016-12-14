package cc.ak.sdk.tool.bean;

import java.util.Map;

public class GameChannel {
	
	private String id;							//渠道号
	private String name;						//描述，用于显示
	private String icon;
	private String suffix;						//包名后缀
	private String keystore;
	private boolean splash = false;				//是否开启闪屏，默认不开启
	private Map<String, String> channelParams;	//渠道参数，最后生成到aksdk_config.xml中的map元素
	private Map<String, String> metaDatas;		//渠道参数，最后生成到AndroidManifest.xml中的meta-data元素
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSplash() {
		return splash;
	}
	public void setSplash(boolean splash) {
		this.splash = splash;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}
	public Map<String, String> getChannelParams() {
		return channelParams;
	}
	public void setChannelParams(Map<String, String> channelParams) {
		this.channelParams = channelParams;
	}
	public Map<String, String> getMetaDatas() {
		return metaDatas;
	}
	public void setMetaDatas(Map<String, String> metaDatas) {
		this.metaDatas = metaDatas;
	}
	@Override
	public String toString() {
		return id + " | " + name;
	}
	
}
