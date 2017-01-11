package com.vas.gamecat.plugin;

import android.app.Activity;

import com.vas.gamecat.GCSDK;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.plugin.IUserPlugin;

public class GameCatUserPlugin implements IUserPlugin
{

    private Activity mActivity;
    
    public GameCatUserPlugin(Activity activity){
        this.mActivity = activity;
        GCSDK.getInstance().init();
    }
    
    @Override
    public void login()
    {
        GCSDK.getInstance().login();
    }

    @Override
    public void logout()
    {
        GCSDK.getInstance().logout();
    }

    @Override
    public VasUserInfo getUserInfo()
    {
        return null;
    }

    @Override
    public void setGameRoleInfo(VasRoleInfo paramGameRoleInfo, boolean paramBoolean)
    {
        GCSDK.getInstance().setGameRoleInfo(paramGameRoleInfo, paramBoolean);
    }

}
