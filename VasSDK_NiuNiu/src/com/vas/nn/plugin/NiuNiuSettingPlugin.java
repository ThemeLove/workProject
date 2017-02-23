package com.vas.nn.plugin;

import android.app.Activity;

import com.vas.nn.NiuNiuSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.plugin.ISettingPlugin;

public class NiuNiuSettingPlugin implements ISettingPlugin
{
    
    public Activity mActivity;
    
    public NiuNiuSettingPlugin(Activity activity){
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
        NiuNiuSDK.getInstance().exit();
    }

    @Override
    public String getExtrasConfig(String arg0)
    {
        return null;
    }

    @Override
    public int getSubPlatformId()
    {
        int type = -1;
        try
        {
            type = Integer.parseInt(VasSDKConfig.VAS_SUBPLATFORMID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return type;
    }

    @Override
    public boolean isFunctionSupported(int arg0)
    {
        return false;
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        return false;//单接渠道sdk的时候，渠道有sdk退出框，返回true;
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
