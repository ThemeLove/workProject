package com.vas.sy.plugin;

import android.app.Activity;

import com.vas.sy.SY07073SDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class SYUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public SYUserPlugin(Activity activity){
        this.mActivity = activity;
        SY07073SDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        SY07073SDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        SY07073SDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        SY07073SDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
