package com.vas.gamecat;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.gamecat.common.callback.CallBackUtil;
import com.gamecat.floatwindow.FloatWindowService;
import com.gamecat.pay.fragment.ConfirmPayFragment;
import com.gamecat.sdk.GameCatSDK;
import com.gamecat.sdk.GameCatSDKListener;
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
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;
import com.zwxpay.android.h5_library.manager.CheckOderManager;

public class GCSDK
{

    private static GCSDK instance;
    
    private VasLoadingDialog mDialog;
    
    private Activity mActivity;
    //游戏id规则 ，渠道名称_游戏拼音_系统类型（1.ios，2.安卓）
    private String mGameId;
    
    private String mUid;
    
    private String mAccount;
    
    private boolean mIsDisplayFloatWindow;
    
    public static final int REQUEST_ORDER = 1;
    
    public static GCSDK getInstance(){
        if(instance == null){
            instance = new GCSDK();
        }
        return instance;
    }
    
    private GCSDK(){
        
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
                if (FloatWindowService.getInstance() != null && mIsDisplayFloatWindow) {
                    FloatWindowService.getInstance().showSmallWindow();
                }
                
//                //添加微信支付监听
//                if (ConfirmPayFragment.mIsWeiXinPay && !TextUtils.isEmpty(ConfirmPayFragment.mPrepayId)) {
//                    mActivity.getWindow().getDecorView().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            new CheckOderManager().checkState(mActivity, ConfirmPayFragment.mPrepayId, new CheckOderManager.QueryPayListener() {
//                                @Override
//                                public void getPayState(String payState) {
//                                    VASLogUtil.d("weixin payState : " + payState);
//                                    //返回支付状态，做对应的UI和业务操作
//                                    if ("SUCCESS".equalsIgnoreCase(payState)) {
//                                        CallBackUtil.onSuccess();
//                                    } else {
//                                        CallBackUtil.onFail();
//                                    }
//                                    //把支付标记还原
//                                    ConfirmPayFragment.mIsWeiXinPay = false;
//                                }
//                            });
//                        }
//                    }, 1000);
//                }
                
                
            }
            
            
            @Override
            public void onPause()
            {
                super.onPause();
                if (FloatWindowService.getInstance() != null && mIsDisplayFloatWindow) {
                    FloatWindowService.getInstance().hideSmallWindow();
                }
            }
            
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                closeFloatWindow();
            }
            
            
        });
        //0为联调环境，1为正式环境,2为测试，3为开发
        String aesKey = VasSDKConfig.getInstance().getConfig("AESKEY");
        mGameId = VasSDKConfig.getInstance().getConfig("GAMEID");
        int testEv = VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true") ? 0 : 1;
        GameCatSDK.setEnvironment(mActivity, mGameId, 1, aesKey);
        //渠道sdk没有初始化成功回调，默认给初始化成功回调
        VasSDK.getInstance().getVasInitCallback().onSuccess();
        //登录失效或者注销监听
        GameCatSDK.sdkCancelListener(new GameCatSDKListener() {
            @Override
            public void onSuccess(final JSONObject message) {
                VASLogUtil.d("logout : message = " + message.toString());
                VasSDK.getInstance().getVasLogoutCallback().onSuccess();
                closeFloatWindow();
            }

            @Override
            public void onFail(String message) {
                VasSDK.getInstance().getVasLogoutCallback().onFailed(message, "");
            }
        });
    }
    
    
    
    
    public void login(){
        
        GameCatSDK.Login(mActivity, false, new GameCatSDKListener()
        {
            
            @Override
            public void onSuccess(JSONObject resultJson)
            {
                VASLogUtil.d("login : resultJson = " + resultJson);
                mUid = resultJson.optString("openId");
                mAccount = resultJson.optString("userName");
                if(TextUtils.isEmpty(mAccount)){
                    mAccount = mUid;
                }
                VasUserInfo paramUserInfo = new VasUserInfo();
                paramUserInfo.setToken(resultJson.optString("token"));
                paramUserInfo.setUid(mUid);
                paramUserInfo.setUserName(mAccount);
                VasSDK.getInstance().getVasLoginCallback().onSuccess(paramUserInfo);
                FloatWindowService.startService(mActivity,true);
                mIsDisplayFloatWindow = true;
            }
            
            @Override
            public void onFail(String message)
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed(message, "");
            }
        });
      
    }
    
    
    
    public void logout(){
        //用户注销接口
        GameCatSDK.Logout();
    }
    
    
    
    public void pay(final VasOrderInfo orderinfo,final VasRoleInfo roleInfo){
        
        if(TextUtils.isEmpty(mUid)){
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
                    if(mDialog != null){
                        mDialog.dismiss();
                    }
                }
                
                @Override
                public void onStart(int arg0)
                {
                    if(mDialog != null){
                        mDialog.show();
                    }
                }
                
                @Override
                public void onSucceed(int arg0, Response<String> response)
                {
                    if (response.getHeaders().getResponseCode() == 200)
                    {// 请求成功。
                        String result = response.get();
                        parseOrderResult(result, orderinfo,roleInfo);
                    }else{
                        VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
                    }
                }
                    });

        
       
    }
    
    
    private void parseOrderResult(String result,VasOrderInfo orderinfo,VasRoleInfo roleInfo){
        try
        {
            JSONObject retObj = new JSONObject(result);
            JSONObject dataObj = retObj.optJSONObject("data");
            if(dataObj != null){
                String oid = dataObj.optString("oid");
                orderinfo.setOrderId(oid);//设置聚合SDK的订单号
                channalPay(orderinfo, roleInfo);
            }else{
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "拉取支付页面失败", "");
        }
    }
    
    
    private void channalPay(final VasOrderInfo orderinfo,VasRoleInfo roleInfo){
        //注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/youximao/" + VasSDKConfig.VAS_GAMEID);
        GameCatSDK.Order(mActivity, orderinfo.getAmount(), orderinfo.getGoodsDesc(), orderinfo.getOrderId(), orderinfo.getCallbackUrl(), orderinfo.getExtrasParams(), new GameCatSDKListener()
        {
            
            @Override
            public void onSuccess(JSONObject resultJson)
            {
                VASLogUtil.i("游戏猫支付成功返回：" + resultJson);
                //{"codeNo":"326654e5f79041899be0cfe92cd912a1","message":"支付成功"}
                VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(), orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
            }
            
            @Override
            public void onFail(String message)
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), message, "");
            }
        });
    }
    
    
    
    public void setGameRoleInfo(VasRoleInfo paramGameRoleInfo, boolean paramBoolean){
        
    }
    
    
    private void closeFloatWindow(){
        if (mIsDisplayFloatWindow) {
            FloatWindowService.stopService(mActivity);
            mIsDisplayFloatWindow = false;
        }
    }
    
    
}
