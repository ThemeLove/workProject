package com.vas.sy.plugin;

import android.app.Activity;

import com.vas.sy.SY07073SDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.plugin.ISettingPlugin;

public class SYSettingPlugin implements ISettingPlugin
{

    public Activity mActivity;
    
    public SYSettingPlugin(Activity activity){
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
        SY07073SDK.getInstance().exit();
    }

    @Override
    public String getExtrasConfig(String arg0)
    {
        return null;
    }

    @Override
    public int getSubPlatformId()
    {
        int subPlatformId = -1;
        try
        {
            subPlatformId = Integer.parseInt(VasSDKConfig.VAS_SUBPLATFORMID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return subPlatformId;
    }

    @Override
    public boolean isFunctionSupported(int arg0)
    {
        return false;
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        return true;
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
