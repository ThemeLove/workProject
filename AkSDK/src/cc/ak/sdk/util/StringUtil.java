
package cc.ak.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cc.ak.sdk.AkSDKConfig;

import org.json.JSONObject;

public class StringUtil {

    public static final String STATISTICSHARENAME = "statistic_setting";
    public static final String KEY_NEWINSTALL = "key_newinstall";

    public static boolean isEmpty(String text) {
        if (text == null || text.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static JSONObject toJSON(String data) {
        JSONObject result = null;
        try {
            result = new JSONObject(data);
        } catch (Exception ex) {
            AKLogUtil.e("to json fail 【" + data + "】", ex);
        }
        return result;
    }

    /**
     * 统计相关
     * @param context
     * @return
     */
    public static JSONObject creatStatisticJson(Context context) {
        JSONObject defaultJson = null;
        try {
            defaultJson = new JSONObject();
            defaultJson.put("logId", getLoginId());
            defaultJson.put("gameId", AkSDKConfig.AK_GAMEID);
            defaultJson.put("platformId", AkSDKConfig.AK_PARTNERID);
            defaultJson.put("channel", AkSDKConfig.AK_CHANNEL_ID);
            defaultJson.put("idfa", DeviceUtil.getAndroidId(context));
            defaultJson.put("mac", DeviceUtil.getMac(context));
            defaultJson.put("devicePlatformName", "Android");
            defaultJson.put("devicePlatformVersion", DeviceUtil.getSystemVer());
            defaultJson.put("newInstall", getNewInstall(context));
            defaultJson.put("deviceName", DeviceUtil.getDeviceName());
            defaultJson.put("actionTime", DeviceUtil.getCurrentTime());
            defaultJson.put("gameVersion", DeviceUtil.getVersionName(context));
            defaultJson.put("sdkVersion", AkSDKConfig.SDK_VER);
        } catch (Exception e) {

        }
        return defaultJson;
    }

    /**
     * Math.random()取值范围是[0,1)
     * @return
     */
    private static String getLoginId() {
        String loginId = String.valueOf(System.currentTimeMillis())
                + String.valueOf((int) (Math.random() * 9000 + 1000));
        return MD5Util.encode(loginId);
    }

    private static String getNewInstall(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                STATISTICSHARENAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NEWINSTALL, "1");
    }

    public static void setNewInstall(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                STATISTICSHARENAME,
                Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NEWINSTALL, "0");
        editor.commit();
    }

}
