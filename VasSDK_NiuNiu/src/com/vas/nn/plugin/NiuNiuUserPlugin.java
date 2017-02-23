package com.vas.nn.plugin;

import android.app.Activity;

import com.vas.nn.NiuNiuSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class NiuNiuUserPlugin implements IUserPlugin
{
    public Activity mActivity;
    
    public NiuNiuUserPlugin(Activity activity){
        this.mActivity = activity;
        NiuNiuSDK.getInstance().init();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return NiuNiuSDK.getInstance().getVasUserInfo();
    }

    @Override
    public void login()
    {
        NiuNiuSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        NiuNiuSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        NiuNiuSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
