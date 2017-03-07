package com.vas.aile.plugin;

import android.app.Activity;

import com.vas.aile.AiLeSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class AiLeUserPlugin implements IUserPlugin
{
    
    public Activity mActivity;
    
    public AiLeUserPlugin(Activity activity){
        this.mActivity = activity;
        AiLeSDK.getInstance().init();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        AiLeSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        AiLeSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        AiLeSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
