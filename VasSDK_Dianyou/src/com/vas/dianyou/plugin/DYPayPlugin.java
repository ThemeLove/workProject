package com.vas.dianyou.plugin;

import android.app.Activity;

import com.vas.dianyou.DianYouSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class DYPayPlugin implements IPayPlugin
{
    public Activity mActivity;
    
    public DYPayPlugin(Activity activity){
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
        DianYouSDK.getInstance().pay(orderInfo, roleInfo);
    }

}
