
package cc.ak.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtil {

    /**
     * 检测网络是否连接
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        return (isWiFiActive(context) || is3GAvailable(context));
    }

    /**
     * 判断wifi是否已经连接
     * 
     * @param context
     * @return
     */
    public static boolean isWiFiActive(Context context) {
        WifiManager mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断3G网络是否已经连接
     * 
     * @param context
     * @return
     */
    public static boolean is3GAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                if (info.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPhone(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|17[0-9])\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

}
