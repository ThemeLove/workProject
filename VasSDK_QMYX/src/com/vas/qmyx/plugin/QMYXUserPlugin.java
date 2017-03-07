package com.vas.qmyx.plugin;

import android.app.Activity;

import com.vas.qmyx.VASQMYXSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class QMYXUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public QMYXUserPlugin(Activity activity){
        this.mActivity = activity;
        VASQMYXSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        VASQMYXSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        VASQMYXSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        VASQMYXSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
