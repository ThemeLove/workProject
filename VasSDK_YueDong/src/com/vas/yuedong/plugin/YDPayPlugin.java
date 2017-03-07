package com.vas.yuedong.plugin;

import android.app.Activity;

import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;
import com.vas.yuedong.YueDongSDK;

public class YDPayPlugin implements IPayPlugin
{

    public Activity mActivity;
    
    public YDPayPlugin(Activity activity){
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
        YueDongSDK.getInstance().pay(arg0, arg1);
    }

}
