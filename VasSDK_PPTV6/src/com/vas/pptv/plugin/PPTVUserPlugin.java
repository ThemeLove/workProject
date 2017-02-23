package com.vas.pptv.plugin;

import android.app.Activity;

import com.vas.pptv.PPTVSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class PPTVUserPlugin implements IUserPlugin
{

    private Activity mActivity;
    
    public PPTVUserPlugin(Activity activity){
        this.mActivity = activity;
        PPTVSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return PPTVSDK.getInstance().getVasUserInfo();
    }

    @Override
    public void login()
    {
        PPTVSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        PPTVSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        PPTVSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
