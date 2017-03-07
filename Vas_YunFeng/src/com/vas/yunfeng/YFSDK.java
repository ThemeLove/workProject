package com.vas.yunfeng;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.tandy.android.fw2.helper.ResponseListener;
import com.ungame.android.sdk.Ungame;
import com.ungame.android.sdk.UngameConstants;
import com.ungame.android.sdk.entity.GameOrderInfo;
import com.ungame.android.sdk.listener.OnProcessListener;
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

public class YFSDK
{
    
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;

    private static YFSDK instance;
    
    private YFSDK(){
        
    }
    
    public static YFSDK getInstance(){
        if(instance == null){
            instance = new YFSDK();
        }
        return instance;
    }
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter(){
            
            @Override
            public boolean onBackPressed()
            {
                return true;
            }
            
            
            @Override
            public void onResume()
            {
                super.onResume();
                Ungame.getInstance().showFloatingView();
            }
            
            @Override
            public void onPause()
            {
                super.onPause();
                Ungame.getInstance().hideFloatingView();
            }
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                Ungame.getInstance().destroyFloatingView();
                Ungame.getInstance().finish();
            }
            
        });
        channelInit();
    }
    
    private void channelInit(){
        
        int orientation = 0;
        String vasOrientation = VasSDKConfig.getInstance().getConfig("VAS_ORIENTATION");
        if("0".equalsIgnoreCase(vasOrientation)){
            orientation = UngameConstants.SCREEN_ORIENTATION_LANDSCAPE;
        }else{
            orientation = UngameConstants.SCREEN_ORIENTATION_PORTRAIT;
        }
        Ungame.getInstance().setScreenOrientation(orientation);
        Ungame.getInstance().initUngame(mActivity, new OnProcessListener()
        {
            
            @Override
            public void finishProcess(int code, Bundle bundle)
            {
                switch (code) {
                    case UngameConstants.RECEIVER_CODE.INIT_COMPLETE:
                        //初始化完成
                        VasSDK.getInstance().getVasInitCallback().onSuccess();
                        break;
                    case UngameConstants.RECEIVER_CODE.NETWORK_ERROR:
                        //网络异常
                        VasSDK.getInstance().getVasInitCallback().onFailed("net error!", "");
                        break;
                    case UngameConstants.RECEIVER_CODE.ERROR:
                        VasSDK.getInstance().getVasInitCallback().onFailed("init error!", "");
                        break;

                    case UngameConstants.RECEIVER_CODE.UPDATEVERSION_FORCE_DOWNING:
                        //强制升级
                        VasSDK.getInstance().getVasInitCallback().onFailed("UPDATEVERSION_FORCE_DOWNING!", "");
                        break;
                    case UngameConstants.RECEIVER_CODE.UPDATEVERSION_UNMOUNTED_SDCARD:
                        VasSDK.getInstance().getVasInitCallback().onFailed("UPDATEVERSION_UNMOUNTED_SDCARD!", "");
                        break;
                }
            }
        });
    }
    
    public void login(){
        Ungame.getInstance().login(new OnProcessListener()
        {
            
            @Override
            public void finishProcess(int code, Bundle bundle)
            {
                switch (code) {
                    case UngameConstants.RECEIVER_CODE.LOGIN_SUCCESS:
                        //登录成功
                        //1、开启悬浮小球
                        Ungame.getInstance().startFloatView();
                        mUid = String.valueOf(Ungame.getInstance().getUserId());
                        mAccount = Ungame.getInstance().getUserInfo().getAccount();
                        if(TextUtils.isEmpty(mAccount)){
                            mAccount = mUid;
                        }
                        mToken = Ungame.getInstance().getUserToken();
                        VasUserInfo userInfo = new VasUserInfo();
                        userInfo.setUid(mUid);
                        userInfo.setUserName(mAccount);
                        userInfo.setToken(mToken);
                        VasSDK.getInstance().getVasLoginCallback().onSuccess(userInfo);
                        
                        Ungame.getInstance().setMessageCallBack(messageOnProcessListener);

                        break;
                    default:
                        VasSDK.getInstance().getVasLoginCallback().onFailed("login fail!", "");
                        break;
                }
            }
        });
    }
    
    OnProcessListener messageOnProcessListener = new OnProcessListener() {

        @Override
        public void finishProcess(int code, Bundle bundle) {

            switch (code) {
                // 支付完成
                case UngameConstants.RECEIVER_CODE.PAY_COMPLETE:
                    VasSDK.getInstance().getVasPayCallback().onSuccess("", "", "");
                    break;

                case UngameConstants.RECEIVER_CODE.PAY_CANCLE:
                    VasSDK.getInstance().getVasPayCallback().onCancel("pay cancel!");
                    break;
            }
        }
    };
    
    public void logout(){
        Ungame.getInstance().logout(new OnProcessListener() {
            @Override
            public void finishProcess(int code, Bundle bundle) {
                if(code == UngameConstants.RECEIVER_CODE.LOGOUT_SUCCESS) {
                    VasSDK.getInstance().getVasLogoutCallback().onSuccess();
                }else{
                    VasSDK.getInstance().getVasLogoutCallback().onFailed("logout fail!", "");
                }
                
            }
        });
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
    
    private void channelPay(final VasOrderInfo orderinfo, VasRoleInfo roleInfo){
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/yunfeng/" + VasSDKConfig.VAS_GAMEID);
        GameOrderInfo gameOrderInfo = new GameOrderInfo();
        gameOrderInfo.setOrderTitle(orderinfo.getGoodsDesc());
        gameOrderInfo.setGameOrderNo(orderinfo.getOrderId());
        gameOrderInfo.setItemName(orderinfo.getGoodsName());
        gameOrderInfo.setItemCode(orderinfo.getGoodsId());
        gameOrderInfo.setUnitPrice(orderinfo.getAmount());
        gameOrderInfo.setTotalPrice(orderinfo.getAmount());
        Ungame.getInstance().openPayCenter(gameOrderInfo);
    }
    
    public void exit(){
        
    }
    
    
    public void setGameRoleInfo(VasRoleInfo roleInfo, boolean isCreateRole){
        Ungame.getInstance().setAreaName(roleInfo.getServerName());
        Ungame.getInstance().setAreaCode(roleInfo.getServerId());
        Ungame.getInstance().setRoleName(roleInfo.getRoleName());
        Ungame.getInstance().setRoleCode(roleInfo.getRoleId());
        Ungame.getInstance().pushGameArea(roleInfo.getRoleLevel(), new ResponseListener()
        {
            
            @Override
            public boolean onResponseSuccess(int gact, String response, Object... extras)
            {
                return false;
            }
            
            @Override
            public boolean onResponseError(int gact, String response, VolleyError error, Object... extras)
            {
                return false;
            }
        });
    }
    
}
