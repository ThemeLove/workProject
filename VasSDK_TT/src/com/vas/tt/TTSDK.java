package com.vas.tt;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import com.vas.vassdk.VasLoadingDialog;
import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.apiadapter.ActivityAdapter;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.http.VasHttpUtil;
import com.vas.vassdk.util.VasMD5Util;
import com.wett.cooperation.container.SdkCallback;
import com.wett.cooperation.container.TTSDKV2;
import com.wett.cooperation.container.bean.GameInfo;
import com.wett.cooperation.container.bean.PayInfo;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class TTSDK
{

    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private static TTSDK instance;
    
    private boolean isPrepare = true;
    
    private TTSDK(){
        
    }
    
    public static TTSDK getInstance(){
        if(instance == null){
            instance = new TTSDK();
        }
        return instance;
    }
    
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
//        if(isPrepare){
//            TTSDKV2.getInstance().prepare(mActivity.getApplicationContext());
//            isPrepare = false;
//        }
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter(){
            
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                TTSDKV2.getInstance().onActivityResult(mActivity, requestCode, resultCode, data);
            }
            
            @Override
            public boolean onBackPressed()
            {
                exit();
                return false;//渠道没有退出框的时候返回true
            }
            
            @Override
            public void onRestart()
            {
                super.onRestart();
                TTSDKV2.getInstance().onRestart(mActivity);
            }
            
            @Override
            public void onResume()
            {
                super.onResume();
                TTSDKV2.getInstance().onResume(mActivity);
                if(TTSDKV2.getInstance().isLogin()){
                    TTSDKV2.getInstance().showFloatView(mActivity);
                }
            }
            
            @Override
            public void onPause()
            {
                super.onPause();
                TTSDKV2.getInstance().onPause(mActivity);
                if(TTSDKV2.getInstance().isLogin()){
                    TTSDKV2.getInstance().hideFloatView(mActivity);
                }
            }
            
            @Override
            public void onStop()
            {
                super.onStop();
                TTSDKV2.getInstance().onStop(mActivity);
            }
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                TTSDKV2.getInstance().onDestroy(mActivity);
            }
            
            @Override
            public void onNewIntent(Intent newIntent)
            {
                super.onNewIntent(newIntent);
                TTSDKV2.getInstance().onNewIntent(newIntent);
            }
            
           
            
        });
        channelInit();
    }
    
    private void channelInit(){
        int orientation = 2;//默认横屏
        String orientationStr = VasSDKConfig.getInstance().getConfig("VAS_ORIENTATION");
        try
        {
            orientation = Integer.parseInt(orientationStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            orientation = 2;
        }
        GameInfo gameInfo = new GameInfo();
        TTSDKV2.getInstance().init(mActivity, gameInfo, false, orientation, new SdkCallback<String>(){

            @Override
            protected boolean onResult(int arg0, String arg1)
            {
                if (arg0 == 0) {
                    VasSDK.getInstance().getVasInitCallback().onSuccess();
                    TTSDKV2.getInstance().onCreate(mActivity);
                    channelLogoutListener();
                } else {
                    VasSDK.getInstance().getVasInitCallback().onFailed(arg1, "");
                }
                return false;
            }
            
        });
        
        
        
    }
    
    private void channelLogoutListener(){
        TTSDKV2.getInstance().setLogoutListener(new SdkCallback<String>() {
            @Override
            protected boolean onResult(int i, String s) {
                if (i == 0) {
                    VasSDK.getInstance().getVasLogoutCallback().onSuccess();
                } else {
                    VasSDK.getInstance().getVasLogoutCallback().onFailed("注销失败", "");
                }
                return false;
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
    
    public void login(){
        TTSDKV2.getInstance().login(mActivity, new SdkCallback<String>()
        {
            
            @Override
            protected boolean onResult(int arg0, String arg1)
            {
                if(arg0 == 0){
                    TTSDKV2.getInstance().showFloatView(mActivity);
                    mUid = TTSDKV2.getInstance().getUid();
                    mToken = TTSDKV2.getInstance().getSession();
                    mAccount = TTSDKV2.getInstance().getGameId();
                    VasUserInfo paramUserInfo = new VasUserInfo();
                    paramUserInfo.setUid(mUid);
                    paramUserInfo.setUserName(mAccount);
                    paramUserInfo.setToken(mToken);
                    VasSDK.getInstance().getVasLoginCallback().onSuccess(paramUserInfo);
                }else{
                    VasSDK.getInstance().getVasLoginCallback().onFailed(arg1, "");
                }
                return false;
            }
        });
    }
    
    public void logout(){
        TTSDKV2.getInstance().logout(mActivity);
    }
    
    
    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo){
        if (!TTSDKV2.getInstance().isLogin())
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
    
    private void channelPay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo){
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/ttyuyin/" + VasSDKConfig.VAS_GAMEID);
        PayInfo payInfo = new PayInfo();
        payInfo.setRoleId(roleInfo.getRoleId());//角色id
        payInfo.setRoleName(roleInfo.getRoleName()); //角色名称
        payInfo.setBody(orderinfo.getGoodsDesc()); //商品描述
        payInfo.setCpFee(orderinfo.getAmount());//CP订单金额
        payInfo.setCpTradeNo(orderinfo.getOrderId());//CP订单号
        payInfo.setServerName(roleInfo.getServerId());//游戏服务器id
        payInfo.setExInfo(orderinfo.getExtrasParams()); //CP扩展信息，该字段将会在支付成功后原样返回给CP
        payInfo.setSubject(orderinfo.getGoodsName());//订单商品名称
        payInfo.setPayMethod(payInfo.PAY_METHOD_ALL); //支付方式
        payInfo.setCpCallbackUrl(orderinfo.getCallbackUrl());//游戏方支付回调
        payInfo.setChargeDate(new Date().getTime());//cp充值时间
        TTSDKV2.getInstance().pay(mActivity, payInfo, new SdkCallback<String>() {
            @Override
            protected boolean onResult(int i, String payResponse) {
                if (i == 0) {
                    VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                            orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
                } else {
                    VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                            payResponse, "");
                }
                return true;
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
    
    
    public void setGameRoleInfo(VasRoleInfo roleInfo, boolean paramBoolean){
        int roleLevel = 0;
        try
        {
            roleLevel = Integer.parseInt(roleInfo.getRoleLevel());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Map<String,String> exMapParams = new ArrayMap<String, String>();
        exMapParams.put("scene_Id","");//场景
        exMapParams.put("zoneId","");//游戏区ID
        exMapParams.put("balance",roleInfo.getGameBalance());//游戏币余额
        exMapParams.put("Vip",roleInfo.getVipLevel());//vip等级
        exMapParams.put("partyName",roleInfo.getPartyName());//玩家所属帮派 
        JSONObject jsonObject = new JSONObject(exMapParams);
        String exInfo = jsonObject.toString();
        
        TTSDKV2.getInstance().submitGameRoleInfo(mActivity, roleInfo.getServerName(), roleInfo.getRoleId(), roleInfo.getRoleName(), roleLevel, exInfo);
    }
    
    public void exit(){
        TTSDKV2.getInstance().uninit(mActivity, new SdkCallback<String>()
        {
            
            @Override
            protected boolean onResult(int arg0, String arg1)
            {
                if(arg0 == 0){
                    VasSDK.getInstance().getVasExitCallback().onSuccess();
                }else{
                    VasSDK.getInstance().getVasExitCallback().onFailed(arg1, "");
                }
                return false;
            }
        });
    }
    
}
