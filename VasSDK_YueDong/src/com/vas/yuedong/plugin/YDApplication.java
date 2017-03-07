package com.vas.yuedong.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.vas.vassdk.apiadapter.IApplicationListener;
import com.zzydgame.supersdk.api.YDMergeApp;
import com.zzydgame.supersdk.api.YDMergeSDK;

public class YDApplication extends YDMergeApp implements IApplicationListener
{

    @Override
    public void onProxyAttachBaseContext(Application arg0, Context arg1)
    {
        YDMergeSDK.appAttachBaseContext(arg0, arg1);
    }

    @Override
    public void onProxyConfigurationChanged(Application arg0, Configuration arg1)
    {
        YDMergeSDK.onConfigurationChanged(arg0, arg1);
    }

    @Override
    public void onProxyCreate(Application arg0)
    {
        YDMergeSDK.appOnCreate(arg0);
    }

}
