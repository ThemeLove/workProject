
package cc.ak.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import cc.ak.sdk.bean.AkPayParam;
import cc.ak.sdk.bean.AkRoleParam;
import cc.ak.sdk.callback.AkResultCallback;
import cc.ak.sdk.listener.IActivityListener;
import cc.ak.sdk.plugin.AkPay;
import cc.ak.sdk.plugin.AkUser;
import cc.ak.sdk.plugin.AkWxEntry;
import cc.ak.sdk.update.CxCheckAppUpdateModel;
import cc.ak.sdk.update.CxCheckUpdateListener;
import cc.ak.sdk.util.AKLogUtil;
import cc.ak.sdk.util.StatisticsUtil;

import org.json.JSONObject;

public class AkSDK {

    private Activity mActivity;
    private static AkSDK instance;
    private Handler mainThreadHandler;
    private AkResultCallback resultCallback;
    private IActivityListener activityListener;

    private AkSDK() {
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public static AkSDK getInstance() {
        if (instance == null) {
            instance = new AkSDK();
        }
        return instance;
    }



    /**
     * 初始化
     * @param activity
     */
    public void init(Activity activity) {
        this.mActivity = activity;

        AkSDKConfig.getInstance().loadConfig(activity);
        AkUser.getInstance().init();
        AkPay.getInstance().init();
        AkWxEntry.getInstance().init();

        new StatisticsUtil(mActivity).active();//投递启动游戏数据
    }

    public void runOnMainThread(Runnable runnable) {
        if (mainThreadHandler != null) {
            mainThreadHandler.post(runnable);
            return;
        }

        if (mActivity != null) {
            mActivity.runOnUiThread(runnable);
        }
    }

    //获取上下文
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * 设置结果回调(客户端调用)
     */
    public void setResultCallback(AkResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    public AkResultCallback getResultCallback() {
        if (resultCallback == null) {
            AKLogUtil.e("AkResultCallback is null");
            return new AkResultCallback() {
                @Override
                public void onResult(int code, JSONObject data) {
                }
            };
        }
        return resultCallback;
    }

    /**
     * 设置Activity监听(渠道实现调用)
     * @param activityListener
     */
    public void setActivityListener(IActivityListener activityListener) {
        this.activityListener = activityListener;
        AKLogUtil.d("setActivityListener");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (activityListener != null) {
            activityListener.onActivityResult(requestCode, resultCode, data);
        }
        AKLogUtil.d("onActivityResult");
    }

    public void onCreate(Bundle savedInstanceState) {
        if (activityListener != null) {
            activityListener.onCreate(savedInstanceState);
        }
        AKLogUtil.d("onCreate");
    }

    public void onStart() {
        if (activityListener != null) {
            activityListener.onStart();
        }
        AKLogUtil.d("onStart");
    }

    public void onPause() {
        if (activityListener != null) {
            activityListener.onPause();
        }
        AKLogUtil.d("onPause");
    }

    public void onResume() {
        if (activityListener != null) {
            activityListener.onResume();
        }
        AKLogUtil.d("onResume");
    }

    public void onNewIntent(Intent newIntent) {
        if (activityListener != null) {
            activityListener.onNewIntent(newIntent);
        }
        AKLogUtil.d("onNewIntent");
    }

    public void onStop() {
        if (activityListener != null) {
            activityListener.onStop();
        }
        AKLogUtil.d("onStop");
    }

    public void onDestroy() {
        if (activityListener != null) {
            activityListener.onDestroy();
        }
        AKLogUtil.d("onDestroy");
    }

    public void onRestart() {
        if (activityListener != null) {
            activityListener.onRestart();
        }
        AKLogUtil.d("onRestart");
    }

    /**
     * 
     * @return false 游戏不需要添加退出逻辑，true 游戏需要调用自己的退出逻辑
     */
    public boolean onBackPressed() {
        boolean onBackPressed = true;
        if (activityListener != null) {
            onBackPressed = activityListener.onBackPressed();
            if (onBackPressed) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showExitDialog();
                    }
                });
            }
        }
        AKLogUtil.d("onBackPressed : " + onBackPressed);
        return onBackPressed;
    }

    public void login() {
        AKLogUtil.d("login");
        AkUser.getInstance().login();
    }

    public void logout() {
        AKLogUtil.d("logout");
        AkUser.getInstance().logout();
    }

    public void createRole(AkRoleParam param) {
        AKLogUtil.d("createRole");
        AkUser.getInstance().createRole(param);
        try {
            AkSDKConfig.sServerId = Integer.parseInt(param.getServerId());
            AkSDKConfig.sRoleName = param.getRoleName();
            new StatisticsUtil(mActivity).createRole(AkSDKConfig.sUid, param.getRoleId(), AkSDKConfig.sRoleName, AkSDKConfig.sServerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AKLogUtil.d("createRole :" + param.toString());
    }

    public void enterGame(AkRoleParam param) {
        AKLogUtil.d("enterGame");
        AkUser.getInstance().enterGame(param);
        try {
            AkSDKConfig.sServerId = Integer.parseInt(param.getServerId());
            AkSDKConfig.sRoleName = param.getRoleName();
            new StatisticsUtil(mActivity).enterGame(AkSDKConfig.sUid, param.getRoleId(), AkSDKConfig.sRoleName, AkSDKConfig.sServerId, param.getRoleLevel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AKLogUtil.d("enterGame :" + param.toString());
    }

    public void roleUpLevel(AkRoleParam param) {
        AKLogUtil.d("roleUpLevel");
        AkUser.getInstance().roleUpLevel(param);
        try {
            AkSDKConfig.sServerId = Integer.parseInt(param.getServerId());
            AkSDKConfig.sRoleName = param.getRoleName();
            new StatisticsUtil(mActivity).roleUpLevel(AkSDKConfig.sUid, param.getRoleId(), AkSDKConfig.sRoleName, AkSDKConfig.sServerId, param.getRoleLevel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AKLogUtil.d("roleUpLevel :" + param.toString());
    }

    public void pay(AkPayParam param) {
        AKLogUtil.d("pay :" + param.toString());
        AkPay.getInstance().pay(param);
    }

    public void weixinExecute(Activity activity) {
        AKLogUtil.d("weixinExecute");
        AkWxEntry.getInstance().execute(activity);
    }

    /**
     * 
     * @param uid 用户标识
     * @param account 用户名称
     */
    public void registerStatistic(String uid, String account) {
//        new StatisticsUtil(mActivity).registerSuccess(uid, account);
    }

    /**
     * 
     * @param uid 用户标识
     * @param account 用户名称
     */
    public void bindGuestStatistic(String uid, String account) {
        new StatisticsUtil(mActivity).bindGuest(uid, account);
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

    /**
     * 检测更新接口，游戏在init后，不管init是否成功，必须调用
     * @param activity
     * @param updateListener
     */
    public void checkUpdateVersion(Activity activity, CxCheckUpdateListener updateListener) {
        new CxCheckAppUpdateModel().checkAppUpdate(activity, updateListener);
    }

}
