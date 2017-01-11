package cc.ak.sdk.callback;

import org.json.JSONObject;

public interface AkResultCallback {
	
	//初始化成功
	public static final int CODE_INIT_SUCCESS = 1;
	
	//初始化失败
	public static final int CODE_INIT_FAILURE = 2;
	
	//登录成功
	public static final int CODE_LOGIN_SUCCESS = 3;
	
	//登录失败
	public static final int CODE_LOGIN_FAILURE = 4;
	
	//切换帐号成功
	public static final int CODE_SWITCH_ACCOUNT_SUCCESS = 5;
	
	//切换帐号失败
	public static final int CODE_SWITCH_ACCOUNT_FAILURE = 6;
	
	//注销成功
	public static final int CODE_LOGOUT_SUCCESS = 7;
	
	//注销失败
	public static final int CODE_LOGOUT_FAILURE = 8;
	
	//支付成功
	public static final int CODE_PAY_SUCCESS = 9;
	
	//已支付，但由于网络等原因未确定是否成功，最终已服务器回调为主
	public static final int CODE_PAY_WAIT = 10;
	
	//支付失败
	public static final int CODE_PAY_FAILURE = 11;
	
	public void onResult(int code, JSONObject data);
	
}
