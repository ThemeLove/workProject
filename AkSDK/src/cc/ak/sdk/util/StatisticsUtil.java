
package cc.ak.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit.URLUtil;

import cc.ak.sdk.AkSDK;
import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.http.AsyncHttpClient;
import cc.ak.sdk.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计相关
 * @author yychen
 * @2015-12-29下午1:51:40
 */
public class StatisticsUtil {

    public static final String TAG = StatisticsUtil.class.getName();

    private Context mContext;

    private final static int VAILD_NUMBER = -1;
    private final static int RETYR_COUNT = 1; // 失败重试次数

    public StatisticsUtil(Context context) {
        this.mContext = context;
        List<String> urlList = getAdress();
        if (urlList != null && urlList.size() > 0) {
            for (String string : urlList) {
                connection(string, 1);
            }
            urlList.clear();
            saveAdress(urlList);
        }
    }

    /**
     * 启动游戏投递
     */
    @SuppressWarnings("deprecation")
    public void active() {
        StringBuilder params = new StringBuilder();
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("channel_id", AkSDKConfig.AK_CHANNEL_ID);
            paramObj.put("sub_channel_id", AkSDKConfig.AK_SUB_CHANNELID);
            paramObj.put("uuid", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity()).toString());
            paramObj.put("idfa", DeviceUtil.getAndroidId(AkSDK.getInstance().getActivity()));
            paramObj.put("brand", android.os.Build.MANUFACTURER);//手机厂商
            paramObj.put("mode", android.os.Build.MODEL);//手机型号
            paramObj.put("net_type", AKHttpUtil.getNetType(AkSDK.getInstance().getActivity()));
            paramObj.put("os_ver", "android" + android.os.Build.VERSION.RELEASE);
            paramObj.put("sdk_ver", AkSDKConfig.SDK_VER);
            paramObj.put("game_ver", DeviceUtil.getVersionCode(AkSDK.getInstance().getActivity()));
            params.append("?m=" + "API_Activate").append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
            AKLogUtil.d(TAG,paramObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        connection(
                AkSDKConfig.AK_URL + params,
                RETYR_COUNT);
        AKLogUtil.d(TAG, "active");
    }
    

    /**
     * 登陆成功投递
     * @param uid
     * @param account
     */
    public void loginSuccess(String uid, String account) {
        loginOrRegister("login", uid, account);
        AKLogUtil.d(TAG, "login");
    }

    /**
     * 注册成功投递
     * @param uid
     * @param account
     */
    public void registerSuccess(String uid, String account) {
        loginOrRegister("register", uid, account);
        AKLogUtil.d(TAG, "register");
    }

    /**
     * 游客绑定成功投递
     * @param uid
     * @param account
     */
    public void bindGuest(String uid, String account) {
        loginOrRegister("bind", uid, account);
        AKLogUtil.d(TAG, "bind");
    }

    /**
     * 登录(注册，游客绑定)
     * @param uid
     * @param account
     */
    private void loginOrRegister(String action, String uid, String account) {
        
    }

    private void enterAndRole(String type, String uid, String roleName,
            int serverId, int level,String roleId) {
        
        StringBuilder params = new StringBuilder();
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("game_id", AkSDKConfig.AK_GAMEID);
            paramObj.put("partner_id", AkSDKConfig.AK_PARTNERID);
            paramObj.put("channel_id", AkSDKConfig.AK_CHANNEL_ID);
            paramObj.put("sub_channel_id", AkSDKConfig.AK_SUB_CHANNELID);
            paramObj.put("uuid", DeviceUtil.getDeviceUuid(AkSDK.getInstance().getActivity()).toString());
            paramObj.put("uid", uid);
            paramObj.put("server_id", serverId);
            paramObj.put("role_id", roleId);
            paramObj.put("role_level", level);
            paramObj.put("role_name", roleName);
            paramObj.put("enter_type", type);
            params.append("?m=" + "API_Enter").append(
                    "&args="
                            + AesEncryptionUtil.encrypt(paramObj.toString(), AkSDKConfig.AES_KEY,
                                    AkSDKConfig.AES_IV));
            AKLogUtil.d(TAG,paramObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        connection(
                AkSDKConfig.AK_URL + params,
                RETYR_COUNT);
    }

    /**
     * 创建角色投递
     * @param uid
     * @param account
     * @param roleName
     * @param serverId
     */
    public void createRole(String uid, String roleId, String roleName, int serverId) {
        enterAndRole("1", uid, roleName, serverId, 0, roleId);
        AKLogUtil.d(TAG, "createRole");
    }

    /**
     * 进入游戏投递
     * @param uid
     * @param account
     * @param roleName
     * @param serverId
     * @param level 玩家等级
     */
    public void enterGame(String uid, String roleId, String roleName, int serverId, int level) {
        enterAndRole("2", uid, roleName, serverId, level, roleId);
        AKLogUtil.d(TAG, "enterGame");
    }

    /**
     * 角色升级投递
     * @param uid
     * @param account
     * @param roleName
     * @param serverId
     * @param level 玩家等级
     */
    public void roleUpLevel(String uid, String roleId, String roleName, int serverId, int level) {
        enterAndRole("3", uid, roleName, serverId, level, roleId);
        AKLogUtil.d(TAG, "roleUpLevel");
    }

    /**
     * 下单成功投递
     * @param userId
     * @param userName
     * @param orderId
     * @param roleName(非必选字段)
     * @param serverId
     * @param money
     * 
     * @param goodsName 用户付费商品名称(非必选字段)
     */
    @SuppressWarnings("deprecation")
    public void getOrder(String userId, String userName, String orderId, String roleName,
            int serverId, int money, String goodsName) {
       
    }

    /**
     * 支付成功投递(服务端投递)
     * @param userId
     * @param userName
     * @param orderId
     * @param roleName
     * @param serverId
     * @param money
     * @param goodsName
     */
    @SuppressWarnings("deprecation")
    public void paySuccess(String userId, String userName, String orderId, String roleName,
            int serverId, int money, String goodsName) {
        
    }

    private void connection(final String url, final int count) {
        if (!URLUtil.isHttpUrl(url)) {
            return;
        }

        try {

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, String content) {
                    AKLogUtil.d(TAG, "statusCode : " + statusCode);
                    AKLogUtil.d(TAG, content);
                    if (statusCode == 200) {
                        try {
                            JSONObject obj = new JSONObject(content);
                            int code = obj.getInt("state");
                            if (code == 1) {
                                if (url.contains("Activate")) {
                                    StringUtil.setNewInstall(mContext);
                                }
                            } else {
                                int number = count + 1;
                                retryConnect(url, number);
                                AKLogUtil.d(TAG,"number : " + number);
                            }
                        } catch (JSONException e) {
                            int number = count + 1;
                            retryConnect(url, number);
                            AKLogUtil.d(TAG,"JSONException number : " + number);
                        }
                    } else {
                        int number = count + 1;
                        retryConnect(url, number);
                    }
                }

                @Override
                public void onFailure(Throwable error, String message) {
                    AKLogUtil.e(TAG, "onFailure :" + error);
                    AKLogUtil.e(TAG, "onFailure :" + message);
                    int number = count + 1;
                    retryConnect(url, number);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 重试链接
     * 
     * @param url
     * @param count
     */
    private void retryConnect(String url, int count) {
        AKLogUtil.d(TAG, url + "  SEND FAILED(" + count + ")");
        if (count <= RETYR_COUNT) {
            connection(url, count);
        } else {
            List<String> urlList = getAdress();
            if (urlList != null) {
                urlList.add(url);
                saveAdress(urlList);
            }
        }
    }

    private void saveAdress(List<String> urlList) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < urlList.size(); i++) {
            result.append(urlList.get(i));
            if (i != urlList.size() - 1) {
                result.append("∑");
            }
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                "cxsentdata", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("sentdata", result.toString());
        editor.commit();
        if (urlList != null) {
            AKLogUtil.d(TAG, "saveAdress ：" + urlList.size());
        }
    }

    private List<String> getAdress() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                "cxsentdata", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("sentdata", "");
        List<String> adressList = new ArrayList<String>();

        if (result.length() > 0) {
            String[] adress = result.split("∑");
            if (adress.length > 0) {
                for (String string : adress) {
                    adressList.add(string);
                }
            }
        }
        if (adressList != null) {
            AKLogUtil.d(TAG, "saveAdress ：" + adressList.size());
        }
        return adressList;
    }

}
