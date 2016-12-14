package com.vas.vassdk.callback;

public interface VasExitCallback
{
    public void onSuccess();
    
    public void onFailed(String message, String trace);
}
