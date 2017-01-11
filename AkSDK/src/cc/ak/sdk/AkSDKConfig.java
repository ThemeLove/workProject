
package cc.ak.sdk;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import cc.ak.sdk.util.AkSDKUtil;
import cc.ak.sdk.util.AKLogUtil;
import cc.ak.sdk.util.PlatformUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkSDKConfig {

    public static final int PLUGIN_TYPE_USER = 1; //用户登录插件
    public static final int PLUGIN_TYPE_PAY = 2; //支付插件
    public static final int PLUGIN_TYPE_WXAPI = 3; //微信插件

    public static final String SDK_VER = "1.0"; //SDK版本
    public static String AK_GAMEID = null;
    public static String AK_CTYPE = null;
    public static String AK_KEY = null;
    public static String AK_PARTNERID = null;
    public static String AK_URL = null;
    public static String DEVICE_NO = null;
    public static String AK_STATISTIC_URL = null;//统计URL地址
    public static String MAIN_ACTIVITY_NAME = null;//闪屏图片需要跳转
    public static String AK_CHECKUPDATE_URL = null;//哆可梦官方包检测更新url
    public static String AK_CHANNEL_ID = null;//广告渠道
    public static String AK_DEBUG = null;//是否是debug模式，控制日志打印
    public static String AK_GAMENAME = null;//接入游戏名称
    public static String AK_SUB_CHANNELID = null;//广告子渠道

    //闪屏图片名称
    public static final String SPLASH_DRAWABLE_NAME = "ak_sdk_splash";

    /*-------------------------AES-------------------------------*/
    public static final String AES_KEY = "6XftfdB7K4EQ5G3b";
    public static final String AES_IV = "5efd3f6060e70330";

    /**-------------------版本检测-----------------------*/
    public static final String UPDATE_APK_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static final String UPDATE_APKFILE_NAME = "updata.apk";

    private Map<String, String> configMap;
    private Map<Integer, String> pluginMap;
    private List<String> applicationList;
    private static AkSDKConfig instance;

    public static String sUid = "";
    public static String sAccount = "";
    public static String sRoleName = "";
    public static int sServerId = -1;

    private AkSDKConfig() {
        configMap = new HashMap<String, String>();
        pluginMap = new HashMap<Integer, String>();
        applicationList = new ArrayList<String>();
    }

    public static AkSDKConfig getInstance() {
        if (instance == null) {
            instance = new AkSDKConfig();
        }
        return instance;
    }

    public void loadConfig(Context context) {
//        AK_CHANNEL_ID = PlatformUtil.getChannel(context);
        String xmlPlugins = AkSDKUtil.getAssetConfigs(context, "aksdk_config.xml");
        if (xmlPlugins == null) {
            AKLogUtil.e("fail to load aksdk_config.xml");
            return;
        }

        configMap.clear();
        pluginMap.clear();
        applicationList.clear();
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(xmlPlugins));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        //加载基础配置
                        if ("param".equals(tag)) {
                            String key = parser.getAttributeValue(null, "name");
                            String value = parser.getAttributeValue(null, "value");
                            if ("AK_GAMEID".equalsIgnoreCase(key)) {
                                AK_GAMEID = value;
                            }
                            else if ("AK_PARTNERID".equalsIgnoreCase(key)) {
                                AK_PARTNERID = value;
                            } else if ("AK_CTYPE".equalsIgnoreCase(key)) {
                                AK_CTYPE = value;
                            } else if ("AK_KEY".equalsIgnoreCase(key)) {
                                AK_KEY = value;
                            } else if ("AK_URL".equalsIgnoreCase(key)) {
                                AK_URL = value;
                            } else if ("AK_STATISTIC_URL".equalsIgnoreCase(key)) {
                                AK_STATISTIC_URL = value;
                            }else if ("MAIN_ACTIVITY_NAME".equalsIgnoreCase(key)) {
                                MAIN_ACTIVITY_NAME = value;
                            } else if ("AK_CHECKUPDATE_URL".equalsIgnoreCase(key)) {
                                AK_CHECKUPDATE_URL = value;
                            } else if ("AK_DEBUG".equalsIgnoreCase(key)) {
                                AK_DEBUG = value;
                            } else if ("AK_GAMENAME".equalsIgnoreCase(key)) {
                                AK_GAMENAME = value;
                            }else if ("AK_CHANNEL_ID".equalsIgnoreCase(key)) {
                                AK_CHANNEL_ID = value;
                            }else if ("AK_SUB_CHANNELID".equalsIgnoreCase(key)) {
                                AK_SUB_CHANNELID = value;
                            }else {
                                configMap.put(key, value);
                            }
                            AKLogUtil.d("Load sdk_config: key:" + key + ", value:" + value);
                        }

                        //加载Application
                        if ("application".equals(tag)) {
                            String application = parser.getAttributeValue(null, "name");
                            applicationList.add(application);
                            AKLogUtil.d("Load Application: name:" + application);
                        }

                        //加载插件
                        if ("plugin".equals(tag)) {
                            String plugin = parser.getAttributeValue(null, "name");
                            int pluginType = Integer.parseInt(parser
                                    .getAttributeValue(null, "type"));
                            pluginMap.put(pluginType, plugin);
                            AKLogUtil.d("Load Plugin: " + pluginType + "; name:" + plugin);
                        }
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getApplicationList() {
        return applicationList;
    }

    public String getConfig(String key) {
        if (configMap != null) {
            return configMap.get(key);
        } else {
            return null;
        }
    }

    public void setConfig(String key, String value) {
        if (configMap != null) {
            configMap.put(key, value);
        }
    }

    public String getPlugin(Integer pluginType) {
        if (pluginMap != null) {
            return pluginMap.get(pluginType);
        } else {
            return null;
        }
    }

    public String getAesKey() {
        return AES_KEY;
    }

    public String getAesIv() {
        return AES_IV;
    }

}
