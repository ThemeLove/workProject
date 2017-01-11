
package cc.ak.sdk.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

public class DeviceUtil {

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static final String PREFS_ADINFO_ID = "adinfo_id";
    protected static UUID uuid;

    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        } else {
            return null;
        }
    }

    public static String getIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImei(Context context) {
        TelephonyManager tm = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE));
        return tm.getDeviceId();
    }

    public static UUID getDeviceUuid(Context context) {
        if (uuid == null) {
            synchronized (DeviceUtil.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(context.getContentResolver(),
                                Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) context
                                        .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId
                                        .getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                    }
                }
            }
        }
        return uuid;
    }

    public static String getSystemVer() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceName() {
        return android.os.Build.MODEL;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
        }
        return versionCode;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        int currTime = (int) (System.currentTimeMillis() / 1000);
        return String.valueOf(currTime);
    }

    private static final int NET_TYPE_UNKNOW = 0;
    private static final int NET_TYPE_WIFI = 1;
    private static final int NET_TYPE_2G = 2;
    private static final int NET_TYPE_3G = 3;
    private static final int NET_TYPE_4G = 4;
    private static final int NET_TYPE_OTHER = 5;

    @SuppressLint("ServiceCast")
    public static int getNetType(Context context) {
        ConnectivityManager connectionManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NET_TYPE_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return NET_TYPE_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return NET_TYPE_3G;
                    case 13:
                        return NET_TYPE_4G;
                    default:
                        String subTypeName = networkInfo.getSubtypeName();
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA")
                                || subTypeName.equalsIgnoreCase("WCDMA")
                                || subTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NET_TYPE_3G;
                        } else {
                            return NET_TYPE_OTHER;
                        }
                }
            } else {
                return NET_TYPE_OTHER;
            }
        } else {
            return NET_TYPE_UNKNOW;
        }
    }

    public static String getAndroidId(Context context) {
        String androidId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return androidId;
    }
    
    
    /**
     * Get local ip address
     *
     * @param useIPv4
     * @return
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress addr = inet.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        String ip = toUpperCase(addr.getHostAddress());
                        boolean isIPv4 = addr instanceof Inet4Address;
                            if (isIPv4) {
                                return ip;
                            }
//                        else {
//                            if (!isIPv4) {
//                                int delim = ip.indexOf('%');
//                                return delim < 0 ? ip : ip.substring(0, delim);
//                            }
//                        }
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * Converts this this string to upper case, using the rules of {@code locale}.
     *
     * @param s
     * @return
     */
    public static String toUpperCase(String s) {
        return s.toUpperCase(Locale.getDefault());
    }
    

}
