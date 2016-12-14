package com.vas.vassdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.vas.vassdk.apiadapter.IActivityAdapter;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.callback.VasExitCallback;
import com.vas.vassdk.callback.VasInitCallback;
import com.vas.vassdk.callback.VasLoginCallback;
import com.vas.vassdk.callback.VasLogoutCallback;
import com.vas.vassdk.callback.VasPayCallback;
import com.vas.vassdk.callback.VasSwitchAccountCallback;
import com.vas.vassdk.plugin.VasPay;
import com.vas.vassdk.plugin.VasSetting;
import com.vas.vassdk.plugin.VasUser;
import com.vas.vassdk.util.VASLogUtil;

public class VasSDK
{

    private Activity mActivity;

    private static VasSDK instance;

    private Handler mainThreadHandler;

    private VasInitCallback mVasInitCallback;
    
    private VasLoginCallback mVasLoginCallback;
    
    private VasLogoutCallback mVasLogoutCallback;
    
    private VasPayCallback mVasPayCallback;
    
    private VasExitCallback mVasExitCallback;
    
    private VasSwitchAccountCallback mVasSwitchAccountCallback;
    
    private IActivityAdapter mIactivityAdapter;

    public static VasSDK getInstance()
    {
        if (instance == null)
        {
            instance = new VasSDK();
        }
        return instance;
    }

    private VasSDK()
    {
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void runOnMainThread(Runnable runnable)
    {
        if (mainThreadHandler != null)
        {
            mainThreadHandler.post(runnable);
            return;
        }

        if (mActivity != null)
        {
            mActivity.runOnUiThread(runnable);
        }
    }

    public void initAllConfigs(Context context){
        VasSDKConfig.getInstance().loadConfig(context);
    }
    
    
    public void init(Activity activity)
    {
        this.mActivity = activity;
        VasUser.getInstance().init();
        VasPay.getInstance().init();
        VasSetting.getInstance().init();
    }

    public Activity getActivity()
    {
        return mActivity;
    }
    
    public void setActivityListener(IActivityAdapter activityListener) {
        this.mIactivityAdapter = activityListener;
        VASLogUtil.d("setActivityListener");
    }

    public void setVasInitCallback(VasInitCallback vasInitCallback)
    {
        this.mVasInitCallback = vasInitCallback;
    }
    
    public void setVasLoginCallback(VasLoginCallback vasLoginCallback)
    {
        this.mVasLoginCallback = vasLoginCallback;
    }
    
    public void setVasLogoutCallback(VasLogoutCallback vasLogoutCallback){
        this.mVasLogoutCallback = vasLogoutCallback;
    }
    
    public void setVasPayCallback(VasPayCallback vasPayCallback){
        this.mVasPayCallback = vasPayCallback;
    }
    
    public void setVasSwitchAccountCallback(VasSwitchAccountCallback vasSwitchAccountCallback){
        this.mVasSwitchAccountCallback = vasSwitchAccountCallback;
    }
    
    public void setVasExitCallback(VasExitCallback vasExitCallback){
        this.mVasExitCallback = vasExitCallback;
    }

    public VasInitCallback getVasInitCallback()
    {
        if (mVasInitCallback == null)
        {
            return new VasInitCallback()
            {

                @Override
                public void onSuccess()
                {

                }

                @Override
                public void onFailed(String message, String trace)
                {

                }
            };
        }

        return mVasInitCallback;
    }
    
    public VasLoginCallback getVasLoginCallback()
    {
        if (mVasLoginCallback == null)
        {
            return new VasLoginCallback()
            {
                
                @Override
                public void onSuccess(VasUserInfo paramUserInfo)
                {
                }
                
                @Override
                public void onFailed(String paramString1, String paramString2)
                {
                    
                }
                
                @Override
                public void onCancel()
                {
                }
            };
        }
        
        return mVasLoginCallback;
    }
    
    public VasLogoutCallback getVasLogoutCallback(){
        if(mVasLogoutCallback == null){
            mVasLogoutCallback = new VasLogoutCallback()
            {
                
                @Override
                public void onSuccess()
                {
                }
                
                @Override
                public void onFailed(String message, String trace)
                {
                }
            };
        }
        return mVasLogoutCallback;
    }
    
    public VasPayCallback getVasPayCallback(){
        if(mVasPayCallback == null){
            mVasPayCallback = new VasPayCallback()
            {
                
                @Override
                public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams)
                {
                }
                
                @Override
                public void onFailed(String cpOrderID, String message, String trace)
                {
                }
                
                @Override
                public void onCancel(String cpOrderID)
                {
                }
            };
        }
        return mVasPayCallback;
    }
    
    public VasExitCallback getVasExitCallback(){
        if(mVasExitCallback == null){
            mVasExitCallback = new VasExitCallback()
            {
                
                @Override
                public void onSuccess()
                {
                }
                
                @Override
                public void onFailed(String message, String trace)
                {
                }
            };
        }
        return mVasExitCallback;
    }
    
    public VasSwitchAccountCallback getVasSwitchAccountCallback(){
        if(mVasSwitchAccountCallback == null){
            mVasSwitchAccountCallback = new VasSwitchAccountCallback()
            {
                
                @Override
                public void onSuccess(VasUserInfo paramUserInfo)
                {
                }
                
                @Override
                public void onFailed(String message, String trace)
                {
                }
                
                @Override
                public void onCancel()
                {
                }
            };
        }
        return mVasSwitchAccountCallback;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onCreate(savedInstanceState);
        }
        VASLogUtil.d("onCreate");
    }

    public void onPause()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onPause();
        }
        VASLogUtil.d("onPause");
    }

    public void onResume()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onResume();
        }
        VASLogUtil.d("onResume");
    }

    public void onStop()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onStop();
        }
        VASLogUtil.d("onStop");
    }

    public void onRestart()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onRestart();
        }
        VASLogUtil.d("onRestart");
    }

    public void onStart()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onStart();
        }
        VASLogUtil.d("onStart");
    }

    public void onDestroy()
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onDestroy();
        }
        VASLogUtil.d("onDestory");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onActivityResult(requestCode, resultCode, data);
        }
        VASLogUtil.d("onActivityResult");
    }

    public void onNewIntent(Intent paramIntent)
    {
        if (mIactivityAdapter != null) {
            mIactivityAdapter.onNewIntent(paramIntent);
        }
        VASLogUtil.d("onNewIntent");
    }
    
    /**
     * 
     * @return false 游戏不需要添加退出逻辑，true 游戏需要调用自己的退出逻辑
     */
    public boolean onBackPressed(){
        boolean onBackPressed = true;
        if(mIactivityAdapter != null){
            onBackPressed = mIactivityAdapter.onBackPressed();
//            if (onBackPressed) {
//                mActivity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        showExitDialog();
//                    }
//                });
//            }
        }
        VASLogUtil.d("onBackPressed");
        return onBackPressed;
    }

    public void exit()
    {
        VasSetting.getInstance().exit();
        VASLogUtil.d("exit");
    }

    public void setDebugMode(boolean debug)
    {
        VasSDKConfig.getInstance().setDebug(debug);
        VasSetting.getInstance().setDebug(debug);
    }

    public void setIsLandScape(boolean isLandScape)
    {
        VasSetting.getInstance().setIsLandScape(isLandScape);
        VASLogUtil.d("setIsLandScape");
    }


    public void setShowExitDialog(boolean showExitDialog)
    {
        VasSetting.getInstance().setShowExitDialog(showExitDialog);
        VASLogUtil.d("setShowExitDialog");
    }
    
    //获取平台id
    public int getPlatformId(){
        VASLogUtil.d("channelType = " + VasSDKConfig.VAS_PLATFORMID);
        int platformid = -1;
        try
        {
            platformid = Integer.parseInt(VasSDKConfig.VAS_PLATFORMID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return platformid;
    }
    
    //获取子平台id
    public int getSubPlatformId(){
        VASLogUtil.d("subPlatformId = " + VasSetting.getInstance().getSubPlatformId());
        return VasSetting.getInstance().getSubPlatformId();
    }
    
    public String getExtrasConfig(String extrasConfig){
        VASLogUtil.d("getExtrasConfig");
        return VasSetting.getInstance().getExtrasConfig(extrasConfig);
    }
    
    public boolean isFunctionSupported(int supported){
        VASLogUtil.d("isFunctionSupported");
        return VasSetting.getInstance().isFunctionSupported(supported);
    }
    
    public String callFunction(int function){
        VASLogUtil.d("callFunction");
        return VasSetting.getInstance().callFunction(function);
    }
    
    public boolean isSDKShowExitDialog(){
        VASLogUtil.d("isSDKShowExitDialog");
        return VasSetting.getInstance().isSDKShowExitDialog();
    }

    public void login()
    {
        VASLogUtil.d("vas login");
        VasUser.getInstance().login();
    }

    public void logout()
    {
        VASLogUtil.d("vas logout");
        VasUser.getInstance().logout();
    }

    public void pay(VasOrderInfo orderInfo, VasRoleInfo roleInfo)
    {
        VASLogUtil.d("vas pay");
        VasPay.getInstance().pay(orderInfo, roleInfo);
    }

    public void setRoleInfo(Activity activity, VasRoleInfo roleInfo, boolean isCreateRole)
    {
        VASLogUtil.d("vas setRoleInfo");
        VasUser.getInstance().setGameRoleInfo(roleInfo, isCreateRole);
    }
    
    public VasUserInfo getVasUserInfo(){
        VASLogUtil.d("getVasUserInfo");
        return VasUser.getInstance().getUserInfo();
    }
    
    
    private void showExitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity).setTitle("退出游戏")
                .setMessage("您确定要退出游戏么?")
                .setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }


}
