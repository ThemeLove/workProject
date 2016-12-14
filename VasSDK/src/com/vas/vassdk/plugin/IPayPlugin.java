package com.vas.vassdk.plugin;

import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;

public interface IPayPlugin
{

    public static final int PLUGIN_TYPE = VasSDKConfig.PLUGIN_TYPE_PAY;
    
    public String getPayParams();
    
    public void pay(VasOrderInfo orderInfo,VasRoleInfo roleInfo);
}
