package com.vas.vassdk.http;

import android.app.Application;

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


//    String url = "http://api.nohttp.net/upload";
//
//    Request<String> request = new StringRequest(url, RequestMethod.POST)
//        .add("id", 123)
//        .add("name",  "yanzhenjie")
//        .add("desc", "abc");
    
    

}
