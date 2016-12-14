
package cc.ak.sdk.tool.bean;

public class Game {

    private String id; //id
    private String gameId; //游戏Id
    private String platpormId; //平台Id
    private String subPlatformId;//子平台id
    private String url; //服务器连接
    private String orientation; //游戏是横屏还是竖屏(landscape|portrait),不配置默认是横屏

    private String statisticUrl; //统计的url
    private String gameMainName;//游戏主页面的全称
    private String debug;//是否debug

    private String name; //游戏名称
    private String folder; //对应的游戏文件夹
    
    private String channelId;//渠道id
    private String subChannelId;//子渠道id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getStatisticUrl() {
        return statisticUrl;
    }

    public void setStatisticUrl(String statisticUrl) {
        this.statisticUrl = statisticUrl;
    }

    public String getGameMainName() {
        return gameMainName;
    }

    public void setGameMainName(String gameMainName) {
        this.gameMainName = gameMainName;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlatpormId()
    {
        return platpormId;
    }

    public void setPlatpormId(String platpormId)
    {
        this.platpormId = platpormId;
    }

    public String getSubPlatformId()
    {
        return subPlatformId;
    }

    public void setSubPlatformId(String subPlatformId)
    {
        this.subPlatformId = subPlatformId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSubChannelId() {
        return subChannelId;
    }

    public void setSubChannelId(String subChannelId) {
        this.subChannelId = subChannelId;
    }

    @Override
    public String toString() {
        return id + " | " + name;
    }

}
