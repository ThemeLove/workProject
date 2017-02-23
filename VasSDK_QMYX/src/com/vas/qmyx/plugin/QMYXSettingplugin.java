package com.vas.qmyx.plugin;

import android.app.Activity;

import com.vas.vassdk.plugin.ISettingPlugin;

public class QMYXSettingplugin implements ISettingPlugin
{
    
    public Activity mActivity;
    
    public QMYXSettingplugin(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public String callFunction(int arg0)
    {
        return null;
    }

    @Override
    public void exit()
    {
        
    }

    @Override
    public String getExtrasConfig(String arg0)
    {
        return null;
    }

    @Override
    public int getSubPlatformId()
    {
        return 0;
    }

    @Override
    public boolean isFunctionSupported(int arg0)
    {
        return false;
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        return false;
    }

    @Override
    public void setDebug(boolean arg0)
    {
        
    }

    @Override
    public void setIsLandScape(boolean arg0)
    {
        
    }

    @Override
    public void setShowExitDialog(boolean arg0)
    {
        
    }

}
