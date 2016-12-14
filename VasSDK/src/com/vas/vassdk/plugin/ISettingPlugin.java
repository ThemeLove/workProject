package com.vas.vassdk.plugin;

import com.vas.vassdk.VasSDKConfig;

public interface ISettingPlugin
{

    public static final int PLUGIN_TYPE = VasSDKConfig.PLUGIN_TYPE_SETTING;
    
    public void setDebug(boolean debug);
    
    public void setIsLandScape(boolean isLandScape);
    
    public void setShowExitDialog(boolean showExitDialog);
    
    public void exit();
    
    public int getSubPlatformId();
    
    public String getExtrasConfig(String paramString);
    
    public boolean isFunctionSupported(int paramInt);
    
    public String callFunction(int paramInt);
    
    public boolean isSDKShowExitDialog();
    
    
    
}
