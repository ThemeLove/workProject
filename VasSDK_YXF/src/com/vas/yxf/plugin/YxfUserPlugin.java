package com.vas.yxf.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;
import com.vas.yxf.YxfSDK;

public class YxfUserPlugin implements IUserPlugin
{
    
    public Activity mActivity;
    
    public YxfUserPlugin(Activity activity){
        this.mActivity = activity;
        YxfSDK.getInstance().init();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        YxfSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        YxfSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        YxfSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
