package com.vas.gp.plugin;

import android.app.Activity;

import com.vas.gp.GPSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class GPUserPlugin implements IUserPlugin
{

    private Activity mActivity;
    
    public GPUserPlugin(Activity activity){
        this.mActivity = activity;
        GPSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return GPSDK.getInstance().getUserInfo();
    }

    @Override
    public void login()
    {
        GPSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        GPSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        GPSDK.getInstance().setGameRole(arg0, arg1);
    }

}
