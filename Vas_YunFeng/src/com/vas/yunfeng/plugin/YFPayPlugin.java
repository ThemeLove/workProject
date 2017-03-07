package com.vas.yunfeng.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;
import com.vas.yunfeng.YFSDK;

public class YFPayPlugin implements IPayPlugin
{
    public Activity mActivity;
    
    public YFPayPlugin(Activity activity){
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
        YFSDK.getInstance().pay(arg0, arg1);
    }

}
