package com.vas.yuedong;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
import com.zzydgame.supersdk.api.YDMergeSDK;
import com.zzydgame.supersdk.callback.YDAuthCallBack;
import com.zzydgame.supersdk.callback.YDExitCallBack;
import com.zzydgame.supersdk.callback.YDLoginCallBack;
import com.zzydgame.supersdk.callback.YDLogoutCallBack;
import com.zzydgame.supersdk.callback.YDPayCallBack;
import com.zzydgame.supersdk.model.params.PayParams;
import com.zzydgame.supersdk.model.params.UserInfo;

public class YueDongSDK
{
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private static YueDongSDK instance;
    
    private YueDongSDK(){
        
    }
    
    public static YueDongSDK getInstance(){
        if(instance == null){
            instance = new YueDongSDK();
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
                exit();
                return false;
            }
            
            @Override
            public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                YDMergeSDK.onCreate(mActivity);
            }
            
            @Override
            public void onResume()
            {
                super.onResume();
                YDMergeSDK.onResume(mActivity);
            }
            
            @Override
            public void onPause()
            {
                super.onPause();
                YDMergeSDK.onPause(mActivity);
            }
            
            @Override
            public void onStop()
            {
                super.onStop();
                YDMergeSDK.onStop(mActivity);
            }
            
            @Override
            public void onRestart()
            {
                super.onRestart();
                YDMergeSDK.onRestart(mActivity);
            }
            
            @Override
            public void onStart()
            {
                super.onStart();
                YDMergeSDK.onStart(mActivity);
            }
            
            @Override
            public void onNewIntent(Intent newIntent)
            {
                super.onNewIntent(newIntent);
                YDMergeSDK.onNewIntent(mActivity, newIntent);
            }
            
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                YDMergeSDK.onActivityResult(mActivity, requestCode, resultCode, data);
            }
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                YDMergeSDK.onDestroy(mActivity);
            }
            
        });
        channelInit();
    }
    
    private void channelInit(){
        YDMergeSDK.auth(mActivity, null, new YDAuthCallBack()
        {
            
            @Override
            public void onAuthSuccess()
            {
                VasSDK.getInstance().getVasInitCallback().onSuccess();
                registerCallBack();
            }
            
            @Override
            public void onAuthFailed()
            {
                VasSDK.getInstance().getVasInitCallback().onFailed("init fail!", "");
            }
        });
    }
    
    private void registerCallBack(){
        YDMergeSDK.registerLogoutCallBack(new YDLogoutCallBack()
        {
            
            @Override
            public void onSwitch()
            {
                //自动弹出登录页面
            }
            
            @Override
            public void onLogoutSuccess()
            {
                VasSDK.getInstance().getVasLogoutCallback().onSuccess();
            }
            
            @Override
            public void onLogoutFail()
            {
                VasSDK.getInstance().getVasLogoutCallback().onFailed("logout fail!", "");
            }
        });
    }
    
    public void login(){
        YDMergeSDK.login(mActivity, new YDLoginCallBack()
        {
            
            @Override
            public void onLoginSuccess(UserInfo info)
            {
                mToken = info.getToken();
                mUid = info.getUserName();//游龙username为唯一标识
                mAccount = info.getUserName();
                VasUserInfo userInfo = new VasUserInfo();
                userInfo.setUid(mUid);
                userInfo.setUserName(mAccount);
                userInfo.setToken(mToken);
                VasSDK.getInstance().getVasLoginCallback().onSuccess(userInfo);
                VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
            }
            
            @Override
            public void onLoginFailed()
            {
                VasSDK.getInstance().getVasLoginCallback().onFailed("login fail!", ""); 
            }
            
            @Override
            public void onLoginCanceled()
            {
                VasSDK.getInstance().getVasLoginCallback().onCancel();
            }
        });
    }
    
    public void logout(){
        YDMergeSDK.logoutAccount();
    }
    
    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {
        if(orderinfo == null || roleInfo == null){
            VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(), "请查看支付参数", "");
            return;
        }
        
        if (!YDMergeSDK.isLogin())
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
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/youlong/" + VasSDKConfig.VAS_GAMEID);
        PayParams mPayParams = new PayParams();
        mPayParams.setUsername("");
        mPayParams.setAmount(orderinfo.getAmount());
        mPayParams.setOrderid(orderinfo.getOrderId());
        mPayParams.setRolename(roleInfo.getRoleName());
        mPayParams.setRolenid(roleInfo.getRoleId());
        mPayParams.setProductname(orderinfo.getGoodsName());
        mPayParams.setProductDesc(orderinfo.getGoodsDesc());
        mPayParams.setGameServerId(roleInfo.getServerId());
        mPayParams.setGameServerName(roleInfo.getServerName());
        mPayParams.setExtra(orderinfo.getExtrasParams());
        YDMergeSDK.pay(mActivity, mPayParams, new YDPayCallBack()
        {
            
            @Override
            public void onPaySuccess()
            {
                VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                        orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
            }
            
            @Override
            public void onPayFailed()
            {
                VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getOrderId(), "pay fail!", "");
            }
            
            @Override
            public void onPayChecking()
            {
                
            }
            
            @Override
            public void onPayCancel()
            {
                VasSDK.getInstance().getVasPayCallback().onCancel("pay cancel!");
            }
        });
    }
    
    public void setGameRoleInfo(VasRoleInfo roleInfo, boolean isCreateRole){
        int roleLevel = 1;
        try
        {
            roleLevel = Integer.parseInt(roleInfo.getRoleLevel());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        YDMergeSDK.setUserGameRole(mActivity, YDMergeSDK.getUserName(), roleInfo.getRoleId(), roleInfo.getRoleName(), roleInfo.getServerName(), roleInfo.getServerId(), roleLevel);
        if(!isCreateRole){
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
    }
    
    public void exit(){
        YDMergeSDK.exit(mActivity, new YDExitCallBack()
        {
            
            @Override
            public void onExit()
            {
                VasSDK.getInstance().getVasExitCallback().onSuccess();
            }
        });
    }
    
}
