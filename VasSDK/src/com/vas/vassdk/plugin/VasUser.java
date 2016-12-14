package com.vas.vassdk.plugin;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.factory.VasFacktory;
import com.vas.vassdk.util.VASLogUtil;

public class VasUser
{
    private volatile static VasUser instance;

    private IUserPlugin mUserPlugin;

    private VasUser()
    {

    }

    public static VasUser getInstance()
    {
        if (instance == null)
        {
            synchronized (VasUser.class)
            {
                if (instance == null)
                {
                    instance = new VasUser();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init()
    {
        mUserPlugin = (IUserPlugin) VasFacktory.newPlugin(IUserPlugin.PLUGIN_TYPE);
        VASLogUtil.d("VasUser init");
    }

    public void login()
    {
        if (mUserPlugin != null)
        {
            VASLogUtil.d("mUserPlugin = " + mUserPlugin);
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mUserPlugin.login();
                    VASLogUtil.d("VasUser login");
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for userPlugin");
        }
    }

    public void logout()
    {
        if (mUserPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mUserPlugin.logout();
                    VASLogUtil.d("VasUser logout");
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for userPlugin");
        }
    }

    public VasUserInfo getUserInfo()
    {
        if (mUserPlugin == null)
        {
            VASLogUtil.e("no instance for userPlugin");
            return null;
        }
        return mUserPlugin.getUserInfo();
    }

    public void setGameRoleInfo(final VasRoleInfo roleInfo, final boolean isCreateRole)
    {
        if (mUserPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mUserPlugin.setGameRoleInfo(roleInfo, isCreateRole);
                    VASLogUtil.d("VasUser setGameRoleInfo");
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for userPlugin");
        }
    }

}
