package com.vas.vassdk.plugin;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.factory.VasFacktory;
import com.vas.vassdk.util.VASLogUtil;

public class VasSetting
{

    private static VasSetting instance;

    private ISettingPlugin mSettingPlugin;

    public static VasSetting getInstance()
    {
        if (instance == null)
        {
            instance = new VasSetting();
        }
        return instance;
    }

    private VasSetting()
    {

    }

    public void init()
    {
        mSettingPlugin = (ISettingPlugin) VasFacktory.newPlugin(ISettingPlugin.PLUGIN_TYPE);
        VASLogUtil.d("vas settingplugin init");
    }

    public void setDebug(final boolean debug)
    {
        if (mSettingPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mSettingPlugin.setDebug(debug);
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for settingplugin");
        }
    }

    public void setIsLandScape(final boolean isLandScape)
    {
        if (mSettingPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mSettingPlugin.setIsLandScape(isLandScape);
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for settingplugin");
        }
    }

    public void setShowExitDialog(final boolean showExitDialog)
    {
        if (mSettingPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mSettingPlugin.setShowExitDialog(showExitDialog);
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for settingplugin");
        }
    }

    public void exit()
    {
        if (mSettingPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mSettingPlugin.exit();
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for settingplugin");
        }
    }

    public int getSubPlatformId()
    {
        if (mSettingPlugin == null)
        {
            VASLogUtil.e("no instance for settingplugin");
            return -1;
        }

        return mSettingPlugin.getSubPlatformId();
    }

    public String getExtrasConfig(final String paramString)
    {
        if (mSettingPlugin == null)
        {
            VASLogUtil.e("no instance for settingplugin");
            return "";
        }
        return mSettingPlugin.getExtrasConfig(paramString);
    }

    public boolean isFunctionSupported(final int paramInt)
    {
        if (mSettingPlugin == null)
        {
            VASLogUtil.e("no instance for settingplugin");
            return false;
        }
        return mSettingPlugin.isFunctionSupported(paramInt);
    }

    public String callFunction(final int paramInt)
    {
        if (mSettingPlugin == null)
        {
            VASLogUtil.e("no instance for settingplugin");
            return "";
        }
        return mSettingPlugin.callFunction(paramInt);
    }

    public boolean isSDKShowExitDialog()
    {
        if (mSettingPlugin == null)
        {
            VASLogUtil.e("no instance for settingplugin");
            return false;
        }
        return mSettingPlugin.isSDKShowExitDialog();
    }

}
