package com.vas.dianyou.plugin;

import android.app.Activity;

import com.vas.dianyou.DianYouSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class DYUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public DYUserPlugin(Activity activity){
        this.mActivity = activity;
        DianYouSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        DianYouSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        
    }

}
