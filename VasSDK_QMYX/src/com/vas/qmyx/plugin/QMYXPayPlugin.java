package com.vas.qmyx.plugin;

import android.app.Activity;

import com.vas.qmyx.VASQMYXSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class QMYXPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public QMYXPayPlugin(Activity activity){
        this.mActivity = activity;
    }
    
    @Override
    public String getPayParams()
    {
        return null;
    }

    @Override
    public void pay(VasOrderInfo arg0, VasRoleInfo arg1)
    {
        VASQMYXSDK.getInstance().pay(arg0, arg1);
    }

}
