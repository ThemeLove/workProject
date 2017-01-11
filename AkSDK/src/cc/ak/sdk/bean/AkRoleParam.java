
package cc.ak.sdk.bean;

public class AkRoleParam {

    private String serverId;
    private String serverName;
    private String roleId;
    private String roleName;
    private int roleLevel;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }

    @Override
    public String toString() {
        return "AkRoleParam [serverId=" + serverId + ", serverName=" + serverName + ", roleId=" + roleId + ", roleName=" + roleName + ", roleLevel=" + roleLevel + "]";
    }

}
