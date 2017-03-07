package com.vas.sy;

import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

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
import com.zqsdk.a07073sy.Zq07073syApi;
import com.zqsdk.a07073sy.callback.ExitCallBack;
import com.zqsdk.a07073sy.callback.InitCallBack;
import com.zqsdk.a07073sy.callback.LoginCallBack;
import com.zqsdk.a07073sy.callback.PayCallBack;
import com.zqsdk.a07073sy.view.FloatWindow;

public class SY07073SDK
{

    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;

    private String mToken;
    
    private String mPid;

    private static final int REQUEST_ORDER = 1;
    
    private static SY07073SDK instance;
    
    private SY07073SDK(){
        
    }
    
    public static SY07073SDK getInstance(){
        if(instance == null){
            instance = new SY07073SDK();
        }
        return instance;
    }
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter(){
            @Override
            public void onPause()
            {
                super.onPause();
                FloatWindow.getInstance(mActivity).hide();
            }
            
            @Override
            public void onResume()
            {
                super.onResume();
                FloatWindow.getInstance(mActivity).show();
            }
            
            @Override
            public boolean onBackPressed()
            {
                exit();
                return false;
            }
            
            @Override
            public void onDestroy()
            {
                super.onDestroy();
                FloatWindow.getInstance(mActivity).destroy();
            }
            
        });
        channelInit();
    }
    
    private void channelInit(){
        mPid = VasSDKConfig.getInstance().getConfig("PID");
        String gameId = VasSDKConfig.getInstance().getConfig("GAMEID");;
        Zq07073syApi.getInstance().init(mActivity, mPid, gameId, new InitCallBack()
        {
            
            @Override
            public void onInitSuccess()
            {
                VasSDK.getInstance().getVasInitCallback().onSuccess();
                FloatWindow.getInstance(mActivity).show();
            }
            
            @Override
            public void onInitFailure(String msg)
            {
                VasSDK.getInstance().getVasInitCallback().onFailed(msg, "");
            }
        });
        Zq07073syApi.getInstance().setLogger(false);
    }
    
    
    public void login(){
        Zq07073syApi.getInstance().login(mActivity, new LoginCallBack()
        {
            
            @Override
            public void onKeydownBack()
            {
                
            }
            
            @Override
            public void onCallBack(String msg)
            {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    int state = jsonObject.optInt("state");
                    String resMsg = jsonObject.optString("msg");
                    if (state == 1) {// 处理UserInfoBean
                        JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                        mAccount = dataJsonObject.optString("username");
                        mToken = dataJsonObject.optString("token");
                        mUid = dataJsonObject.optString("uid");
                        if(TextUtils.isEmpty(mAccount)){
                            mAccount = mUid;
                        }
                        VasUserInfo vasUserInfo = new VasUserInfo();
                        vasUserInfo.setUid(mUid);
                        vasUserInfo.setUserName(mAccount);
                        vasUserInfo.setToken(mToken);
                        VasSDK.getInstance().getVasLoginCallback().onSuccess(vasUserInfo);
                        VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.LOGIN);
                    }else if(state == 1099){
                        VasSDK.getInstance().getVasLoginCallback().onCancel();
                    }else{
                        VasSDK.getInstance().getVasLoginCallback().onFailed(resMsg, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    VasSDK.getInstance().getVasLoginCallback().onFailed("login fail!", "");
                }
            }
        });
    }
    
    
    public void logout(){
        VasSDK.getInstance().getVasLogoutCallback().onSuccess();
    }
    
    public void setGameRoleInfo(VasRoleInfo roleInfo, boolean isCreateRole){
        if(!isCreateRole){
            VasStatisticUtil.sendStatistic(mUid, VasStatisticUtil.ENTERGAME);
        }
    }
    
    public void pay(final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
    {
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

    private void parseOrderResult(String result, final VasOrderInfo orderinfo, final VasRoleInfo roleInfo)
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

    private void channelPay(final VasOrderInfo orderinfo, VasRoleInfo roleInfo){
        // 注意渠道SDK的参数orderid要传聚合SDK生成的oid,和callbackUrl
        orderinfo.setCallbackUrl("http://game.g.pptv.com/notify/lqlqs/" + VasSDKConfig.VAS_GAMEID);
        String amount = String.valueOf(orderinfo.getAmount());
        Zq07073syApi.getInstance().pay(mActivity, mAccount, mToken, amount, mPid, roleInfo.getServerId(), orderinfo.getExtrasParams(), new PayCallBack()
        {
            
            @Override
            public void onKeydownBack()
            {
                
            }
            
            @Override
            public void onPayCallBack(String msg)
            {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    int state = jsonObject.optInt("state");
                    String resMsg = jsonObject.optString("msg");
                    if (state == 1) {
                        
                        VasSDK.getInstance().getVasPayCallback().onSuccess(orderinfo.getOrderId(),
                                orderinfo.getCpOrderId(), orderinfo.getExtrasParams());
                    }else if(state == 1099){
                        VasSDK.getInstance().getVasPayCallback().onCancel(resMsg);
                    }else{
                        VasSDK.getInstance().getVasPayCallback().onFailed(orderinfo.getCpOrderId(),
                                resMsg, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void exit(){
        Zq07073syApi.getInstance().exit(mActivity, new ExitCallBack()
        {
            
            @Override
            public void onKeydownBack()
            {
                
            }
            
            @Override
            public void onExit(String msg)
            {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    int state = jsonObject.optInt("state");
                    if(state == 1){
                        VasSDK.getInstance().getVasExitCallback().onSuccess();
                    }else{
                        VasSDK.getInstance().getVasExitCallback().onFailed("continue game", "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}
