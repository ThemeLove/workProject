
package cc.ak.sdk.plugin;

import cc.ak.sdk.AkSDK;
import cc.ak.sdk.bean.AkRoleParam;
import cc.ak.sdk.factory.AkFactory;
import cc.ak.sdk.util.AKLogUtil;

public class AkUser {

    private static AkUser instance;
    private IUserPlugin userPlugin;

    private AkUser() {
    }

    public static AkUser getInstance() {
        if (instance == null) {
            instance = new AkUser();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        userPlugin = (IUserPlugin) AkFactory.newPlugin(IUserPlugin.PLUGIN_TYPE);
        AKLogUtil.d("AKUser init");
    }

    /**
     * 登录
     */
    public void login() {
        if (userPlugin != null) {
            AkSDK.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    userPlugin.login();
                }
            });
        } else {
            AKLogUtil.e("no instance for userPlugin");
        }
    }

    /**
     * 注销
     */
    public void logout() {
        if (userPlugin != null) {
            AkSDK.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    userPlugin.logout();
                }
            });
        } else {
            AKLogUtil.e("no instance for userPlugin");
        }
    }

    /**
     * 创建角色
     * @param param
     */
    public void createRole(AkRoleParam param) {
        if (userPlugin != null) {
            userPlugin.createRole(param);
        } else {
            AKLogUtil.e("no instance for userPlugin");
        }
    }

    /**
     * 进入游戏
     * @param param
     */
    public void enterGame(AkRoleParam param) {
        if (userPlugin != null) {
            userPlugin.enterGame(param);
        } else {
            AKLogUtil.e("no instance for userPlugin");
        }
    }

    /**
     * 角色升级
     * @param param
     */
    public void roleUpLevel(AkRoleParam param) {
        if (userPlugin != null) {
            userPlugin.roleUpLevel(param);
        } else {
            AKLogUtil.e("no instance for userPlugin");
        }
    }

}
