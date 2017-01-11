package com.vas.quicksdk.plugin;

import android.app.Activity;

import com.vas.quicksdk.QKSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class QuickUserPlugin implements IUserPlugin
{
    
    private Activity mActivity;
    
    public QuickUserPlugin(Activity activity){
        this.mActivity = activity;
        QKSDK.getInstance().init();
    }
    

    @Override
    public void login()
    {
        QKSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        QKSDK.getInstance().login();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return QKSDK.getInstance().getVasUserInfo();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo paramGameRoleInfo, boolean paramBoolean)
    {
        QKSDK.getInstance().setRoleInfo(paramGameRoleInfo, paramBoolean);
    }

}
