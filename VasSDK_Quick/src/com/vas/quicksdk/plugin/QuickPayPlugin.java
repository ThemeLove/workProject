package com.vas.quicksdk.plugin;

import com.vas.quicksdk.QKSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

import android.app.Activity;

public class QuickPayPlugin implements IPayPlugin
{

    private Activity mActivity;
    
    public QuickPayPlugin(Activity activity){
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
        QKSDK.getInstance().pay(orderInfo, roleInfo);
    }
    
    
}
