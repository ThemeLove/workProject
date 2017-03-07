package com.vas.yunfeng.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;
import com.vas.yunfeng.YFSDK;

public class YFUserPlugin implements IUserPlugin
{
    
    public Activity mActivity;
    
    public YFUserPlugin(Activity activity){
        this.mActivity = activity;
        YFSDK.getInstance().init();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        YFSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        YFSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        YFSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
