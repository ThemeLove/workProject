package com.vas.gamecat.plugin;

import android.app.Activity;

import com.vas.gamecat.GCSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class GameCatPayPlugin implements IPayPlugin
{
    
    public Activity mActivity;
    
    public GameCatPayPlugin(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public String getPayParams()
    {
        return null;
    }

    @Override
    public void pay(VasOrderInfo orderInfo, VasRoleInfo roleInfo)
    {
        GCSDK.getInstance().pay(orderInfo,roleInfo);
    }

}
