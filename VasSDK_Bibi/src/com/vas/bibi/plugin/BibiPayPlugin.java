package com.vas.bibi.plugin;

import android.app.Activity;

import com.vas.bibi.VasBiBiSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class BibiPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public BibiPayPlugin(Activity activity){
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
        VasBiBiSDK.getInstance().pay(arg0, arg1);
    }

}
