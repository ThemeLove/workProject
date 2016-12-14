package com.vas.vassdk.callback;

import com.vas.vassdk.bean.VasUserInfo;

public interface VasLoginCallback
{

    public void onSuccess(VasUserInfo paramUserInfo);
    
    public void onCancel();
    
    public void onFailed(String message, String trace);
    
}
