package com.vas.vassdk;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

import com.vas.vassdk.apiadapter.IApplicationListener;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

public class VasApplication extends Application
{

    private static final String DEFAULT_PKG_NAME = "com.vas.vassdk";

    private static final String PROXY_NAME = "VAS_APPLICATION_PROXY_NAME";

    private IApplicationListener listener;

    public void onCreate()
    {
        super.onCreate();
        if (listener != null)
        {
            listener.onProxyCreate();
        }
        //对外提供的时候改为false
        NoHttp.initialize(this);
        Logger.setDebug(false);
        Logger.setTag("VasHttpUtil");
    }

    public void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        VasSDK.getInstance().initAllConfigs(base);
        
        this.listener = initProxyApplication();

        if (this.listener != null)
        {
            this.listener.onProxyAttachBaseContext(base);
        }
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
        Log.i("VasSDK", "VasApplication onConfigurationChanged");
        
        if (this.listener != null)
        {
            this.listener.onProxyConfigurationChanged(newConfig);
        }
    }

    @SuppressWarnings("rawtypes")
    private IApplicationListener initProxyApplication()
    {
//        String proxyAppName = VasSDKUtil.getMetaData(this, PROXY_NAME);
        List<String> applicationList = VasSDKConfig.getInstance().getApplicationList();
        String proxyAppName = "";
        if(applicationList != null && applicationList.size() > 0){
            proxyAppName = VasSDKConfig.getInstance().getApplicationList().get(0);
        }
        Log.d("VasSDK" , "VasApplication : proxyAppName = " + proxyAppName);
        if (proxyAppName == null || TextUtils.isEmpty(proxyAppName))
        {
            return null;
        }

        if (proxyAppName.startsWith("."))
        {
            proxyAppName = DEFAULT_PKG_NAME + proxyAppName;
        }

        try
        {
            Class clazz = Class.forName(proxyAppName);
            return (IApplicationListener) clazz.newInstance();

        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {

            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
