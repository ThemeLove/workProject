package com.vas.vassdk;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.vas.vassdk.util.VASLogUtil;
import com.vas.vassdk.util.VasSDKUtil;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class VasSDKConfig
{

    public static final int PLUGIN_TYPE_USER = 1; // 用户登录插件

    public static final int PLUGIN_TYPE_PAY = 2; // 支付插件

    public static final int PLUGIN_TYPE_SETTING = 3;// 其他公用的设置插件

    public static String MAIN_ACTIVITY_NAME = "";// 闪屏图片需要跳转
    
    public static String VAS_GAMEID = "";//游戏id
    
    public static String VAS_PLATFORMID = "";//平台id
    
    public static String VAS_SUBPLATFORMID = "";//子平台id
    
    public static String VAS_CHANNELID = "";//渠道id
    
    public static String VAS_SUBCHANNEL_ID ="";//子渠道id
    
    public static String VAS_DEBUG ="";
    
    //闪屏图片名称
    public static final String SPLASH_DRAWABLE_NAME = "vas_sdk_splash";

    private Map<String, String> configMap;

    private Map<Integer, String> pluginMap;

    private List<String> applicationList;

    private static VasSDKConfig instance;

    private boolean mDebug = false;

    private VasSDKConfig()
    {
        configMap = new HashMap<String, String>();
        pluginMap = new HashMap<Integer, String>();
        applicationList = new ArrayList<String>();
    }

    public static VasSDKConfig getInstance()
    {
        if (instance == null)
        {
            instance = new VasSDKConfig();
        }
        return instance;
    }

    public void loadConfig(Context context)
    {

//        String xmlPlugins = VasSDKUtil.getAssetConfigs(context, "vassdk_config.xml");//解析普通xml
        String xmlPlugins = VasSDKUtil.getAssetDesConfigs(context, "vassdk_config.xml");//解析加密过的xml
        if (xmlPlugins == null)
        {
            Log.e("VasSDK","fail to load vassdk_config.xml");
            return;
        }

        configMap.clear();
        pluginMap.clear();
        applicationList.clear();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setInput(new StringReader(xmlPlugins));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        // 加载基础配置
                        if ("param".equals(tag))
                        {
                            String key = parser.getAttributeValue(null, "name");
                            String value = parser.getAttributeValue(null, "value");
                            if ("MAIN_ACTIVITY_NAME".equalsIgnoreCase(key))
                            {
                                MAIN_ACTIVITY_NAME = value;
                            }else if("VAS_GAMEID".equalsIgnoreCase(key)){
                                VAS_GAMEID = value;
                            }else if("VAS_CHANNELID".equalsIgnoreCase(key)){
                                VAS_CHANNELID = value;
                            }else if("VAS_SUBCHANNEL_ID".equalsIgnoreCase(key)){
                                VAS_SUBCHANNEL_ID = value;
                            }else if("VAS_PLATFORMID".equalsIgnoreCase(key)){
                                VAS_PLATFORMID = value;
                            }else if("VAS_SUBPLATFORMID".equalsIgnoreCase(key)){
                                VAS_SUBPLATFORMID = value;
                            }else if("VAS_DEBUG".equalsIgnoreCase(key)){
                                VAS_DEBUG = value;
                            }
                            else
                            {
                                configMap.put(key, value);
                            }
                            configMap.put(key, value);
                            VASLogUtil.d("Load sdk_config: key:" + key + ", value:" + value);

                        }

                        // 加载Application
                        if ("application".equals(tag))
                        {
                            String application = parser.getAttributeValue(null, "name");
                            applicationList.add(application);
                            VASLogUtil.d("Load Application: name:" + application);
                        }

                        // 加载插件
                        if ("plugin".equals(tag))
                        {
                            String plugin = parser.getAttributeValue(null, "name");
                            int pluginType = Integer.parseInt(parser.getAttributeValue(null, "type"));
                            pluginMap.put(pluginType, plugin);
                            VASLogUtil.d("Load Plugin: " + pluginType + "; name:" + plugin);
//                            Log.d("xxxx", "Load Plugin: " + pluginType + "; name:" + plugin);
                        }
                }
                eventType = parser.next();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public List<String> getApplicationList()
    {
        return applicationList;
    }

    public String getConfig(String key)
    {
        if (configMap != null)
        {
            return configMap.get(key);
        }
        else
        {
            return null;
        }
    }

    public void setConfig(String key, String value)
    {
        if (configMap != null)
        {
            configMap.put(key, value);
        }
    }

    public String getPlugin(Integer pluginType)
    {
        if (pluginMap != null)
        {
            return pluginMap.get(pluginType);
        }
        else
        {
            return null;
        }
    }

    public void setDebug(boolean debug)
    {
        this.mDebug = debug;
    }

    public boolean getDebug()
    {
        return mDebug;
    }

}
