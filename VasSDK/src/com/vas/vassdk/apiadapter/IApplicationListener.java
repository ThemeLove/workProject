package com.vas.vassdk.apiadapter;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationListener
{
    public void onProxyCreate(Application application);

    public void onProxyAttachBaseContext(Application application,Context base);

    public void onProxyConfigurationChanged(Application application,Configuration config);

}
