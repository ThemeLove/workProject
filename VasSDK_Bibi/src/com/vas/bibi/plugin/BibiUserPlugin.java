package com.vas.bibi.plugin;

import android.app.Activity;

import com.vas.bibi.VasBiBiSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class BibiUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public BibiUserPlugin(Activity activity){
        this.mActivity = activity;
        VasBiBiSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        VasBiBiSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        VasBiBiSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        VasBiBiSDK.getInstance().setGameRoleInfo(arg0, arg1);  
    }

}
