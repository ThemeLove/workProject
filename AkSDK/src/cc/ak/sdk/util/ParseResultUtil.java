package cc.ak.sdk.util;

import org.json.JSONObject;

import cc.ak.sdk.bean.AKSdkInfo;

public class ParseResultUtil {

    
    public static AKSdkInfo parseInitRetData(String data){
        AKSdkInfo info = null;
        try {
            JSONObject retObj = new JSONObject(data);
            boolean flag = retObj.optBoolean("flag");
            JSONObject sdkInfoObj = retObj.optJSONObject("sdk_info");
            info = new AKSdkInfo();
            info.setPartnerId(sdkInfoObj.optString("partner_id"));
            info.setGameId(sdkInfoObj.optString("game_id"));
            info.setAppId(sdkInfoObj.optString("app_id"));
            info.setAppkey(sdkInfoObj.optString("app_key"));
            info.setSecretKey(sdkInfoObj.optString("secret_key"));
            info.setPn(sdkInfoObj.optString("pn"));
            info.setAuthUrl(sdkInfoObj.optString("auth_url"));
            info.setPayUrl(sdkInfoObj.optString("pay_url"));
            info.setExtUrl(sdkInfoObj.optString("ext_url"));
            info.setUtime(sdkInfoObj.optString("utime"));
            info.setStatus(sdkInfoObj.optString("status"));
            info.setLicenseUrl(sdkInfoObj.optString("license_url"));//哆可梦官方sdk注册协议url
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    
}
