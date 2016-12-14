package com.vas.vassdk.callback;

public interface VasInitCallback
{

    void onSuccess();
    
    void onFailed(String message, String trace);
    
}
