package com.vas.quicksdk.plugin;

import android.app.Activity;

import com.vas.quicksdk.QKSDK;
import com.vas.vassdk.plugin.ISettingPlugin;

public class QuickSettingPlugin implements ISettingPlugin
{

    private Activity mActivity;

    public QuickSettingPlugin(Activity activity)
    {
        this.mActivity = activity;
    }

    @Override
    public void setDebug(boolean debug)
    {
        QKSDK.getInstance().setDebugMode(debug);
    }

    @Override
    public void setIsLandScape(boolean isLandScape)
    {
        QKSDK.getInstance().setIsLandScape(isLandScape);
    }

    @Override
    public void setShowExitDialog(boolean showExitDialog)
    {
        QKSDK.getInstance().setShowExitDialog(showExitDialog);
    }

    @Override
    public void exit()
    {
        QKSDK.getInstance().exit();
    }

    @Override
    public int getSubPlatformId()
    {
        return QKSDK.getInstance().getChannelType();
    }

    @Override
    public String getExtrasConfig(String paramString)
    {
        return QKSDK.getInstance().getExtrasConfig(paramString);
    }

    @Override
    public boolean isFunctionSupported(int paramInt)
    {
        return QKSDK.getInstance().isFunctionSupported(paramInt);
    }

    @Override
    public String callFunction(int paramInt)
    {
        return QKSDK.getInstance().callFunction(paramInt);
    }

    @Override
    public boolean isSDKShowExitDialog()
    {
        return QKSDK.getInstance().isSDKShowExitDialog();
    }

}
