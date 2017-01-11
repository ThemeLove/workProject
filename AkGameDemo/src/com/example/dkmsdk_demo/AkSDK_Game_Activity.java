
package com.example.dkmsdk_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import cc.ak.sdk.AkSDK;
import cc.ak.sdk.bean.AkPayParam;
import cc.ak.sdk.bean.AkRoleParam;
import cc.ak.sdk.callback.AkResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.cxsdk_demo.R;

public class AkSDK_Game_Activity extends Activity implements OnClickListener {

    private Button mLogin;
    private Button mLogout;
    private Button mPay;
    private Button createRole;
    private Button enterGame;
    private Context mContext;
    private Button levelUpdate;
    private boolean isInitSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = getApplicationContext();
        mLogin = (Button) findViewById(R.id.login);
        mLogout = (Button) findViewById(R.id.logout);
        mPay = (Button) findViewById(R.id.pay);
        createRole = (Button) findViewById(R.id.createRole);
        enterGame = (Button) findViewById(R.id.enterGame);
        levelUpdate = (Button) findViewById(R.id.levelUpdate);
        mLogin.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mPay.setOnClickListener(this);
        createRole.setOnClickListener(this);
        enterGame.setOnClickListener(this);
        levelUpdate.setOnClickListener(this);

        // 配置回调
        AkSDK.getInstance().setResultCallback(new AkResultCallback() {
            @Override
            public void onResult(int code, JSONObject data) {
                switch (code) {
                    case AkResultCallback.CODE_INIT_SUCCESS:
                        showToast("初始化成功");
                        try {
                            String gameId = data.getString("game_id");
                            // partner_id 对应映射平台关系请参考对接文档中平台号对应表格
                            String partnerId = data.getString("partner_id");
                            isInitSuccess = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case AkResultCallback.CODE_INIT_FAILURE:
                        showToast("初始化失败");
                        // 需再次调用初始化方法
                        AkSDK.getInstance().init(AkSDK_Game_Activity.this);
                        break;
                    case AkResultCallback.CODE_LOGIN_SUCCESS:
                        showToast("登录成功");
                        try {
                            String uid = data.getString("uid");
                            String account = data.getString("account");
                            String token = data.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case AkResultCallback.CODE_LOGIN_FAILURE:
                        showToast("登录失败");
                        break;
                    case AkResultCallback.CODE_SWITCH_ACCOUNT_SUCCESS:
                        showToast("切换帐号成功");
                        try {
                            String uid = data.getString("uid");
                            String account = data.getString("account");
                            String token = data.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case AkResultCallback.CODE_SWITCH_ACCOUNT_FAILURE:
                        showToast("切换帐号失败");
                        break;
                    case AkResultCallback.CODE_LOGOUT_SUCCESS:
                        try {
                            String uid = data.getString("uid");
                            String account = data.getString("account");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showToast("注销成功");
                        break;
                    case AkResultCallback.CODE_LOGOUT_FAILURE:
                        showToast("注销失败");
                        break;
                    case AkResultCallback.CODE_PAY_SUCCESS://这个成功并不一定是成功，有的平台是异步的，只是订单流程走通
                        try {
                            String uid = data.optString("uid");
                            String account = data.optString("account");
                            String cpOrderNo = data.optString("cpOrderNo");
                            String orderNo = data.optString("orderNo");
                            String amount = data.optString("amount");
                            String extension = data.optString("extension");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showToast("支付成功");
                        break;
                    case AkResultCallback.CODE_PAY_WAIT:
                        showToast("已支付，等待确认");
                        break;
                    case AkResultCallback.CODE_PAY_FAILURE:
                        showToast("支付失败");
                        break;
                }
            }
        });

        // 初始化，必须调用
        AkSDK.getInstance().init(AkSDK_Game_Activity.this);
        AkSDK.getInstance().onCreate(savedInstanceState);
    }

    /**
     * 下面的功能在游戏中间必须调用
     */
    @Override
    public void onClick(View v) {
        if (v == mLogin) {
            if (isInitSuccess) {
                // 注意：必须是初始化成功后才能调用登录函数，否则可能会引起程序异常
                AkSDK.getInstance().login();
            } else {
                // TODO 游戏可以根据需要做相关保护，提升体验
                System.out.println("初始化未完成，请稍后再次尝试登陆");
            }
        } else if (v == mPay) {
            AkPayParam payParam = new AkPayParam();
            payParam.setCpBill(System.currentTimeMillis() + "");
            payParam.setProductId("1"); // 商品标识
            payParam.setProductName("钻石100"); // 商品名
            payParam.setProductDesc("首充100送100"); // 商品说明
            payParam.setServerId("1"); // 服务器编号，需要为数字，1服标识为1,2服标识为2
            payParam.setServerName("风起云涌");
            payParam.setRoleId("1"); // 角色id
            payParam.setRoleName("风雨逍遥"); // 角色名
            payParam.setRoleLevel(1); // 角色等级
            payParam.setPrice(1); // 价格
            payParam.setExtension("扩展数据");// 会原样返回给游戏
            AkSDK.getInstance().pay(payParam);
        } else if (v == mLogout) {
            /**
             * 小米，华为，oppp,金立不支持logout的调用。在这些平台的调用将无效,同时收不到回调
             * 目前支持平台有：4399，百度，酷派，畅想游戏，联想，偶玩，PPS,360,搜狗，uc，vivo,豌豆荚 其他超出范围平台可询问技术对接人员
             */
            AkSDK.getInstance().logout();
        } else if (v == createRole) {
            AkRoleParam roleParam = new AkRoleParam();
            roleParam.setRoleId("1");
            roleParam.setRoleName("风雨逍遥");
            roleParam.setRoleLevel(1);
            roleParam.setServerId("1");
            roleParam.setServerName("服务器1");
            AkSDK.getInstance().createRole(roleParam);
        } else if (v == enterGame) {
            AkRoleParam roleParam = new AkRoleParam();
            roleParam.setRoleId("1");
            roleParam.setRoleName("风雨逍遥");
            roleParam.setRoleLevel(1);
            roleParam.setServerId("1");
            roleParam.setServerName("服务器1");
            AkSDK.getInstance().enterGame(roleParam);
        } else if (v == levelUpdate) {
            AkRoleParam roleParam = new AkRoleParam();
            roleParam.setRoleId("1");
            roleParam.setRoleName("风雨逍遥");
            roleParam.setRoleLevel(2);
            roleParam.setServerId("1");
            roleParam.setServerName("服务器1");
            AkSDK.getInstance().roleUpLevel(roleParam);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AkSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AkSDK.getInstance().onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AkSDK.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AkSDK.getInstance().onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AkSDK.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AkSDK.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AkSDK.getInstance().onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AkSDK.getInstance().onRestart();
    }

    @Override
    public void onBackPressed() {
        AkSDK.getInstance().onBackPressed();
    }

    private Toast toast;

    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(msg);
        }
        toast.show();
    }
}
