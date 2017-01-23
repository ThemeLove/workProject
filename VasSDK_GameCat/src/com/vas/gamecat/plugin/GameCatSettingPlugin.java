package com.vas.gamecat.plugin;

import android.app.Activity;

import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.plugin.ISettingPlugin;

public class GameCatSettingPlugin implements ISettingPlugin
{

    private Activity mActivity;
    
    public GameCatSettingPlugin(Activity activity){
        this.mActivity = activity;
    }
    
    @Override
    public void setDebug(boolean debug)
    {
        
    }

    @Override
    public void setIsLandScape(boolean isLandScape)
    {
        
    }

    @Override
    public void setShowExitDialog(boolean showExitDialog)
    {
        
    }

    @Override
    public void exit()
    {
        
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
    public String getExtrasConfig(String paramString)
    {
        return "";
    }

    @Override
    public boolean isFunctionSupported(int paramInt)
    {
        return false;
    }

    @Override
    public String callFunction(int paramInt)
    {
        return "";
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        return false;//单接渠道sdk的时候，渠道有sdk退出框，返回true;
    }

}
