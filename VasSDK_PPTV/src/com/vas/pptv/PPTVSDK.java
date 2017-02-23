package com.vas.pptv;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.pptv.vassdk.agent.PptvVasAgent;
import com.pptv.vassdk.agent.listener.ExitDialogListener;
import com.pptv.vassdk.agent.listener.LoginListener;
import com.pptv.vassdk.agent.listener.PayListener;
import com.pptv.vassdk.agent.model.LoginResult;
import com.pptv.vassdk.agent.model.PayResult;
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

import android.app.Activity;
import android.text.TextUtils;

public class PPTVSDK
{

    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;

    private static PPTVSDK instance;

    private PPTVSDK()
    {

    }

    public static PPTVSDK getInstance()
    {

        if (instance == null)
        {
            instance = new PPTVSDK();
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
                exit();
                return false;
            }

            @Override
            public void onResume()
            {
                super.onResume();
                if(!TextUtils.isEmpty(mUid)){
                    PptvVasAgent.showFloatingView(mActivity);
                }
            }

            @Override
            public void onPause()
            {
                super.onPause();
            }

            @Override
            public void onDestroy()
            {
                super.onDestroy();
                PptvVasAgent.releseFloatViewWindow();
            }

        });
        channelInit();
    }

    private void channelInit()
    {
        String gameId = VasSDKConfig.getInstance().getConfig("GAMEID");
        boolean isDebug = VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true");
        PptvVasAgent.init(mActivity, gameId, "", "", false);
        PptvVasAgent.setDebugMode(isDebug);
        // 渠道sdk没有初始化成功回调，默认给初始化成功回调
        VasSDK.getInstance().getVasInitCallback().onSuccess();
    }

    public void login()
    {
        PptvVasAgent.startLoginActivity(mActivity, new LoginListener()
        {

            @Override
            public void onLoginSuccess(LoginResult loginResult)
            {
                mUid = loginResult.getUserId();
                mAccount = loginResult.getBindUsrName();
                mToken = loginResult.getSessionId();
                if (TextUtils.isEmpty(mAccount))
                {
                    mAccount = mUid;
                }
                VasUserInfo paramUserInfo = new VasUserInfo();
                paramUserInfo.setUid(mUid);
                paramUserInfo.setUserName(mAccount);
                paramUserInfo.setToken(mToken);
                VasSDK.getInstance().getVasLoginCallback().onSuccess(paramUserInfo);
                PptvVasAgent.showFloatingView(mActivity);
            }

            @Override
            public void onLoginCancel()
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed("登录失败", "");
            }
        });
    }

    public void logout()
    {
        VasSDK.getInstance().getVasLogoutCallback().onSuccess();
        PptvVasAgent.releseFloatViewWindow();
    }

    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {
        if(orderinfo == null || roleInfo == null){
            VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "请查看支付参数", "");
            return;
        }
        
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

    private void channalPay(final VasOrderInfo orderinfo, VasRoleInfo roleInfo)
    {
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/pptv/" + VasSDKConfig.VAS_GAMEID);
        String amount = String.valueOf(orderinfo.getAmount());
        PptvVasAgent.startJHPayActivity(mActivity, roleInfo.getServerId(), roleInfo.getRoleId(),
                orderinfo.getExtrasParams(), 1, amount, orderinfo.getGoodsName(), orderinfo.getOrderId(),new PayListener()
                {

                    @Override
                    public void onPayWait(PayResult arg0)
                    {
                        VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                                arg0.getMessage(), "");
                    }

                    @Override
                    public void onPaySuccess(PayResult arg0)
                    {
                        VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                                orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
                    }

                    @Override
                    public void onPayFinish()
                    {
                        VasSDK.getInstance().getVasPayCallback().onCancel("");
                    }

                    @Override
                    public void onPayFail(PayResult payResult)
                    {
                        VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                                payResult.getMessage(), "");
                    }
                });
    }

    public void exit()
    {
        PptvVasAgent.onExit(mActivity, new ExitDialogListener()
        {

            @Override
            public void onExit()
            {
                VasSDK.getInstance().getVasExitCallback().onSuccess();
            }

            @Override
            public void onContinue()
            {
                VasSDK.getInstance().getVasExitCallback().onFailed("继续游戏", "");
            }
        });
    }
    
    public VasUserInfo getVasUserInfo()
    {
        VasUserInfo vasUserInfo = new VasUserInfo();
        vasUserInfo.setToken(mToken);
        vasUserInfo.setUid(mUid);
        vasUserInfo.setUserName(mAccount);
        vasUserInfo.setExtra("");
        return vasUserInfo;
    }

    public void setGameRoleInfo(VasRoleInfo paramGameRoleInfo, boolean paramBoolean)
    {
        if(paramBoolean){
            PptvVasAgent.statisticCreateRole(mActivity);
        }else{
            PptvVasAgent.statisticEnterGame(mActivity);
        }
    }

}
