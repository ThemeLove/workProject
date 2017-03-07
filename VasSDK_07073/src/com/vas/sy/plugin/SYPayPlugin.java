package com.vas.sy.plugin;

import android.app.Activity;

import com.vas.sy.SY07073SDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class SYPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public SYPayPlugin(Activity activity){
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
        SY07073SDK.getInstance().pay(arg0, arg1);
    }

}
