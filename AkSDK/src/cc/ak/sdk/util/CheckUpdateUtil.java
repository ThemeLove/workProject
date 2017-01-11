
package cc.ak.sdk.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class CheckUpdateUtil {

    public static boolean checkVersion(String version, String newVersion) {
        if (version == null || newVersion == null)
            return false;
        System.out.println(version + "m" + newVersion);
        String[] subVersion = version.split("\\.");
        String[] subNewVersion = newVersion.split("\\.");
        int length = subVersion.length > subNewVersion.length ? subVersion.length
                : subNewVersion.length;

        for (int i = 0; i < length; i++) {
            int isub = 0;
            int isubnew = 0;

            try {
                if (i < subNewVersion.length) {
                    isubnew = Integer.parseInt(subNewVersion[i]);
                }
                if (i < subVersion.length) {
                    isub = Integer.parseInt(subVersion[i]);
                }

                if (isubnew > isub) {
                    return true;
                } else if (isubnew < isub) {
                    return false;
                }
            } catch (Exception ex) {
                // ignore.
                return false;
            }
        }
        return false;
    }

    public static String getPackageVersion(Context context) throws NameNotFoundException {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        String version = info.versionName;
        return version;
    }
}
