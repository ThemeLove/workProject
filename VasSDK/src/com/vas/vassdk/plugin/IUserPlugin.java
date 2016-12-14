package com.vas.vassdk.plugin;



import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;

public abstract interface IUserPlugin
{
  
  public static final int PLUGIN_TYPE = VasSDKConfig.PLUGIN_TYPE_USER;
    
  public abstract void login();
  
  public abstract void logout();
  
  public abstract VasUserInfo getUserInfo();
  
  public abstract void setGameRoleInfo(VasRoleInfo paramGameRoleInfo, boolean paramBoolean);
}
