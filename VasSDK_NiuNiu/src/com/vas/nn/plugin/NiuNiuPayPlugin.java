package com.vas.nn.plugin;

import android.app.Activity;

import com.vas.nn.NiuNiuSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class NiuNiuPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public NiuNiuPayPlugin(Activity activity){
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
        NiuNiuSDK.getInstance().pay(arg0, arg1);
    }

}
