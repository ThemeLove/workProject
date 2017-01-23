package com.vas.vassdk.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Application;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.util.DeviceUtil;
import com.vas.vassdk.util.VASLogUtil;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;

public class VasHttpUtil
{

    private volatile static VasHttpUtil instance;

    private RequestQueue queue;

    private VasHttpUtil()
    {
        queue = NoHttp.newRequestQueue();
    }

    public static VasHttpUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (VasHttpUtil.class)
            {
                if (instance == null)
                {
                    instance = new VasHttpUtil();
                }
            }
        }
        return instance;
    }

//    public void init(Application application, boolean isDebug)
//    {
//        NoHttp.initialize(application);
//        Logger.setDebug(isDebug);
//        Logger.setTag("VasHttpUtil");
//    }

    /**
     * 添加一个请求到请求队列。
     * 
     * @param what 用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what。
     * @param request 请求对象。
     * @param listener 结果回调对象。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void add(int what, Request<T> request, OnResponseListener listener)
    {
        request.addHeader("PLATFORM", "android");
        request.addHeader("SDKVERSION", "1.0.0");
        request.addHeader("CID", VasSDKConfig.VAS_CHANNELID);
        request.addHeader("CCID", VasSDKConfig.VAS_SUBCHANNEL_ID);
        request.addHeader("GID", VasSDKConfig.VAS_GAMEID);
        request.addHeader("DEVICEINFO", android.os.Build.MODEL);
        request.addHeader("UUID", DeviceUtil.getDeviceUuid(VasSDK.getInstance().getActivity()).toString());
        request.addHeader("BRAND", android.os.Build.MANUFACTURER);
        request.addHeader("NETTYPE", DeviceUtil.getNetType(VasSDK.getInstance().getActivity()) + "");
        request.setCancelSign(what);
        queue.add(what, request, listener);
    }

    /**
     * 取消这个sign标记的所有请求。
     * 
     * @param sign 请求的取消标志。
     */
    public void cancelBySign(Object sign)
    {
        queue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求。
     */
    public void cancelAll()
    {
        queue.cancelAll();
    }


    @SuppressWarnings("rawtypes")
    public <T> void addHeader(Request<T> request,HashMap<String,String> headMap){
        if(headMap == null){
            return;
        }
        for(Iterator iter = headMap.entrySet().iterator();iter.hasNext();){
            Map.Entry element = (Map.Entry)iter.next();
            String strKey = (String) element.getKey();
            String strValue = (String) element.getValue();
            request.addHeader(strKey, strValue);
            VASLogUtil.d("header : " + strKey+"/"+strValue);
          }
        
    }
    
    

}
