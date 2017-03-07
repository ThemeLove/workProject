package com.vas.dianyou.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.dianyou.openapi.DYOnlineApplication;
import com.dianyou.openapi.DYSDK;
import com.vas.vassdk.apiadapter.IApplicationListener;

public class DYApplication extends DYOnlineApplication implements IApplicationListener 
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
        DYSDK.init(arg0);
    }

}
