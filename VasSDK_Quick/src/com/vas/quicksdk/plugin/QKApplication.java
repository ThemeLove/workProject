package com.vas.quicksdk.plugin;

import android.content.Context;
import android.content.res.Configuration;

import com.quicksdk.QuickSdkApplication;
import com.vas.vassdk.apiadapter.IApplicationListener;

public class QKApplication extends QuickSdkApplication implements IApplicationListener
{

    @Override
    public void onProxyCreate()
    {
        super.onCreate();
    }

    @Override
    public void onProxyAttachBaseContext(Context base)
    {
        super.attachBaseContext(base);
    }

    @Override
    public void onProxyConfigurationChanged(Configuration config)
    {
        super.onConfigurationChanged(config);
    }

}
