package com.vas.vassdk.callback;

public interface VasLogoutCallback
{

    public void onSuccess();

    public void onFailed(String message, String trace);

}
