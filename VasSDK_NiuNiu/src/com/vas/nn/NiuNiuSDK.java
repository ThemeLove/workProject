package com.vas.nn;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.niuniu.android.sdk.NiuniuGame;
import com.niuniu.android.sdk.extra.NiuniuGameCode;
import com.niuniu.android.sdk.extra.NiuniuGameInfo;
import com.niuniu.android.sdk.extra.NiuniuGameUserInfo;
import com.niuniu.android.sdk.listener.OnProcessListener;
import com.vas.vassdk.VasLoadingDialog;
import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.apiadapter.ActivityAdapter;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.http.VasHttpUtil;
import com.vas.vassdk.util.VasMD5Util;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class NiuNiuSDK
{
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;

    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private VasUserInfo mUserInfo;

    private static NiuNiuSDK instance;

    private NiuNiuSDK()
    {

    }

    public static NiuNiuSDK getInstance()
    {
        if (instance == null)
        {
            instance = new NiuNiuSDK();
        }
        return instance;
    }

    public void init()
    {
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter()
        {

            @Override
            public boolean onBackPressed()
            {

                return true;// 渠道有sdk退出返回false
            }

            @Override
            public void onDestroy()
            {
                super.onDestroy();
                NiuniuGame.getInstance().hiddenLogoButton(mActivity);
                NiuniuGame.getInstance().finish(mActivity);
                NiuniuGame.getInstance().removeMessageCallBack(messageOnProcessListener);
            }

        });
        channelInit();
    }

    private void channelInit()
    {

        int orientation = 0;
        String ori = VasSDKConfig.getInstance().getConfig("VAS_ORIENTATION");
        if ("0".equalsIgnoreCase(ori))
        {
            orientation = NiuniuGame.SCREEN_ORIENTATION_LANDSCAPE;
        }
        else if ("6".equalsIgnoreCase(ori))
        {
            orientation = NiuniuGame.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }
        else if ("7".equalsIgnoreCase(ori))
        {
            orientation = NiuniuGame.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        }
        else
        {
            orientation = NiuniuGame.SCREEN_ORIENTATION_PORTRAIT;
        }
        boolean isDebug = VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true") ? true : false;
        int appId = 0;
        try
        {
            appId = Integer.parseInt(VasSDKConfig.getInstance().getConfig("APPID"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        NiuniuGame.getInstance().setScreenOrientation(orientation);
        NiuniuGame.getInstance().setShowLog(isDebug);
        NiuniuGameInfo mNiuniuInfo = new NiuniuGameInfo();
        mNiuniuInfo.setCtx(mActivity);
        mNiuniuInfo.setAppId(appId);
        mNiuniuInfo.setAppKey(VasSDKConfig.getInstance().getConfig("APPKEY"));
        NiuniuGame.getInstance().init(mNiuniuInfo, new OnProcessListener()
        {

            @Override
            public void finishProcess(int code, Bundle bundle)
            {
                if (NiuniuGameCode.INITCOMPLETE == code)
                {// 初始化完成
                    VasSDK.getInstance().getVasInitCallback().onSuccess();

                    NiuniuGame.getInstance().setMessageCallBack(messageOnProcessListener);
                }
                else if (NiuniuGameCode.UPDATEVERSION_FORCE_DOWNING == code)
                {// 版本强制更新，需要退出游戏
                    VasSDK.getInstance().getVasInitCallback().onFailed("版本强制更新，需要退出游戏", "");
                }
                else if (NiuniuGameCode.UPDATEVERSION_UNMOUNTED_SDCARD == code)
                {// 没有sdcard
                    VasSDK.getInstance().getVasInitCallback().onFailed("没有SD卡，请检查！", "");
                }
                else if (NiuniuGameCode.ERROR == code)
                {// 没有网络或者其它错误
                    VasSDK.getInstance().getVasInitCallback().onFailed(bundle.getString(NiuniuGame.ERROR_MESSAGE), "");
                }
                else
                {
                    VasSDK.getInstance().getVasInitCallback().onFailed("初始化失败", "");
                }
            }
        });
    }

    OnProcessListener messageOnProcessListener = new OnProcessListener()
    {
        @Override
        public void finishProcess(int code, Bundle bundle)
        {
            switch (code)
            {
               
                case NiuniuGameCode.SWITCHACCOUNT:
                    break;
                 // 注销
                case NiuniuGameCode.LOGOUTSUCCESS:
                    VasSDK.getInstance().getVasLogoutCallback().onSuccess();
                    break;
                case NiuniuGameCode.LOGOUTFAIL_NOTLOGIN:
                    VasSDK.getInstance().getVasLogoutCallback().onFailed("", "");
                    break;
                 // 支付完成
                case NiuniuGameCode.PAYCOMPLETE:
                    VasSDK.getInstance().getVasPayCallback().onSuccess("", "", "");
                    break;
                default:
                    break;
            }
        }
    };

    public void login()
    {
        NiuniuGame.getInstance().login(mActivity, new OnProcessListener()
        {
            @Override
            public void finishProcess(int code, Bundle bundle)
            {
                if (NiuniuGameCode.LOGINSUCCESS == code)
                {
                    NiuniuGameUserInfo nnUserInfo = NiuniuGame.getInstance().getUserInfo();
                    mUserInfo = new VasUserInfo();
                    mUid = nnUserInfo.getUserId();
                    mToken = nnUserInfo.getUserToken();
                    mAccount = nnUserInfo.getNickName();
                    mUserInfo.setUid(mUid);
                    mUserInfo.setToken(mToken);
                    mUserInfo.setUserName(mAccount);
                    VasSDK.getInstance().getVasLoginCallback().onSuccess(mUserInfo);
                    NiuniuGame.getInstance().showLogoButton(mActivity);// 显示悬浮框
                }
                else if (NiuniuGameCode.ERROR == code)
                {
                    VasSDK.getInstance().getVasLoginCallback().onFailed("登录错误", "");
                }
                else if (NiuniuGameCode.BACKTOGAME == code)
                {
                    VasSDK.getInstance().getVasLoginCallback().onFailed("返回游戏", "");
                }
            }
        });
    }

    public void logout()
    {
        NiuniuGame.getInstance().logout(mActivity, messageOnProcessListener);
    }
    
    
    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {
        if(orderinfo == null || roleInfo == null){
            VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "请查看支付参数", "");
            return;
        }
        
        if (!NiuniuGame.getInstance().isLogined())
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
        treeMap.put("sub_platform", VasSDKConfig.VAS_SUBPLATFORMID);
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
            String status = retObj.optString("status");
            String message = retObj.optString("message");
            if(!"0".equalsIgnoreCase(status)){
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), message, "");
                return;
            }
            JSONObject dataObj = retObj.optJSONObject("data");
            if (dataObj != null)
            {
                String oid = dataObj.optString("oid");
                orderinfo.setOrderId(oid);// 设置聚合SDK的订单号
                channalPay(orderinfo, roleInfo);
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
    
    private void channalPay(final VasOrderInfo orderinfo, VasRoleInfo roleInfo){
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/niuniu/" + VasSDKConfig.VAS_GAMEID);
        NiuniuGame.getInstance().enterPaymentCenter(mActivity, orderinfo.getGoodsId(), orderinfo.getOrderId(), messageOnProcessListener);
        NiuniuGame.getInstance().setPayExpandData(orderinfo.getExtrasParams());
    }
    

    public void exit()
    {

    }
    
    public VasUserInfo getVasUserInfo(){
        return mUserInfo;
    }

    public void setGameRoleInfo(VasRoleInfo roleInfo, boolean isCreateRole)
    {
        int roleLevel = 0;
        try
        {
            roleLevel = Integer.getInteger(roleInfo.getRoleLevel());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        NiuniuGame.getInstance().setPlayerAndServerInfo(roleInfo.getRoleId(), roleInfo.getRoleName(), roleLevel,
                roleInfo.getServerId(), roleInfo.getServerName());
    }

}
