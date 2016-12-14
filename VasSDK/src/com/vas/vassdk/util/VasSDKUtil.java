package com.vas.vassdk.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class VasSDKUtil {
	
	/***
	 * 获取assets目录下文件内容
	 * @param context
	 * @param assetsFile
	 * @return
	 */
	public static String getAssetConfigs(Context context, String assetsFile){
		InputStreamReader reader = null;
		BufferedReader br = null;
		try {
			reader = new InputStreamReader(context.getAssets().open(assetsFile));
			br = new BufferedReader(reader);
			
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = br.readLine())!= null){
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(br != null){
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
			if(reader != null){
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	
	@SuppressLint("TrulyRandom")
    public static String getAssetDesConfigs(Context context,String assetsFile){
	    InputStreamReader reader = null;
        BufferedReader br = null;
        ObjectInputStream b = null;
        try {
            
            //获取key
            b=new ObjectInputStream(context.getAssets().open("vas.dat"));
            Key key=(Key)b.readObject();
           
            // 加解密要用Cipher来实现
            Cipher cipher = Cipher.getInstance("DESede");
            // 输入流  
            cipher.init(Cipher.DECRYPT_MODE, key);
            CipherInputStream cipherInputStream = new CipherInputStream(  
                    new BufferedInputStream(context.getAssets().open(assetsFile)), cipher);
            reader = new InputStreamReader(cipherInputStream);
            br = new BufferedReader(reader);
            
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine())!= null){
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            
            try
            {
                if (b != null)
                {
                    b.close();
                    b = null;
                }

                if (br != null)
                {
                    br.close();
                    br = null;

                }

                if (reader != null)
                {
                    reader.close();
                    reader = null;
                }
                
            }
            catch (IOException e2)
            {
                e2.printStackTrace();
            }
            
        }
        return null;
	}
	
	public static String getAppName(Context context){    
		String appName = null;
		try {
			String packname = context.getPackageName();
			PackageManager pm = context.getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);     
			appName = info.loadLabel(pm).toString();
		} catch(Exception e) {    
			e.printStackTrace();    
		}
		return appName;
	}
	
	private final static String DRAWABLE = "drawable";
	private final static String LAYOUT = "layout";
	private final static String ID = "id";
	private final static String COLOR = "color";
	private final static String DIMEN = "dimen";
	private final static String STRING = "string";
	private final static String STYLE = "style";
	private final static String ANIM = "anim";
	
	/**
     * 按资源名称查找资源名称
     * @param context
     * @param packageName
     * @param resourcesName
     * @return
     */
    private static int getResourcesIdByName(Context context, String packageName, String resourcesName) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(resourcesName, packageName, context.getPackageName());
        if(id == 0) {
            VASLogUtil.e("fail to load resource:" + resourcesName);
        }
        return id;
    }
    
    /**
     * 根据动画文件的名称 返回对应的id
     * @param context
     * @param animName
     * @return
     */
    public static int getAnimId(Context context, String animName) {
    	return getResourcesIdByName(context, ANIM, animName);
    }
    
    /**
     * 获取drawable资源的id
     * @param context
     * @param resourcesName
     * @return
     */
    public static int getDrawableId(Context context, String drawableName) {
        return getResourcesIdByName(context, DRAWABLE, drawableName);
    }
    
    /**
     * 获取layout资源的id
     * 
     * @param context
     * @param layoutName
     * @return
     */
    public static int getLayoutId(Context context, String layoutName) {
        return getResourcesIdByName(context, LAYOUT, layoutName);
    }

    /**
     * 获取view的id
     * 
     * @param context
     * @param viewId
     * @return
     */
    public static int getViewID(Context context, String viewId) {
        return getResourcesIdByName(context, ID, viewId);
    }

    /**
     * 获取color资源的id
     * 
     * @param context
     * @param colorName
     * @return
     */
    public static int getColorId(Context context, String colorName) {
        return getResourcesIdByName(context, COLOR, colorName);
    }

    /**
     * 获取dimen资源的id
     * 
     * @param context
     * @param dimenName
     * @return
     */
    public static int getDimenId(Context context, String dimenName) {
        return getResourcesIdByName(context, DIMEN, dimenName);
    }

    /**
     * 获取String资源的id
     * 
     * @param context
     * @param stringName
     * @return
     */
    public static int getStringId(Context context, String stringName) {
        return getResourcesIdByName(context, STRING, stringName);
    }

    public static String getStringFormResouse(Context context, String stringName) {
        return context.getResources().getString(getResourcesIdByName(context, STRING, stringName));
    }

    /**
     * 获取style资源id
     * 
     * @param context
     * @param styleName
     * @return
     */
    public static int getStyleId(Context context, String styleName) {
        return getResourcesIdByName(context, STYLE, styleName);
    }
    
    public static String getMetaData(Context context, String key) {
    	try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Object value = info.metaData.get(key);
			if(value != null) {
				return value.toString();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static JSONObject toJSON(String data) {
        JSONObject result = null;
        try {
            result = new JSONObject(data);
        } catch (Exception ex) {
            VASLogUtil.e("to json fail 【" + data + "】", ex);
        }
        return result;
    }
    
}
