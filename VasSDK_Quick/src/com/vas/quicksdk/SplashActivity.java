package com.vas.quicksdk;

import android.content.Intent;
import android.graphics.Color;

import com.quicksdk.QuickSdkSplashActivity;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.util.VASLogUtil;

public class SplashActivity extends QuickSdkSplashActivity
{

    @Override
    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public void onSplashStop() {
        // 闪屏结束后，跳转到游戏界面
        
        startGameActivity();
    }
    
    
    private void startGameActivity() {
        try {
            VASLogUtil.d("startGameActivity : " + VasSDKConfig.MAIN_ACTIVITY_NAME);
            Class<?> mainClass = Class.forName(VasSDKConfig.MAIN_ACTIVITY_NAME);
            Intent intent = new Intent(this, mainClass);
            startActivity(intent);
            SplashActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    
    
}
