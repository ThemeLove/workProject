package com.vas.tt.plugin;

import android.app.Activity;

import com.vas.tt.TTSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class TTPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public TTPayPlugin(Activity activity){
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
        TTSDK.getInstance().pay(arg0, arg1);
    }

}
