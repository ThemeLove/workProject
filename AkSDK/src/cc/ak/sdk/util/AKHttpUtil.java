package cc.ak.sdk.util;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cc.ak.sdk.AkSDK;
import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.bean.AkPayParam;
import cc.ak.sdk.callback.AkResultCallback;
import cc.ak.sdk.http.AsyncHttpClient;
import cc.ak.sdk.http.AsyncHttpResponseHandler;
import cc.ak.sdk.http.RequestParams;

public class AKHttpUtil {

    private static final int TIME_OUT = 30000;

    private static AsyncHttpClient createHttpClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIME_OUT);
        return client;
    }

    public static String createRequestParams(String act) {
        StringBuilder params = new StringBuilder();
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("uuid", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity())
                    .toString());
            paramObj.put("mac", DeviceUtil.getMac(AkSDK.getInstance().getActivity()));
            paramObj.put("sdk_ver", AkSDKConfig.SDK_VER);
            paramObj.put("ip", DeviceUtil.getLocalIp());
            paramObj.put("os_type", "1");
            params.append("?m=" + act).append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params.toString();
    }

    // 3.0版本后开始使用
    public static RequestParams creatCheckUpdateParams(String act) {
        RequestParams params = new RequestParams();
//        params.put("act", act);
//        params.put("time", System.currentTimeMillis() + "");
//        params.put("game_id", AkSDKConfig.AK_GID);
//        try {
//            params.put("channel_id", AkSDKConfig.AK_CID);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        params.put("clientip", DeviceUtil.getIp());
//        params.put("osver", "android" + android.os.Build.VERSION.RELEASE);
//        params.put("sdkver", AkSDKConfig.SDK_VER);
//        params.put("nettype", getNetType(AkSDK.getInstance().getActivity()) + "");
//        params.put("mac", DeviceUtil.getMac(AkSDK.getInstance().getActivity()));
//        params.put("device_id", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity())
//                .toString());
//        params.put("platform_id", AkSDKConfig.AK_PLATFORMID); // 哆可梦平台平台号固定为200001
//        params.put("platform", "1"); // 1:android; 2:ios
        return params;
    }

    public static void post(String url, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = createHttpClient();
        client.post(url, params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = createHttpClient();
        client.get(url, responseHandler);
    }

    public static void init(final Context context, final SuccessCallback callback) {
        String params = AKHttpUtil.createRequestParams("Auth_SDKInit");

        AKHttpUtil.get(AkSDKConfig.AK_URL + params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                AKLogUtil.d("init onSuccess : " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("state") == 1) {
                        JSONObject dataObj = json.optJSONObject("data");
                        if (callback != null) {
                            callback.onSuccess(dataObj);
                            AkSDK.getInstance()
                            .getResultCallback()
                            .onResult(
                                    AkResultCallback.CODE_INIT_SUCCESS,
                                    StringUtil.toJSON(
                                            String.format(
                                                    "{game_id:'%s', partner_id:'%s'}",
                                                    new String[] {
                                                            AkSDKConfig.AK_GAMEID,
                                                            AkSDKConfig.AK_PARTNERID,
                                                    }
                                                    )
                                            )
                            );
                        }
                        AKLogUtil.d("init Success : game_id = " + AkSDKConfig.AK_GAMEID + "\n" + "partner_id = " + AkSDKConfig.AK_PARTNERID);
                    } else {
                        onFailure(new Exception(), json.getString("error_msg"));
                    }
                } catch (Exception ex) {
                    onFailure(ex, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable ex, String errorResponse) {
                AKLogUtil.e("初始化失败", ex);
                AkSDK.getInstance()
                        .getResultCallback()
                        .onResult(AkResultCallback.CODE_INIT_FAILURE,
                                StringUtil.toJSON("{msg:'初始化失败'}"));
            }
        });
    }

    /**
     * 登录验证
     * 
     * @param mActivity
     * @param type
     * @param param
     * @param callback
     */
    public static void login(final Context context, String token, final SuccessCallback callback) {

        String partnerId = AkSDKConfig.AK_PARTNERID;
        
        StringBuilder params = new StringBuilder();
        try {
            String[] split = token.split("\\|");//需要转义
            String account = split[0];
            String splitToken = split[1];
            AKLogUtil.d("token = " + token);
            AKLogUtil.d("account = " + account);
            AKLogUtil.d("splitToken = " + splitToken);
            
            JSONObject paramObj = new JSONObject();
        if(partnerId.equalsIgnoreCase("250250")){//为官方哆可梦平台
            
            boolean isPhone = VerifyUtil.isPhone(account);
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("device", 1);
            paramObj.put("server_id", "102");//默认传0
            JSONObject sdkJson = new JSONObject();
            if(isPhone){
                sdkJson.put("mobile", account);
            }else{
                sdkJson.put("username", account);
            }    
            sdkJson.put("token", splitToken);
            paramObj.put("sdkJson", sdkJson);
            
        }else{//其它三方平台
            
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("device", 1);
            paramObj.put("token", token);
            
        }
            paramObj.put("uuid", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity()));
            paramObj.put("mac", DeviceUtil.getMac(AkSDK.getInstance().getActivity()));
           
            params.append("?m=" + "Auth_Login").append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AKHttpUtil.get(AkSDKConfig.AK_URL + params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                AKLogUtil.d("onSuccess : " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("state") == 1) {
                        JSONObject dataObj = json.optJSONObject("data");
                        if (callback != null) {
                            callback.onSuccess(dataObj);
                            
                            AkSDK.getInstance()
                            .getResultCallback()
                            .onResult(
                                    AkResultCallback.CODE_LOGIN_SUCCESS,
                                    StringUtil.toJSON(
                                            String.format(
                                                    "{uid:'%s', account:'%s',token:'%s'}",
                                                    new String[] {
                                                            dataObj.getString("uid"),
                                                            dataObj.getString("uname"),
                                                            dataObj.getString("token")
                                                    }
                                                    )
                                            )
                            );
                            
                            
                        }
                    } else {
                        onFailure(new Exception(), json.getString("error_msg"));
                    }
                } catch (Exception ex) {
                    onFailure(ex, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable ex, String errorResponse) {
                AKLogUtil.e("登录失败", ex);
                AkSDK.getInstance()
                        .getResultCallback()
                        .onResult(AkResultCallback.CODE_LOGIN_FAILURE,
                                StringUtil.toJSON("{msg:'登录失败'}"));
            }
        });
        
        
        
    }

    /**
     * 切换帐号验证
     * 
     * @param mActivity
     * @param type
     * @param param
     * @param callback
     */
    public static void reLogin(final Context context, String token, final SuccessCallback callback) {
        
        
        
        
        
        String partnerId = AkSDKConfig.AK_PARTNERID;
        
        StringBuilder params = new StringBuilder();
        try {
            String[] split = token.split("\\|");//需要转义
            String account = split[0];
            String splitToken = split[1];
            AKLogUtil.d("token = " + token);
            AKLogUtil.d("account = " + account);
            AKLogUtil.d("splitToken = " + splitToken);
            
            JSONObject paramObj = new JSONObject();
        if(partnerId.equalsIgnoreCase("250250")){//为官方哆可梦平台
            
            boolean isPhone = VerifyUtil.isPhone(account);
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("device", 1);
            paramObj.put("server_id", "102");//默认传0
            JSONObject sdkJson = new JSONObject();
            if(isPhone){
                sdkJson.put("mobile", account);
            }else{
                sdkJson.put("username", account);
            }    
            sdkJson.put("token", splitToken);
            paramObj.put("sdkJson", sdkJson);
            
        }else{//其它三方平台
            
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("device", 1);
            paramObj.put("token", token);
            
        }
            paramObj.put("uuid", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity()));
            paramObj.put("mac", DeviceUtil.getMac(AkSDK.getInstance().getActivity()));
           
            params.append("?m=" + "Auth_Login").append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AKHttpUtil.get(AkSDKConfig.AK_URL + params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                AKLogUtil.d("onSuccess : " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("state") == 1) {
                        JSONObject dataObj = json.optJSONObject("data");
                        if (callback != null) {
                            callback.onSuccess(dataObj);
                            
                            AkSDK.getInstance()
                            .getResultCallback()
                            .onResult(
                                    AkResultCallback.CODE_SWITCH_ACCOUNT_SUCCESS,
                                    StringUtil.toJSON(
                                            String.format(
                                                    "{uid:'%s', account:'%s',token:'%s'}",
                                                    new String[] {
                                                            dataObj.getString("uid"),
                                                            dataObj.getString("uname"),
                                                            dataObj.getString("token")
                                                    }
                                                    )
                                            )
                            );
                            
                            
                        }
                    } else {
                        onFailure(new Exception(), json.getString("error_msg"));
                    }
                } catch (Exception ex) {
                    onFailure(ex, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable ex, String errorResponse) {
                AKLogUtil.e("切换帐号失败", ex);
                AkSDK.getInstance()
                        .getResultCallback()
                        .onResult(AkResultCallback.CODE_SWITCH_ACCOUNT_FAILURE,
                                StringUtil.toJSON("{msg:'切换失败'}"));
            }
        });
        
        
    }

    public static void pay(final Context context, String uid, final AkPayParam payParam,
            final SuccessCallback callback) {

        StringBuilder params = new StringBuilder();
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("userID", uid);
            paramObj.put("productId", payParam.getProductId());
            paramObj.put("productName", payParam.getProductName());
            paramObj.put("productDesc", payParam.getProductDesc());
            paramObj.put("money", payParam.getPrice());
            paramObj.put("roleID", payParam.getRoleId());
            paramObj.put("roleName", payParam.getRoleName());
            paramObj.put("serverID", payParam.getServerId());
            paramObj.put("serverNames", payParam.getServerName());
            paramObj.put("extension", payParam.getExtension());
            paramObj.put("channel_id", AkSDKConfig.AK_CHANNEL_ID);
            paramObj.put("sub_channel_id", AkSDKConfig.AK_SUB_CHANNELID);
            params.append("?m=" + "Pay_DealOrder").append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AKHttpUtil.get(AkSDKConfig.AK_URL + params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                AKLogUtil.d("onSuccess : " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("state") == 1) {
                        String data = json.optString("data");
                        if (callback != null) {
                            JSONObject decodeData = new JSONObject(data);
                            callback.onSuccess(decodeData);
                        }
                    } else {
                        onFailure(new Exception(), json.getString("error_msg"));
                    }
                } catch (Exception ex) {
                    onFailure(ex, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable ex, String errorResponse) {
                AKLogUtil.e("获取订单号失败", ex);
            }
        });


    }

    public interface SuccessCallback {
        public void onSuccess(JSONObject data) throws Exception;
    }

    private static final int NET_TYPE_UNKNOW = 0;
    private static final int NET_TYPE_WIFI = 1;
    private static final int NET_TYPE_2G = 2;
    private static final int NET_TYPE_3G = 3;
    private static final int NET_TYPE_4G = 4;
    private static final int NET_TYPE_OTHER = 5;

    @SuppressLint("ServiceCast")
    public static int getNetType(Context context) {
        ConnectivityManager connectionManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NET_TYPE_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return NET_TYPE_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return NET_TYPE_3G;
                    case 13:
                        return NET_TYPE_4G;
                    default:
                        String subTypeName = networkInfo.getSubtypeName();
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA")
                                || subTypeName.equalsIgnoreCase("WCDMA")
                                || subTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NET_TYPE_3G;
                        } else {
                            return NET_TYPE_OTHER;
                        }
                }
            } else {
                return NET_TYPE_OTHER;
            }
        } else {
            return NET_TYPE_UNKNOW;
        }
    }

}
