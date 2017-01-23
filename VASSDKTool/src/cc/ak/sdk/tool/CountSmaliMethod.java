package cc.ak.sdk.tool;

import java.io.File;

public class CountSmaliMethod
{

    
    /**
     * 计算smali文件方法数
     */
    public static int countSmailMethods(String smailPath,String allMethods[]){
        int num = 0;
        File smaliFile = new File(smailPath);
        if(!smaliFile.exists()){
            return num;
        }
        
        
        return num;
    }
    
    
    
}
