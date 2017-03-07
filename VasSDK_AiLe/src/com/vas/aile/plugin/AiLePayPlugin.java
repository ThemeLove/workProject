package com.vas.aile.plugin;

import android.app.Activity;

import com.vas.aile.AiLeSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class AiLePayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public AiLePayPlugin(Activity activity){
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
        AiLeSDK.getInstance().pay(arg0, arg1);
    }

}
