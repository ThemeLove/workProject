
package cc.ak.sdk.update;

public class CheckUpdateBean {

    private String downloadUrl;
    private String gameVersion;
    private String updateTip;
    private int updateType; // 1为强制更新，0为非强制更新

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getUpdateTip() {
        return updateTip;
    }

    public void setUpdateTip(String updateTip) {
        this.updateTip = updateTip;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

}
