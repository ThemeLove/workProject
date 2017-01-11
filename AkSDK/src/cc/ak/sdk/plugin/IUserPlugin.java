
package cc.ak.sdk.plugin;

import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.bean.AkRoleParam;

public interface IUserPlugin {

    public static final int PLUGIN_TYPE = AkSDKConfig.PLUGIN_TYPE_USER;

    public void login();

    public void logout();

    public void createRole(AkRoleParam param);

    public void enterGame(AkRoleParam param);

    public void roleUpLevel(AkRoleParam param);

}
