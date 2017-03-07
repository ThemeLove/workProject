package com.vas.aile;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.game.sdk.HuosdkManager;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.domain.SubmitRoleInfoCallBack;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.listener.OnPaymentListener;
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

public class AiLeSDK
{

    private static AiLeSDK instance;
    
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private HuosdkManager mSdkManager = HuosdkManager.getInstance();
    
    
    private AiLeSDK(){
    }
    
    public static AiLeSDK getInstance(){
        if(instance == null){
            instance = new AiLeSDK();
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
                return true;//true表示渠道没有sdk退出框
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
                mSdkManager.recycle(new OnLogoutListener()
                {
                    
                    @Override
                    public void logoutSuccess(String arg0, String arg1)
                    {
                        
                    }
                    
                    @Override
                    public void logoutError(String arg0, String arg1)
                    {
                        
                    }
                });
            }
            
        });
        channelInit();
    }
    
    private void channelInit(){
        mSdkManager.initSdk(mActivity, new OnInitSdkListener()
        {
            
            @Override
            public void initSuccess(String code, String msg)
            {
                VasSDK.getInstance().getVasInitCallback().onSuccess();
            }
            
            @Override
            public void initError(String code, String msg)
            {
                VasSDK.getInstance().getVasInitCallback().onFailed(msg, "");
            }
        });
        mSdkManager.addLoginListener(new OnLoginListener()
        {
            
            @Override
            public void loginSuccess(int type, LogincallBack logincBack)
            {
                mUid = logincBack.mem_id;
                mToken = logincBack.user_token;
                mAccount = mUid;
                
                VasUserInfo info = new VasUserInfo();
                info.setUid(mUid);
                info.setToken(mToken);
                info.setUserName(mAccount);
                
                if (type == OnLoginListener.TYPE_NORMAL_LOGIN) {
                    //正常登陆
                    VasSDK.getInstance().getVasLoginCallback().onSuccess(info);
                }else if(type == OnLoginListener.TYPE_SWITCH_ACCOUNT){
                    //切换账号
                    VasSDK.getInstance().getVasSwitchAccountCallback().onSuccess(info);
                }else{
                    //token过期重新登陆
                    VasSDK.getInstance().getVasLoginCallback().onSuccess(info);
                }
                mSdkManager.showFloatView();
                VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
            }
            
            @Override
            public void loginError(int arg0, LoginErrorMsg arg1)
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed(arg1.msg, "");
            }
        });
    }
    
    public void login(){
        mSdkManager.showLogin(false);
    }
    
    public void logout(){
        mSdkManager.logout(new OnLogoutListener()
        {
            
            @Override
            public void logoutSuccess(String code, String msg)
            {
                VasSDK.getInstance().getVasLogoutCallback().onSuccess();
            }
            
            @Override
            public void logoutError(String code, String msg)
            {
                VasSDK.getInstance().getVasLogoutCallback().onFailed(msg, "");
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
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/aile/" + VasSDKConfig.VAS_GAMEID);
        int rate = 10;
        float balance = 0;
        try
        {
            rate = Integer.parseInt(VasSDKConfig.getInstance().getConfig("VAS_RATE"));
            balance = Float.parseFloat(roleInfo.getGameBalance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        CustomPayParam customPayParam = new CustomPayParam();
        customPayParam.setCp_order_id(orderinfo.getOrderId());
        customPayParam.setCurrency_name("");
        customPayParam.setExchange_rate(rate);
        customPayParam.setExt(orderinfo.getExtrasParams());
        customPayParam.setProduct_count(1);
        customPayParam.setProduct_desc(orderinfo.getGoodsDesc());
        customPayParam.setProduct_id(orderinfo.getGoodsId());
        customPayParam.setProduct_name(orderinfo.getGoodsName());
        customPayParam.setProduct_price((float) orderinfo.getAmount());
        customPayParam.setRoleinfo(initSdkRoleInfo(roleInfo,balance));
        customPayParam.setServer_id(roleInfo.getServerId());
        mSdkManager.showPay(customPayParam, new OnPaymentListener()
        {
            
            @Override
            public void paymentSuccess(PaymentCallbackInfo callbackInfo)
            {
                VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                        orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
            }
            
            @Override
            public void paymentError(PaymentErrorMsg errorMsg)
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                        errorMsg.msg, "");
            }
        });
    }
    
    private RoleInfo initSdkRoleInfo(VasRoleInfo roleInfo,float balance){
        
        String creatTime = roleInfo.getRoleCreateTime();
        if(TextUtils.isEmpty(creatTime)){
            creatTime = System.currentTimeMillis() + "";
        }
        RoleInfo sdkRoleInfo = new RoleInfo();
        sdkRoleInfo.setParty_name(roleInfo.getPartyName());
        sdkRoleInfo.setRole_balence(balance);
        sdkRoleInfo.setRole_id(roleInfo.getRoleId());
        sdkRoleInfo.setRole_level(roleInfo.getRoleLevel());
        sdkRoleInfo.setRole_name(roleInfo.getRoleName());
        sdkRoleInfo.setRole_type(1);
        sdkRoleInfo.setRole_vip(roleInfo.getVipLevel());
        sdkRoleInfo.setRolelevel_ctime(creatTime);
        sdkRoleInfo.setRolelevel_mtime("0");
        sdkRoleInfo.setServer_id(roleInfo.getServerId());
        sdkRoleInfo.setServer_name(roleInfo.getServerName());
        return sdkRoleInfo;
    }
    
    public void exit(){
        
    }
    
    public void setGameRoleInfo(VasRoleInfo vasRoleInfo, boolean isCreateRole){
        
        if(!isCreateRole){
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
        
        float balance = 0;
        try
        {
            balance = Float.parseFloat(vasRoleInfo.getGameBalance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mSdkManager.setRoleInfo(initSdkRoleInfo(vasRoleInfo, balance), new SubmitRoleInfoCallBack()
        {
            
            @Override
            public void submitSuccess()
            {
                VASLogUtil.d("summit role success!");
            }
            
            @Override
            public void submitFail(String failMsg)
            {
                VASLogUtil.d(failMsg);
            }
        });
    }
    
}
