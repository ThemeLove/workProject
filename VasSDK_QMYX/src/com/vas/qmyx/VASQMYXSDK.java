package com.vas.qmyx;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.haobangnet.gamesdk.QMYXCallbackListener;
import com.haobangnet.gamesdk.QMYXCallbackListenerNullException;
import com.haobangnet.gamesdk.QMYXFloatMenuCallbackListener;
import com.haobangnet.gamesdk.QMYXGameSDKStatusCode;
import com.haobangnet.gamesdk.QMYXLogLevel;
import com.haobangnet.gamesdk.QMYXPaymentInfo;
import com.haobangnet.gamesdk.QMYXSDK;
import com.haobangnet.gamesdk.bean.OrderInfo;
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
import com.vas.vassdk.util.VasStatisticUtil;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class VASQMYXSDK
{
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;

    private String mToken;

    private static final int REQUEST_ORDER = 1;

    private static final int REQUEST_LOGIN = 2;

    private static VASQMYXSDK instance;
    
    private boolean isCreateFloatButton = true;

    private VASQMYXSDK()
    {

    }

    public static VASQMYXSDK getInstance()
    {
        if (instance == null)
        {
            instance = new VASQMYXSDK();
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
            public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
            }

            @Override
            public void onResume()
            {
                super.onResume();
                if(isCreateFloatButton){
                    createChannelFloatButton();
                }
                QMYXSDK.getDefault().show(mActivity, true);
            }

            @Override
            public void onDestroy()
            {
                super.onDestroy();
                QMYXSDK.getDefault().destroyFloatButton(mActivity);
            }

            @Override
            public boolean onBackPressed()
            {
                exit();
                return false;
            }

        });
        channelInit();
    }
    
    private void createChannelFloatButton(){
        try
        {
            QMYXSDK.getDefault().createFloatButton(VasSDK.getInstance().getActivity(), new QMYXFloatMenuCallbackListener()
            {

                @Override
                public void callback(boolean arg0)
                {
                    VASLogUtil.d("createFloatButton arg0 = " + arg0);
                }
            });
            VASLogUtil.d("onCreate() createFloatButton"); 
            isCreateFloatButton = false;
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
            isCreateFloatButton = true;
        }
    }

    private void channelInit()
    {
        boolean isDebug = VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true") ? true : false;
        try
        {
            QMYXSDK.getDefault().initSDK(mActivity, isDebug, QMYXLogLevel.DEBUG, new QMYXCallbackListener<String>()
            {

                @Override
                public void callback(int statuscode, String data)
                {
                    switch (statuscode)
                    {
                        case QMYXGameSDKStatusCode.SUCCESS:
                            VasSDK.getInstance().getVasInitCallback().onSuccess();
                            break;
                        case QMYXGameSDKStatusCode.INIT_FAIL:
                            VasSDK.getInstance().getVasInitCallback().onFailed("init fail", "");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
            VasSDK.getInstance().getVasInitCallback().onFailed("init fail", "");
        }
    }

    public void login()
    {
        try
        {
            QMYXSDK.getDefault().login(mActivity, new QMYXCallbackListener<String>()
            {

                @Override
                public void callback(int code, String msg)
                {
                    switch (code)
                    {
                        case QMYXGameSDKStatusCode.SUCCESS:
                            // 登录成功，可以执行后续操作
                            mToken = QMYXSDK.getDefault().getSessionId();
                            getUserKey(mToken);
                            QMYXSDK.getDefault().show(mActivity, true);
                            break;
                        case QMYXGameSDKStatusCode.LOGIN_EXIT:
                            VasSDK.getInstance().getVasLoginCallback().onCancel();
                            break;
                        case QMYXGameSDKStatusCode.NO_INIT:
                            break;
                        case QMYXGameSDKStatusCode.FAIL:
                            VasSDK.getInstance().getVasLoginCallback().onFailed("login fail", "");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
        }
    }

    private void getUserKey(String token)
    {
        String time = System.currentTimeMillis() + "";
        Request<String> request = new StringRequest("http://game.g.pptv.com/api/sdk/integration/check_user_info.php",
                RequestMethod.POST);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("user_id", "pptv");
        treeMap.put("username", "pptv");
        treeMap.put("token", token);
        treeMap.put("platform", VasSDKConfig.VAS_PLATFORMID);
        treeMap.put("sub_platform", VasSDKConfig.VAS_SUBPLATFORMID);
        treeMap.put("time", time);
        treeMap.put("gid", VasSDKConfig.VAS_GAMEID);
        String sign = new VasMD5Util().MD5EncryptString(treeMap, VasSDKConfig.getInstance().getConfig("VAS_LOGINKEY"));
        request.add(treeMap);
        request.add("sign", sign);
        VasHttpUtil.getInstance().add(REQUEST_LOGIN, request, new OnResponseListener<String>()
        {

            @Override
            public void onFailed(int arg0, String arg1, Object arg2, Exception arg3, int arg4, long arg5)
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed("login fail", "");
            }

            @Override
            public void onFinish(int arg0)
            {
            }

            @Override
            public void onStart(int arg0)
            {
            }

            @Override
            public void onSucceed(int arg0, Response<String> response)
            {
                if (response.getHeaders().getResponseCode() == 200)
                {// 请求成功。
                    String result = response.get();
                    parseLoginResult(result);
                }
                else
                {
                    VasSDK.getInstance().getVasLoginCallback().onFailed("login fail", "");
                }
            }
        });
    }
    
    private void parseLoginResult(String result){
        try
        {
            JSONObject resultObj = new JSONObject(result);
            int code = resultObj.optInt("code");
            if(1 == code){
                JSONObject dataObj = resultObj.optJSONObject("data");
                if(dataObj != null){
                    mUid = dataObj.optString("user_key");
                    mAccount = dataObj.optString("nick_name");
                    if (TextUtils.isEmpty(mAccount))
                    {
                        mAccount = mUid;
                    }
                    VasUserInfo paramUserInfo = new VasUserInfo();
                    paramUserInfo.setUid(mUid);
                    paramUserInfo.setUserName(mAccount);
                    paramUserInfo.setToken(mToken);
                    VasSDK.getInstance().getVasLoginCallback().onSuccess(paramUserInfo);
                    VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
                }else{
                    VasSDK.getInstance().getVasLoginCallback().onFailed("login error", "");
                }
            }else{
                VasSDK.getInstance().getVasLoginCallback().onFailed("login error", "");
            }
        }
        catch (JSONException e)
        {
            VASLogUtil.d("parse login data error!");
            e.printStackTrace();
        }
    }

    public void logout()
    {
        try
        {
            QMYXSDK.getDefault().logout(new QMYXCallbackListener<String>()
            {
                @Override
                public void callback(int statuscode, String data)
                {
                    switch (statuscode)
                    {
                        case QMYXGameSDKStatusCode.NO_INIT:
                            VasSDK.getInstance().getVasLogoutCallback().onFailed("no init!", "");
                            break;
                        case QMYXGameSDKStatusCode.NO_LOGIN:
                            VasSDK.getInstance().getVasLogoutCallback().onFailed("no login!", "");
                            break;
                        case QMYXGameSDKStatusCode.SUCCESS:
                            VasSDK.getInstance().getVasLogoutCallback().onSuccess();
                            QMYXSDK.getDefault().show(mActivity, false);
                            break;
                        case QMYXGameSDKStatusCode.FAIL:
                            VasSDK.getInstance().getVasLogoutCallback().onFailed("logout fail!", "");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
        }
    }

    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {
        if (orderinfo == null || roleInfo == null)
        {
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
            if (!"0".equalsIgnoreCase(status))
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), message, "");
                return;
            }
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

    private void channelPay(final VasOrderInfo orderInfo, final VasRoleInfo vasRoleInfo)
    {
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderInfo.setCallbackUrl("http://game.g.pptv.com/notify/quanmin/" + VasSDKConfig.VAS_GAMEID);
        try
        {
            int serverId = 0;
            float amount = (float) orderInfo.getAmount();
            serverId = Integer.parseInt(vasRoleInfo.getServerId());
            QMYXPaymentInfo paymentInfo = new QMYXPaymentInfo();// 创建Payment对象，用于传递充值信息
            paymentInfo.setAmount(amount);// 单位：元
            // 设置允许充值的金额，此为可选参数，默认为0；
            paymentInfo.setServerId(serverId);// 设置区服的ID
            paymentInfo.setPayDesc(orderInfo.getGoodsDesc());// 设置充值描述
            paymentInfo.setCustomInfo(orderInfo.getExtrasParams());
            // 充值自定义参数，此参数不作任何处理，在充值完成后通知游戏服务器充值结果时原封不动传给游戏服务器。此参数为可选参数，默认为空。
            // 对于需要此参数的游戏，充值前建议先判断下次参数传递的值是否正常不为空再调充值接口，注意长度不能超过120。
            QMYXSDK.getDefault().pay(mActivity, paymentInfo, new QMYXCallbackListener<OrderInfo>()
            {
                @Override
                public void callback(int statuscode, OrderInfo data)
                {
                    switch (statuscode)
                    {
                        case QMYXGameSDKStatusCode.NO_INIT:
                            break;
                        case QMYXGameSDKStatusCode.SUCCESS:
                            VasSDK.getInstance().getVasPayCallback().onSuccess(orderInfo.getOrderId(),
                                    orderInfo.getCpOrderId(), orderInfo.getExtrasParams());

                            break;
                        case QMYXGameSDKStatusCode.FAIL:
                            VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), "pay fail", "");
                            break;
                        case QMYXGameSDKStatusCode.NO_LOGIN:
                            break;
                        case QMYXGameSDKStatusCode.PAY_USER_EXIT:
                            VasSDK.getInstance().getVasPayCallback().onCancel("");
                            break;
                    }
                }
            });
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
            VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), "pay fail", "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), "pay fail", "");
        }
    }

    public void exit()
    {
        try
        {
            QMYXSDK.getDefault().exitSDK(mActivity, new QMYXCallbackListener<String>()
            {
                @Override
                public void callback(int statuscode, String data)
                {
                    switch (statuscode)
                    {
                        case QMYXGameSDKStatusCode.NO_INIT:
                            // QMYXSDK未初始化
                            break;
                        case QMYXGameSDKStatusCode.SUCCESS:
                            // 退出账号成功
                            VasSDK.getInstance().getVasExitCallback().onSuccess();
                            break;
                        case QMYXGameSDKStatusCode.FAIL:
                            // 退出账号失败
                            VasSDK.getInstance().getVasExitCallback().onFailed("exit fail", "");
                            break;
                        default:
                            break;
                    }
                }

            });
        }
        catch (QMYXCallbackListenerNullException e)
        {
            e.printStackTrace();
        }
    }
    
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1){
        if(!arg1){
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
    }


}
