package com.vas.vassdk.callback;

public interface VasPayCallback
{

    public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams);

    public void onCancel(String cpOrderID);

    public void onFailed(String cpOrderID, String message, String trace);

}
