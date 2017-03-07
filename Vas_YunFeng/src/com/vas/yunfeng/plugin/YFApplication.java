package com.vas.yunfeng.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.ungame.android.sdk.UngameApplication;
import com.vas.vassdk.apiadapter.IApplicationListener;

public class YFApplication implements IApplicationListener
{

    @Override
    public void onProxyAttachBaseContext(Application arg0, Context arg1)
    {
        
    }

    @Override
    public void onProxyConfigurationChanged(Application arg0, Configuration arg1)
    {
        
    }

    @Override
    public void onProxyCreate(Application arg0)
    {
        UngameApplication.initContext(arg0);
    }

}
