package com.vas.vassdk.plugin;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.factory.VasFacktory;
import com.vas.vassdk.util.VASLogUtil;

public class VasPay
{

    private static VasPay instance;

    private IPayPlugin mPayPlugin;

    public static VasPay getInstance()
    {
        if (instance == null)
        {
            instance = new VasPay();
        }
        return instance;
    }

    private VasPay()
    {

    }

    public void init()
    {
        mPayPlugin = (IPayPlugin) VasFacktory.newPlugin(IPayPlugin.PLUGIN_TYPE);
        VASLogUtil.d("VasPay init");
    }

    public String getPayParams()
    {
        if (mPayPlugin == null)
        {
            VASLogUtil.e("no instance for payPlugin");
            return "";
        }
        return mPayPlugin.getPayParams();
    }

    public void pay(final VasOrderInfo orderInfo, final VasRoleInfo roleInfo)
    {
        if (mPayPlugin != null)
        {
            VasSDK.getInstance().runOnMainThread(new Runnable()
            {

                @Override
                public void run()
                {
                    mPayPlugin.pay(orderInfo, roleInfo);
                }
            });
        }
        else
        {
            VASLogUtil.e("no instance for payPlugin");
        }
    }

}
