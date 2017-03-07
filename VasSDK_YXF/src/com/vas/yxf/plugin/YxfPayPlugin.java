package com.vas.yxf.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;
import com.vas.yxf.YxfSDK;

public class YxfPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public YxfPayPlugin(Activity activity){
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
        YxfSDK.getInstance().pay(arg0, arg1);
    }

}
