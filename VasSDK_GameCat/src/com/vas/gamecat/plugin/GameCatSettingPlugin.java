package com.vas.gamecat.plugin;

import android.app.Activity;
import android.util.Log;

import com.example.demo.VasSDK_GameCatMainActivity;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setIsLandScape(boolean isLandScape)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShowExitDialog(boolean showExitDialog)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void exit()
    {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        return "getExtrasConfig";
    }

    @Override
    public boolean isFunctionSupported(int paramInt)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String callFunction(int paramInt)
    {
        // TODO Auto-generated method stub
        return "callFunction";
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
