package com.vas.tt.plugin;

import android.app.Activity;

import com.vas.tt.TTSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class TTUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public TTUserPlugin(Activity activity){
        this.mActivity = activity;
        TTSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return TTSDK.getInstance().getVasUserInfo();
    }

    @Override
    public void login()
    {
        TTSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        TTSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        TTSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
