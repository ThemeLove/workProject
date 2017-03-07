package com.vas.vassdk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.http.VasHttpUtil;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;



public class VasStatisticUtil
{

    public static final String STATISTICURL = "http://tj.g.pptv.com/data/1.php?";
    
    public static final String APP = "unisdk";
    
    public static final String VER = "1.0.0";
    
    public static final String FIRST_ACTIVE = "501";//SDK首次启动
    
    public static final String ACTIVE = "502";//SDK启动
    
    public static final String LOGIN = "503";//登录成功
    
    public static final String ENTERGAME = "504";//进入游戏
    
    public static final String ACTIVE_NAME = "vasunisdk";
    
    public static final int REQUEST_CODE = 10;
    
    private static final Base64Encoder encoder = new Base64Encoder();
    
    private static String encodeInBip(String str)
    {

        String PPL_KEY = "pplive_vas";
        int KEY_LEN = PPL_KEY.length();
        byte[] result;
        byte[] enUrl = null;

        try
        {
            enUrl = str.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
        }

        result = new byte[enUrl.length];

        for (int i = 0; i < enUrl.length; i++)
        {
            int key_index = i % KEY_LEN;
            result[i] = (byte) (enUrl[i] + (byte) PPL_KEY.charAt(key_index));
        }

        return new String(encode(result));
    }
    
    
    private static byte[] encode(byte[] data)
    {
        int len = (data.length + 2) / 3 * 4;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try
        {
            encoder.encode(data, 0, data.length, bOut);
        }
        catch (IOException e)
        {
            throw new RuntimeException("exception encoding base64 string: " + e);
        }

        return bOut.toByteArray();
    }
    
    
    private static String genUrl(Bundle param)
    {
        StringBuffer sbBuffer = new StringBuffer();
        if (param != null)
        {
            int i = 0;
            for (String key : param.keySet())
            {

                if (i == 0)
                {
                    sbBuffer.append("");
                }
                else
                {
                    sbBuffer.append("&");
                }

                try
                {
                    sbBuffer.append(key + "=" + param.getString(key));
                }
                catch (Exception e)
                {
                    VASLogUtil.d("VasStatisticUtil " + e.getMessage());
                }

                i++;
            }
        }

        return sbBuffer.toString();
    }
    
    
    public static void sendStatistic(String uid,String evt){
        Activity activity = VasSDK.getInstance().getActivity();
        // 获取网络
        TelephonyManager telMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        // 运营商名称
        String carrier = telMgr.getNetworkOperatorName();
        if (TextUtils.isEmpty(carrier))
        {
            carrier = "";
        }

        // 获取网络类型
        String network = NetworkUtils.networkType(telMgr, activity);
        // 获取分辨率
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        
        Bundle param = new Bundle();
        param.putString("app", APP);
        param.putString("uid", uid);
        param.putString("puid", DeviceUtil.getDeviceUuid(activity) + "");
        param.putString("cid", VasSDKConfig.VAS_PLATFORMID);
        param.putString("ccid", VasSDKConfig.VAS_SUBPLATFORMID);
        param.putString("gid", VasSDKConfig.VAS_GAMEID);
        param.putString("ver", VER);
        param.putString("evt", evt);
        param.putString("_st", "Android");
        param.putString("_sv", android.os.Build.BRAND);
        param.putString("_sr", new StringBuffer().append(width).append('*').append(height).toString());
        param.putString("_nt", network);
        param.putString("_np", carrier);
        param.putString("_spm", android.os.Build.MODEL);
        param.putString("_spv", android.os.Build.VERSION.RELEASE);

        String paramString = encodeInBip(genUrl(param));
        
        Request<String> request = new StringRequest(STATISTICURL + paramString, RequestMethod.GET);
        
        VasHttpUtil.getInstance().add(REQUEST_CODE, request, new OnResponseListener<String>(){

            @Override
            public void onFailed(int arg0, String arg1, Object arg2, Exception arg3, int arg4, long arg5)
            {
                
            }

            @Override
            public void onFinish(int arg0)
            {
                
            }

            @Override
            public void onStart(int arg0)
            {
                
            }

            @Override
            public void onSucceed(int arg0, Response<String> response)
            {
                if (response.getHeaders().getResponseCode() == 200)
                {
                    String result = response.get();
                    Log.d("VasStatusticUtil", result);
                }
            }
            
        });
    }
    
    
}
