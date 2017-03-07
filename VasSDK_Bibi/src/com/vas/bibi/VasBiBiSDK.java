package com.vas.bibi;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.game.sdk.YTSDKManager;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.vas.vassdk.VasLoadingDialog;
import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.apiadapter.ActivityAdapter;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.http.VasHttpUtil;
import com.vas.vassdk.util.VasMD5Util;
import com.vas.vassdk.util.VasStatisticUtil;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class VasBiBiSDK
{

    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private static VasBiBiSDK instance;
    
    private YTSDKManager mSdkManager;
    
    private VasBiBiSDK(){
        
    }
    
    public static VasBiBiSDK getInstance(){
        if(instance == null){
            instance = new VasBiBiSDK();
        }
        return instance;
    }
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        mSdkManager = YTSDKManager.getInstance(mActivity);
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
                mSdkManager.showFloatView();
            }
            
            @Override
            public void onStop()
            {
                super.onStop();
                mSdkManager.removeFloatView();
            }
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                mSdkManager.recycle();
            }
            
        });
        channelInit();
    }
    
    private void channelInit(){
        VasSDK.getInstance().getVasInitCallback().onSuccess();
    }
    
    public void login(){
        mSdkManager.showLogin(mActivity, false, new OnLoginListener()
        {
            
            @Override
            public void loginSuccess(LogincallBack callBack)
            {
                mUid = callBack.mem_id;
                mAccount = mUid;
                mToken = callBack.user_token;
                VasUserInfo info = new VasUserInfo();
                info.setUid(mUid);
                info.setUserName(mAccount);
                info.setToken(mToken);
                VasSDK.getInstance().getVasLoginCallback().onSuccess(info);
                mSdkManager.showFloatView();
                VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
            }
            
            @Override
            public void loginError(LoginErrorMsg errorMsg)
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed(errorMsg.msg, "");
            }
        });
    }
    
    public void logout(){
        VasSDK.getInstance().getVasLogoutCallback().onSuccess();
        mSdkManager.removeFloatView();
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
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/bibi/" + VasSDKConfig.VAS_GAMEID);
        mSdkManager.showPay(mActivity, roleInfo.getRoleId(), String.valueOf(orderinfo.getAmount()), roleInfo.getServerId(), orderinfo.getGoodsName(), orderinfo.getGoodsDesc(), orderinfo.getCallbackUrl(),
                orderinfo.getExtrasParams(), new OnPaymentListener() {
                    @Override
                    public void paymentSuccess(
                            PaymentCallbackInfo callbackInfo) {
                        VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                                orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
                         
                    }

                    @Override
                    public void paymentError(PaymentErrorMsg errorMsg) {

                        VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                                errorMsg.msg, "");
                    }
                });
    }
    
    
    public void exit(){
        
    }
    
    public void setGameRoleInfo(VasRoleInfo arg0, boolean arg1){
        if(!arg1){
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
    }
    
}
