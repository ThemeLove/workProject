package com.vas.vassdk.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 网络相关工具类
 * 
 * @author chuckcheng
 * @version [版本号, 2011-9-27]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class NetworkUtils
{

    private static final String TAG = "VasSDK-NetworkUtils";

    /**
     * Returns whether the network is available
     * 
     * @param context Context
     * @return 网络是否可用
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNetworkAvailable(Context context)
    {

        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null)
            {
                VASLogUtil.e(TAG +"couldn't get connectivity manager");
            }
            else
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                {
                    for (int i = 0; i < info.length; i++)
                    {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {

                            // LogUtils.error("network is available:"
                            // + info[i].getTypeName());

                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }

     //   LogUtils.error("network is not available");

        return false;
    }

    /**
     * 获取网络类型
     * 
     * @param context Context
     * @return 网络类型
     * @see [类、类#方法、类#成员]
     */
    public static int getNetworkType(Context context)
    {
        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null)
            {
                VASLogUtil.e(TAG,"couldn't get connectivity manager");
            }
            else
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                {
                    for (int i = 0; i < info.length; i++)
                    {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {

                            return info[i].getType();
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }

        return -1;
    }

    /**
     * 判断网络是不是手机网络，非wifi
     * 
     * @param context Context
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public static boolean isMobileNetwork(Context context)
    {
        return isNetworkAvailable(context) && (ConnectivityManager.TYPE_MOBILE == getNetworkType(context));
    }

    /**
     * 判断网络是不是wifi
     * 
     * @param context Context
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public static boolean isWifiNetwork(Context context)
    {
        return isNetworkAvailable(context) && (ConnectivityManager.TYPE_WIFI == getNetworkType(context));
    }

    /**
     * Returns whether the network is roaming
     * 
     * @param context Context
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNetworkRoaming(Context context)
    {
        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null)
            {
                VASLogUtil.e(TAG,"couldn't get connectivity manager");
            }
            else
            {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE)
                {
                    if (/* TelephonyManager.getDefault() */telephonyManager.isNetworkRoaming())
                    {

                        return true;
                    }
                    else
                    {

                    }
                }
                else
                {

                }
            }

        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }

        return false;
    }

    /** 获取本机ip。 */
    public static String getLocalIpAddress()
    {
        // String ipaddress = "";
        try
        {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements())
            {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements())
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    // loopback地址就是代表本机的IP地址，只要第一个字节是127，就是lookback地址
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                        // ipaddress = ipaddress + ";" +
                        // inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }
        return null;
        // return ipaddress;
    }

    /** 手机号码 */
    public static String getLine1Number(Context context)
    {
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getLine1Number();
        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }
        return "";
    }

    /** 判断端口是否被占用 */
    public static boolean isPortUsed(int port)
    {
        String[] cmds = {"netstat", "-an"};
        Process process = null;
        InputStream is = null;
        DataInputStream dis = null;
        try
        {

            String line = "";
            Runtime runtime = Runtime.getRuntime();

            process = runtime.exec(cmds);
            is = process.getInputStream();
            dis = new DataInputStream(is);
            while ((line = dis.readLine()) != null)
            {
                // LogUtils.error(line);
                if (line.contains(":" + port))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }
        finally
        {
            try
            {
                if (dis != null)
                {
                    dis.close();
                }
                if (is != null)
                {
                    is.close();
                }
                if (process != null)
                {
                    process.destroy();
                }
            }
            catch (Exception e)
            {
                VASLogUtil.e(TAG, e);
            }
        }
        return false;
    }

    private static String getWifiMacAddress(Context context)
    {
        try
        {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        }
        catch (Exception e)
        {
            VASLogUtil.e(TAG, e);
        }
        return "";
    }

    public static String getMacAddress(Context context)
    {
        String addr = getMacAddrInFile("/sys/class/net/eth0/address");
        if (TextUtils.isEmpty(addr))
        {
            addr = getMacAddrInFile("/sys/class/net/wlan0/address");
        }

        if (TextUtils.isEmpty(addr))
        {
            return getWifiMacAddress(context);
        }

        return addr;
    }

    private static String getMacAddrInFile(String filepath)
    {
        File f = new File(filepath);
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(f);
            BufferedReader rd = new BufferedReader(new InputStreamReader(fis));
            String str = rd.readLine();

            // 去除空格
            str = str.replaceAll(" ", "");

            // 查看是否是全0的无效MAC地址 如 00:00:00:00:00:00
            String p = str.replaceAll("-", "");
            p = p.replaceAll(":", "");
            if (p.matches("0*"))
            {
                return null;
            }
            return str;
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return null;
    }

    /**
     * 获取网络类型
     * 
     * @param telMgr
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String networkType(TelephonyManager telMgr, Context context)
    {
        // 获取网络类型
        String network = null;
        if (NetworkUtils.isWifiNetwork(context))
        {
            network = "wifi";
        }
        else
        {
            if (telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE)
            {
                network = "EDGE";
            }
            else if (telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS)
            {
                network = "GPRS";
            }
            else if (telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA)
            {
                network = "CDMA";
            }
            else
            {
                network = "3G";
            }
        }

        return network;

    }
}
