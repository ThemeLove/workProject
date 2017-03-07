package com.vas.yuedong.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;
import com.vas.yuedong.YueDongSDK;

public class YDUserPlugin implements IUserPlugin
{

    public Activity mActivity;
    
    public YDUserPlugin(Activity activity){
        this.mActivity = activity;
        YueDongSDK.getInstance().init();
    }
    
    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void login()
    {
        YueDongSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        YueDongSDK.getInstance().logout();
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1)
    {
        YueDongSDK.getInstance().setGameRoleInfo(arg0, arg1);
    }

}
