package com.vas.qmyx;

import android.app.Activity;
import android.text.TextUtils;

import com.haobangnet.gamesdk.QMYXCallbackListener;
import com.haobangnet.gamesdk.QMYXCallbackListenerNullException;
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

public class VASQMYXSDK
{
    private Activity mActivity;

    private VasLoadingDialog mDialog;

    private String mUid;

    private String mAccount;
    
    private String mToken;

    private static final int REQUEST_ORDER = 1;
    
    private static VASQMYXSDK instance;
    
    private VASQMYXSDK(){
        
    }
    
    public static VASQMYXSDK getInstance(){
        if(instance == null){
            instance = new VASQMYXSDK();
        }
        return instance;
    }
    
    
    public void init(){
        mActivity = VasSDK.getInstance().getActivity();
        mDialog = new VasLoadingDialog(mActivity, "创建订单中...");
        VasSDK.getInstance().setActivityListener(new ActivityAdapter(){
            
        });
        channelInit();
    }
    
    private void channelInit(){
        boolean isDebug = VasSDKConfig.VAS_DEBUG.equalsIgnoreCase("true") ? true : false;
        try
        {
            QMYXSDK.getDefault().initSDK(mActivity, isDebug, QMYXLogLevel.DEBUG, new QMYXCallbackListener<String>()
            {
                
                @Override
                public void callback(int statuscode,String data)
                {
                    switch (statuscode) {
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
    
    public void login(){
        try
        {
            QMYXSDK.getDefault().login(mActivity, new QMYXCallbackListener<String>()
            {
                
                @Override
                public void callback(int code, String msg)
                {
                    switch (code) {
                        case QMYXGameSDKStatusCode.SUCCESS:
                            System.out.println("java login SUCCESS...");
                            // 登录成功，可以执行后续操作
                            mToken = QMYXSDK.getDefault().getSessionId();
                            mUid = "";
                            mAccount = "";
                            if (TextUtils.isEmpty(mAccount))
                            {
                                mAccount = mUid;
                            }
                            VasUserInfo paramUserInfo = new VasUserInfo();
                            paramUserInfo.setUid(mUid);
                            paramUserInfo.setUserName(mAccount);
                            paramUserInfo.setToken(mToken);
                            VasSDK.getInstance().getVasLoginCallback().onSuccess(paramUserInfo);
                            break;
                        case QMYXGameSDKStatusCode.LOGIN_EXIT:
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
    
    public void logout(){
        try {
            QMYXSDK.getDefault().logout(new QMYXCallbackListener<String>() {
                @Override
                public void callback(int statuscode, String data) {
                    switch (statuscode){
                        case QMYXGameSDKStatusCode.NO_INIT:
                            //QMYXSDK未初始化
                            break;
                        case QMYXGameSDKStatusCode.NO_LOGIN:
                            //未登录
                            break;
                        case QMYXGameSDKStatusCode.SUCCESS:
                            //退出账号成功
                            break;
                        case QMYXGameSDKStatusCode.FAIL:
                            //退出账号失败
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (QMYXCallbackListenerNullException e) {
            e.printStackTrace();
        }
    }
    
    
    private void channelpay(final VasOrderInfo orderInfo,final VasRoleInfo vasRoleInfo){
        try {
            int serverId = 0;
            float amount = (float) orderInfo.getAmount();
            serverId = Integer.parseInt(vasRoleInfo.getServerId());
            QMYXPaymentInfo paymentInfo = new QMYXPaymentInfo();// 创建Payment对象，用于传递充值信息
            paymentInfo.setAmount(amount);// 单位：元
            // 设置允许充值的金额，此为可选参数，默认为0；
            paymentInfo.setServerId(serverId);// 设置区服的ID
            paymentInfo.setPayDesc(orderInfo.getGoodsDesc());// 设置充值描述
            paymentInfo.setCustomInfo(orderInfo.getExtrasParams());
            //充值自定义参数，此参数不作任何处理，在充值完成后通知游戏服务器充值结果时原封不动传给游戏服务器。此参数为可选参数，默认为空。
            //对于需要此参数的游戏，充值前建议先判断下次参数传递的值是否正常不为空再调充值接口，注意长度不能超过120。
            QMYXSDK.getDefault().pay(mActivity, paymentInfo,
                    new QMYXCallbackListener<OrderInfo>() {
                        @Override
                        public void callback(int statuscode, OrderInfo data) {
                            switch (statuscode) {
                                case QMYXGameSDKStatusCode.NO_INIT:
//                                    Toast.makeText(mActivity, "没有初始化", Toast.LENGTH_SHORT).show();
//                                    sendCallback(CALLBACK_PAY, "statuscode=" + statuscode + "  data = " + data);
                                    break;
                                case QMYXGameSDKStatusCode.SUCCESS:
//                                    Log.d(TAG, data.getOrderId());//sdk服务器返回的订单号
//                                    Log.d(TAG, data.getOrderAmount()+"");//充值金额
                                    System.out.println("order = "+data.getOrderId()+" amount = "+data.getOrderAmount());
//                                    Toast.makeText(mActivity, "支付成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case QMYXGameSDKStatusCode.FAIL:
//                                    Toast.makeText(mActivity, "支付失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case QMYXGameSDKStatusCode.NO_LOGIN:
//                                    Toast.makeText(mActivity, "没有登录", Toast.LENGTH_SHORT).show();
                                    break;
                                case QMYXGameSDKStatusCode.PAY_USER_EXIT:
//                                    Toast.makeText(mActivity, "退出支付界面", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        } catch (QMYXCallbackListenerNullException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
