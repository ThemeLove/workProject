package com.vas.gp;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPSDKPlayerInfo;
import com.flamingo.sdk.access.GPUploadPlayerInfoResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUploadPlayerInfoObsv;
import com.flamingo.sdk.access.IGPUserObsv;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class GPSDK
{

    private static GPSDK instance;
    
    private Activity mActivity;
    
    private VasLoadingDialog mDialog;
    
    private String mUid;
    
    private String mAccount;
    
    private String mToken;
    
    private int mInitCount = 0;
    
    private boolean mIsInitSuc = false;
    
    private String mAppid;
    
    private String mAppkey;
    
    private VasUserInfo mUserInfo;
    
    private static final int REQUEST_ORDER = 1;
    
    private GPSDK(){
        
    }
    
    public static GPSDK getInstance(){
        if(instance == null){
            instance = new GPSDK();
        }
        return instance;
    }
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter(){
            
            @Override
            public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                GPApiFactory.getGPApi().onCreate(mActivity);
            }
            
            @Override
            public void onStart() {
                super.onStart();
                GPApiFactory.getGPApi().onStart(mActivity);
            }

            @Override
            public void onRestart() {
                super.onRestart();
                GPApiFactory.getGPApi().onRestart(mActivity);
            }

            @Override
            public void onResume() {
                super.onResume();
                GPApiFactory.getGPApi().onResume(mActivity);
            }

            @Override
            public void onNewIntent(Intent intent) {
                super.onNewIntent(intent);
                GPApiFactory.getGPApi().onNewIntent(mActivity);
            }

            @Override
            public void onPause() {
                super.onPause();
                GPApiFactory.getGPApi().onPause(mActivity);
            }

            @Override
            public void onStop() {
                super.onStop();
                GPApiFactory.getGPApi().onStop(mActivity);
            }

            @Override
            public void onDestroy() {
                super.onDestroy();
                GPApiFactory.getGPApi().onDestroy(mActivity);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                GPApiFactory.getGPApi().onActivityResult(mActivity, requestCode, resultCode, data);
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
    
    private void channelInit(){
        mAppid = VasSDKConfig.getInstance().getConfig("APPID");
        mAppkey = VasSDKConfig.getInstance().getConfig("APPKEY"); 
        boolean isDebug = VasSDKConfig.VAS_DEBUG.equals("true") ? true : false;
        GPApiFactory.getGPApi().setLogOpen(isDebug);
        if(!mIsInitSuc){
            GPApiFactory.getGPApi().initSdk(mActivity, mAppid, mAppkey, mInitObsv);
        }
    }
    
    /**
     * 初始化回调接口
     */
    private IGPSDKInitObsv mInitObsv = new IGPSDKInitObsv() {
        @Override
        public void onInitFinish(GPSDKInitResult initResult) {
            VASLogUtil.d("GPSDKInitResult mInitErrCode: " + initResult.mInitErrCode);
            switch (initResult.mInitErrCode) {
                case GPSDKInitResult.GPInitErrorCodeConfig:
                    VasSDK.getInstance().getVasInitCallback().onFailed("初始化配置错误", "");
                    retryInit();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
                    VasSDK.getInstance().getVasInitCallback().onFailed("游戏需要更新", "");
                    break;
                case GPSDKInitResult.GPInitErrorCodeNet:
                    VasSDK.getInstance().getVasInitCallback().onFailed("初始化网络错误", "");
                    retryInit();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNone:
                    VasSDK.getInstance().getVasInitCallback().onSuccess();
                    mIsInitSuc = true;
                    break;
            }
            }
        };
    
    
    
    
    /**
     * 重试初始化3次
     */
    public void retryInit() {
        if (mInitCount >= 3) {
            VasSDK.getInstance().getVasInitCallback().onFailed("初始化失败，请检查网络", "");
            return;
        }
        mInitCount++;
        GPApiFactory.getGPApi().initSdk(mActivity, mAppid, mAppkey, mInitObsv);
    }
    
    public void login(){
        if(mIsInitSuc){
            GPApiFactory.getGPApi().login(mActivity.getApplication(), new IGPUserObsv()
            {
                
                @Override
                public void onFinish(GPUserResult gpUserResult)
                {
                    switch (gpUserResult.mErrCode) {
                        case GPUserResult.USER_RESULT_LOGIN_FAIL:
                            VasSDK.getInstance().getVasLoginCallback().onFailed("登录失败", "");
                            break;
                        case GPUserResult.USER_RESULT_LOGIN_SUCC:
                            mUid = GPApiFactory.getGPApi().getLoginUin();
                            mAccount = GPApiFactory.getGPApi().getAccountName();
                            mToken = GPApiFactory.getGPApi().getLoginToken();
                            if(TextUtils.isEmpty(mAccount)){
                                mAccount = mUid;
                            }
                            mUserInfo = new VasUserInfo();
                            mUserInfo.setUid(mUid);
                            mUserInfo.setToken(mToken);
                            mUserInfo.setUserName(mAccount);
                            VasSDK.getInstance().getVasLoginCallback().onSuccess(mUserInfo);
                            VASLogUtil.d("login suc : " + "channelName=" + GPApiFactory.getGPApi().getChannelName());
                            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
                            break;
                    }
                }
            });
        }else{
            retryInit();
        }
    }
    
    public void logout(){
        GPApiFactory.getGPApi().logout();
        VasSDK.getInstance().getVasLogoutCallback().onSuccess();
    }
    
    public void pay(final VasOrderInfo orderinfo,final VasRoleInfo roleInfo){
        if(!GPApiFactory.getGPApi().isLogin()){
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
    
    private void channelPay(final VasOrderInfo orderInfo,VasRoleInfo roleInfo){
        GPSDKGamePayment payParam = new GPSDKGamePayment();
        payParam.mItemName = orderInfo.getGoodsName();//商品名称
        payParam.mPaymentDes = orderInfo.getGoodsDesc();//描述
        payParam.mItemPrice = (float) orderInfo.getAmount();
        payParam.mItemOrigPrice = (float) orderInfo.getAmount();
        payParam.mItemId = orderInfo.getGoodsId();
        payParam.mSerialNumber = orderInfo.getOrderId();
        payParam.mCurrentActivity = mActivity;
        payParam.mReserved = orderInfo.getExtrasParams();
        payParam.mPlayerId = roleInfo.getRoleId();
        payParam.mPlayerNickName = roleInfo.getRoleName();
        payParam.mServerId = roleInfo.getServerId();
        payParam.mServerName = roleInfo.getServerName();
        String rate = VasSDKConfig.getInstance().getConfig("RATE");
        payParam.mRate = Integer.parseInt(rate);//兑换比例
        GPApiFactory.getGPApi().buy(payParam, new IGPPayObsv()
        {
            
            @Override
            public void onPayFinish(GPPayResult payResult)
            {
                if(payResult == null){
                    VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), "支付失败", "");
                    return;
                }
                disposePayResult(payResult,orderInfo);
            }
        });
    }
    
    private void disposePayResult(GPPayResult payResult,final VasOrderInfo orderInfo){
        String msg = "";
        switch (payResult.mErrCode) {
            case GPPayResult.GPSDKPayResultCodeSucceed:
                msg = "支付成功";
                VasSDK.getInstance().getVasPayCallback().onSuccess(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodePayBackground:
                msg = "后台正在轮循购买";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeBackgroundSucceed:
                msg = "后台购买成功";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeBackgroundTimeOut:
                msg = "后台购买超时";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeCancel:
                msg = "用户取消";
                VasSDK.getInstance().getVasPayCallback().onCancel(msg);
                break;
            case GPPayResult.GPSDKPayResultCodeNotEnough:
                msg = "余额不足";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeOtherError:
                msg = "其他错误";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodePayForbidden:
                msg = "用户被限制";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodePayHadFinished:
                msg = "该订单已经完成";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeServerError:
                msg = "服务器错误";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultNotLogined:
                msg = "未登录";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultParamWrong:
                msg = "参数错误";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            case GPPayResult.GPSDKPayResultCodeLoginOutofDate:
                msg = "登录态失效";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
            default:
                msg = "未知错误";
                VasSDK.getInstance().getVasPayCallback().onFailed(orderInfo.getCpOrderId(), msg, "");
                break;
        }
    }
    
    public void setGameRole(VasRoleInfo roleInfo, boolean isCreateRole){
        
        GPSDKPlayerInfo gpsdkPlayerInfo = new GPSDKPlayerInfo();
        gpsdkPlayerInfo.mGameLevel = roleInfo.getRoleLevel();
        gpsdkPlayerInfo.mPlayerId = roleInfo.getRoleId();
        gpsdkPlayerInfo.mPlayerNickName = roleInfo.getRoleName();
        gpsdkPlayerInfo.mServerId = roleInfo.getServerId();
        gpsdkPlayerInfo.mServerName = roleInfo.getServerName();
        gpsdkPlayerInfo.mBalance = 0;
        gpsdkPlayerInfo.mGameVipLevel = roleInfo.getVipLevel();
        gpsdkPlayerInfo.mPartyName = roleInfo.getPartyName();
        // 第一次创建角色调用createPlayerInfo，后续同一个角色调用uploadPlayerInfo
        if(isCreateRole){
            GPApiFactory.getGPApi().createPlayerInfo(gpsdkPlayerInfo, mGPUploadPlayerInfoObsv);
        }else{
            GPApiFactory.getGPApi().uploadPlayerInfo(gpsdkPlayerInfo, mGPUploadPlayerInfoObsv);
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
    }
    
    /**
     * 上报用户信息回调接口
     */
    private IGPUploadPlayerInfoObsv mGPUploadPlayerInfoObsv = new IGPUploadPlayerInfoObsv() {
        @Override
        public void onUploadFinish(final GPUploadPlayerInfoResult gpUploadPlayerInfoResult) {
            VasSDK.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (gpUploadPlayerInfoResult.mResultCode == GPUploadPlayerInfoResult.GPSDKUploadSuccess)
                        VASLogUtil.d("上传成功");
                    else
                        VASLogUtil.d("上传失败");
                }
            });

        }
    };
    
    public VasUserInfo getUserInfo(){
        return mUserInfo;
    }
    
    public void exit(){
        GPApiFactory.getGPApi().exit(new IGPExitObsv()
        {
            
            @Override
            public void onExitFinish(GPExitResult exitResult)
            {
                switch (exitResult.mResultCode) {
                    case GPExitResult.GPSDKExitResultCodeError:
                        VasSDK.getInstance().getVasExitCallback().onFailed("调用退出弹框失败", "");
                        break;
                    case GPExitResult.GPSDKExitResultCodeExitGame:
                        VasSDK.getInstance().getVasExitCallback().onSuccess();
                        break;
                    case GPExitResult.GPSDKExitResultCodeCloseWindow:
                        VasSDK.getInstance().getVasExitCallback().onFailed("关闭退出弹框", "");
                        break;
                }
            }
        });
    }
    
}
