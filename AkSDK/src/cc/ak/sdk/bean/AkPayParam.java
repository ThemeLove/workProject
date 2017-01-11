
package cc.ak.sdk.bean;

public class AkPayParam {

    private String cpBill;
    private String productId;
    private String productName;
    private String productDesc;
    private int price;
    private String serverId;
    private String serverName;
    private String roleId;
    private String roleName;
    private int roleLevel;
    private String payNotifyUrl;
    private String vip;
    private String orderID;
    private String extension;

    public String getCpBill() {
        return cpBill;
    }

    public void setCpBill(String cpBill) {
        this.cpBill = cpBill;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

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

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "AkPayParam [cpBill=" + cpBill + ", productId=" + productId + ", productName=" + productName + ", productDesc=" + productDesc + ", price=" + price + ", serverId=" + serverId + ", serverName=" + serverName + ", roleId=" + roleId + ", roleName=" + roleName + ", roleLevel=" + roleLevel + ", payNotifyUrl=" + payNotifyUrl + ", vip=" + vip + ", orderID=" + orderID + ", extension=" + extension + "]";
    }

}
