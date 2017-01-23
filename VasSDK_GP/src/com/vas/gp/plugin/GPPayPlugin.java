package com.vas.gp.plugin;

import android.app.Activity;

import com.vas.gp.GPSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class GPPayPlugin implements IPayPlugin
{

    private Activity mActivity;
    
    public GPPayPlugin(Activity activity){
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
        GPSDK.getInstance().pay(arg0, arg1);
    }

}
