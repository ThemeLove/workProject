package com.vas.quicksdk;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.quicksdk.Extend;
import com.quicksdk.QuickSDK;
import com.quicksdk.Sdk;
import com.quicksdk.User;
import com.quicksdk.entity.GameRoleInfo;
import com.quicksdk.entity.OrderInfo;
import com.quicksdk.entity.UserInfo;
import com.quicksdk.notifier.ExitNotifier;
import com.quicksdk.notifier.InitNotifier;
import com.quicksdk.notifier.LoginNotifier;
import com.quicksdk.notifier.LogoutNotifier;
import com.quicksdk.notifier.PayNotifier;
import com.quicksdk.notifier.SwitchAccountNotifier;
import com.vas.vassdk.VasLoadingDialog;
import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.apiadapter.ActivityAdapter;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.http.VasHttpUtil;
import com.vas.vassdk.util.VASLogUtil;
import com.vas.vassdk.util.VasMD5Util;
import com.vas.vassdk.util.VasSDKUtil;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class QKSDK
{

    private static QKSDK instance;

    private Activity mActivity;

    private String mProductCode = "";

    private String mProductKey = "";

    public static final int REQUEST_ORDER = 1;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;

    public static QKSDK getInstance()
    {
        if (instance == null)
        {
            instance = new QKSDK();
        }
        return instance;
    }

    private QKSDK()
    {

    }

    public void init()
    {
        mActivity = VasSDK.getInstance().getActivity();
        mProductCode = VasSDKConfig.getInstance().getConfig("PRODUCTCODE");
        mProductKey = VasSDKConfig.getInstance().getConfig("PRODUCTKEY");
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter()
        {

            @Override
            public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);

                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onCreate(mActivity);
            }

            @Override
            public void onStart()
            {
                super.onStart();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onStart(mActivity);
            }

            @Override
            public void onResume()
            {
                super.onResume();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onResume(mActivity);
            }

            @Override
            public void onPause()
            {
                super.onPause();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onPause(mActivity);
            }

            @Override
            public void onStop()
            {
                super.onStop();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onStop(mActivity);
            }

            @Override
            public void onRestart()
            {
                super.onRestart();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onRestart(mActivity);
            }

            @Override
            public void onDestroy()
            {
                super.onDestroy();
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onDestroy(mActivity);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onActivityResult(mActivity, requestCode, resultCode, data);
            }

            @Override
            public void onNewIntent(Intent newIntent)
            {
                super.onNewIntent(newIntent);
                // 生命周期接口调用(必接)
                com.quicksdk.Sdk.getInstance().onNewIntent(newIntent);
            }

            @Override
            public boolean onBackPressed()
            {

                // 先判断渠道是否有退出框，如果有则直接调用quick的exit接口
                if (QuickSDK.getInstance().isShowExitDialog())
                {
                    exit();
                    return false;
                }
                return true;
            }

        });

        initChannel();

    }

    /**
     * 初始化渠道
     */
    private void initChannel()
    {
        // 设置通知，用于监听初始化，登录，注销，支付及退出功能的返回值(必接)
        initQkNotifiers();
        // quicksdk初始化
        com.quicksdk.Sdk.getInstance().init(mActivity, mProductCode, mProductKey);
    }

    public void login()
    {
        com.quicksdk.User.getInstance().login(mActivity);
    }

    public void logout()
    {
        com.quicksdk.User.getInstance().logout(mActivity);
    }

    private void channelPay(VasOrderInfo vasOrderInfo, VasRoleInfo roleInfo)
    {
        String callbackUrl = "http://game.g.pptv.com/notify/quicksdk/" + VasSDKConfig.VAS_GAMEID;
        vasOrderInfo.setCallbackUrl(callbackUrl);// 这个地方设置聚合SDK通知url

        GameRoleInfo gameRoleInfo = new GameRoleInfo();
        gameRoleInfo.setGameBalance(roleInfo.getGameBalance());
        gameRoleInfo.setGameRoleID(roleInfo.getRoleId());
        gameRoleInfo.setGameRoleName(roleInfo.getRoleName());
        gameRoleInfo.setGameUserLevel(roleInfo.getRoleLevel());
        gameRoleInfo.setPartyName(roleInfo.getPartyName());
        gameRoleInfo.setRoleCreateTime(roleInfo.getRoleCreateTime());
        gameRoleInfo.setServerID(roleInfo.getServerId());
        gameRoleInfo.setServerName(roleInfo.getServerName());
        gameRoleInfo.setVipLevel(roleInfo.getVipLevel());

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAmount(vasOrderInfo.getAmount());
        orderInfo.setCallbackUrl(vasOrderInfo.getCallbackUrl());
        orderInfo.setCount(vasOrderInfo.getCount());
        // 这里需要传聚合SDK生成的订单号
        orderInfo.setCpOrderID(vasOrderInfo.getOrderId());
        orderInfo.setExternalParams(vasOrderInfo.getExternalParams());
        orderInfo.setExtrasParams(vasOrderInfo.getExtrasParams());
        orderInfo.setGoodsDesc(vasOrderInfo.getGoodsDesc());
        orderInfo.setGoodsID(vasOrderInfo.getGoodsId());
        orderInfo.setGoodsName(vasOrderInfo.getGoodsName());
        // orderInfo.setPrice(vasOrderInfo.getPrice());
        orderInfo.setQuantifier(vasOrderInfo.getQuantifier());

        com.quicksdk.Payment.getInstance().pay(mActivity, orderInfo, gameRoleInfo);
    }

    public void setRoleInfo(VasRoleInfo roleInfo, boolean isCreateRole)
    {
        GameRoleInfo info = new GameRoleInfo();
        info.setGameBalance(roleInfo.getGameBalance());
        info.setGameRoleID(roleInfo.getRoleId());
        info.setGameRoleName(roleInfo.getRoleName());
        info.setGameUserLevel(roleInfo.getRoleLevel());
        info.setPartyName(roleInfo.getPartyName());
        info.setRoleCreateTime(roleInfo.getRoleCreateTime());
        info.setServerID(roleInfo.getServerId());
        info.setServerName(roleInfo.getServerName());
        info.setVipLevel(roleInfo.getVipLevel());
        com.quicksdk.User.getInstance().setGameRoleInfo(mActivity, info, isCreateRole);
    }

    public VasUserInfo getVasUserInfo()
    {
        UserInfo userInfo = User.getInstance().getUserInfo(mActivity);
        VasUserInfo vasUserInfo = new VasUserInfo();
        vasUserInfo.setToken(userInfo.getToken());
        vasUserInfo.setUid(userInfo.getUID());
        vasUserInfo.setUserName(userInfo.getUserName());
        vasUserInfo.setExtra(getExtra());
        return vasUserInfo;
    }

    public void setDebugMode(boolean debug)
    {
        QuickSDK.getInstance().setDebugMode(debug);
    }

    public void setIsLandScape(boolean isLandScape)
    {
        // 设置横竖屏，游戏横屏为true，游戏竖屏为false(必接)
        QuickSDK.getInstance().setIsLandScape(isLandScape);
    }

    public void setShowExitDialog(boolean showExitDialog)
    {
        QuickSDK.getInstance().setShowExitDialog(showExitDialog);
    }

    public int getChannelType()
    {
        return Extend.getInstance().getChannelType();
    }

    public String getExtrasConfig(String extrasConfig)
    {
        return Extend.getInstance().getExtrasConfig(extrasConfig);
    }

    public boolean isFunctionSupported(int paramInt)
    {
        return Extend.getInstance().isFunctionSupported(paramInt);
    }

    public String callFunction(int paramInt)
    {
        return Extend.getInstance().callFunction(mActivity, paramInt);
    }

    public boolean isSDKShowExitDialog()
    {
        return Sdk.getInstance().isSDKShowExitDialog();
    }

    /**
     * 退出
     */
    public void exit()
    {
        Sdk.getInstance().exit(mActivity);
    }

    /**
     * 设置通知，用于监听初始化，登录，注销，支付及退出功能的返回值
     */
    private void initQkNotifiers()
    {
        QuickSDK.getInstance()
        // 1.设置初始化通知(必接)
        .setInitNotifier(new InitNotifier()
        {

            @Override
            public void onSuccess()
            {
                VasSDK.getInstance().getVasInitCallback().onSuccess();
            }

            @Override
            public void onFailed(String message, String trace)
            {
                VasSDK.getInstance().getVasInitCallback().onFailed(message, trace);
            }
        })
        // 2.设置登录通知(必接)
        .setLoginNotifier(new LoginNotifier()
        {

            @Override
            public void onSuccess(UserInfo userInfo)
            {
                mUid = userInfo.getUID();
                mAccount = userInfo.getUserName();
                VasUserInfo vasUserInfo = new VasUserInfo();
                vasUserInfo.setUid(mUid);
                vasUserInfo.setToken(userInfo.getToken());
                vasUserInfo.setUserName(mAccount);
                vasUserInfo.setExtra(getExtra());
                VasSDK.getInstance().getVasLoginCallback().onSuccess(vasUserInfo);
            }

            @Override
            public void onCancel()
            {
                VasSDK.getInstance().getVasLoginCallback().onCancel();
            }

            @Override
            public void onFailed(final String message, String trace)
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed(message, trace);
            }

        })
        // 3.设置注销通知(必接)
        .setLogoutNotifier(new LogoutNotifier()
        {

            @Override
            public void onSuccess()
            {
                VasSDK.getInstance().getVasLogoutCallback().onSuccess();
            }

            @Override
            public void onFailed(String message, String trace)
            {
                VasSDK.getInstance().getVasLogoutCallback().onFailed(message, trace);
            }
        })
        // 4.设置切换账号通知(必接)
        .setSwitchAccountNotifier(new SwitchAccountNotifier()
        {

            @Override
            public void onSuccess(UserInfo userInfo)
            {
                VasUserInfo vasUserInfo = new VasUserInfo();
                vasUserInfo.setUid(userInfo.getUID());
                vasUserInfo.setToken(userInfo.getToken());
                vasUserInfo.setUserName(userInfo.getUserName());
                vasUserInfo.setExtra(getExtra());
                VasSDK.getInstance().getVasSwitchAccountCallback().onSuccess(vasUserInfo);
            }

            @Override
            public void onFailed(String message, String trace)
            {
                VasSDK.getInstance().getVasSwitchAccountCallback().onFailed(message, trace);
            }

            @Override
            public void onCancel()
            {
                VasSDK.getInstance().getVasSwitchAccountCallback().onCancel();
            }
        })
        // 5.设置支付通知(必接)
        .setPayNotifier(new PayNotifier()
        {

            @Override
            public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams)
            {

                VASLogUtil.d("QKSDK onPaySuccess: sdkOrderID " + sdkOrderID);

                VasSDK.getInstance().getVasPayCallback().onSuccess(sdkOrderID, cpOrderID, extrasParams);
            }

            @Override
            public void onCancel(String cpOrderID)
            {
                VasSDK.getInstance().getVasPayCallback().onCancel(cpOrderID);
            }

            @Override
            public void onFailed(String cpOrderID, String message, String trace)
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(cpOrderID, message, trace);
            }
        })
        // 6.设置退出通知(必接)
        .setExitNotifier(new ExitNotifier()
        {

            @Override
            public void onSuccess()
            {
                VasSDK.getInstance().getVasExitCallback().onSuccess();
            }

            @Override
            public void onFailed(String message, String trace)
            {
                VasSDK.getInstance().getVasExitCallback().onFailed(message, trace);
            }
        });
    }

    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {

        if (TextUtils.isEmpty(mUid))
        {
            login();
            return;
        }
        Request<String> request = new StringRequest("http://game.g.pptv.com/api/sdk/order/index.php", RequestMethod.GET);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("username", mAccount);
        treeMap.put("open_id", mUid);
        treeMap.put("amount", orderinfo.getAmount() + "");
        treeMap.put("user_id", mUid);
        treeMap.put("ext", orderinfo.getExtrasParams());
        treeMap.put("trade_no", orderinfo.getCpOrderId());
        treeMap.put("gid", VasSDKConfig.VAS_GAMEID);
        treeMap.put("cid", VasSDKConfig.VAS_CHANNELID);
        treeMap.put("ccid", VasSDKConfig.VAS_SUBCHANNEL_ID);
        treeMap.put("sid", roleInfo.getServerId());
        treeMap.put("roid", roleInfo.getRoleId());
        treeMap.put("platform", VasSDKConfig.VAS_PLATFORMID);
        treeMap.put("sub_platform", getChannelType() + "");
        String sign = new VasMD5Util().MD5EncryptString(treeMap, "2aaa08de964854800c204e400006f45b");
        request.add(treeMap);
        request.add("sign", sign);
        VasHttpUtil.getInstance().add(REQUEST_ORDER, request, new OnResponseListener<String>()
        {

            @Override
            public void onFailed(int arg0, String arg1, Object arg2, Exception arg3, int arg4, long arg5)
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
            }

            @Override
            public void onFinish(int arg0)
            {
                if (mDialog != null)
                {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onStart(int arg0)
            {
                if (mDialog != null)
                {
                    mDialog.show();
                }
            }

            @Override
            public void onSucceed(int arg0, Response<String> response)
            {
                if (response.getHeaders().getResponseCode() == 200)
                {// 请求成功。
                    String result = response.get();
                    parseOrderResult(result, orderinfo, roleInfo);
                }
                else
                {
                    VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
                }
            }
        });

    }

    private void parseOrderResult(String result, VasOrderInfo orderinfo, VasRoleInfo roleInfo)
    {
        try
        {
            JSONObject retObj = new JSONObject(result);
            JSONObject dataObj = retObj.optJSONObject("data");
            if (dataObj != null)
            {
                String oid = dataObj.optString("oid");
                orderinfo.setOrderId(oid);// 设置聚合SDK的订单号
                channelPay(orderinfo, roleInfo);
            }
            else
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
        }
    }

    /**
     * 获取扩展参数，json里面key需要和服务端人员 事先约定好
     * 
     * @return
     */
    private String getExtra()
    {
        String extra = VasSDKUtil.toJSON(String.format("{product_code:'%s'}", mProductCode)).toString();
        VASLogUtil.d("extra : " + extra);
        return extra;
    }

}
