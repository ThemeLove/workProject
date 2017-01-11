
package com.example.dkmsdk_demo.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import cc.ak.sdk.AkSDK;

public class WXEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        AkSDK.getInstance().weixinExecute(this);
    }
}
